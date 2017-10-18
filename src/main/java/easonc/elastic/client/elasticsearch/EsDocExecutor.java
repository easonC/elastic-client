package easonc.elastic.client.elasticsearch;

import easonc.elastic.client.elasticsearch.base.DocExecutor;
import easonc.elastic.client.elasticsearch.base.ESBaseClient;
import easonc.elastic.client.elasticsearch.base.EsBaseConnectSetting;
import easonc.elastic.client.elasticsearch.base.ExecutorRetry;
import easonc.elastic.client.elasticsearch.callback.BulkDeleteCallable;
import easonc.elastic.client.elasticsearch.callback.BulkDocCallable;
import easonc.elastic.client.elasticsearch.callback.IndexParentFunction;
import easonc.elastic.client.elasticsearch.callback.IndexRoutingFunction;
import easonc.elastic.client.elasticsearch.client.NativeEsClient;
import easonc.elastic.client.elasticsearch.rawclient.TransportClient;
import easonc.elastic.client.model.BulkIndexModel;
import easonc.elastic.client.model.ParallelExecuteResult;
import easonc.elastic.client.util.CollectionsUtil;
import easonc.elastic.client.util.Parallel;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Es operation high-level api
 * Created by zhuqi on 2016/12/13.
 */
public class EsDocExecutor implements DocExecutor, AutoCloseable {

    ExecutorService pool;
    ElasticsearchClient client = null;

    String clusterName;
    String ipAddr;
    int port;

    int retryTimes = 3;

    ExecutorRetry retry;

    private volatile boolean inited = false;

    public EsDocExecutor(String clusterName, String ipAddr, int port) {
        this.clusterName = clusterName;
        this.ipAddr = ipAddr;
        this.port = port;
        this.pool = Executors.newFixedThreadPool(5);
        this.retry = new EsExecutorRetry(pool, retryTimes);
    }

    public EsDocExecutor(String clusterName, String ipAddr, int port, ExecutorRetry retry) {
        this.clusterName = clusterName;
        this.ipAddr = ipAddr;
        this.port = port;
        this.pool = Executors.newFixedThreadPool(5);
        this.retry = retry;
    }

    @Override
    public synchronized void init() {
        if (!inited) {
            EsBaseConnectSetting settings = new EsConnectSetting(clusterName, ipAddr, port);
            ESBaseClient baseClient = new TransportClient(settings);
            client = new NativeEsClient(baseClient);
            inited = true;
        }
    }

    @Override
    public <T> boolean mappingAndIndice(String indexName, Class<T> clazz) {
        CreateIndexResponse response = client.prepareCreate(indexName, clazz);
        return response.isAcknowledged();
    }

    @Override
    public <T> boolean mappingAndIndice(String indexName, Class<T> clazz, int shards, int replicas) {
        CreateIndexResponse response = client.prepareCreate(indexName, clazz, shards, replicas);
        return response.isAcknowledged();
    }

    @Override
    public <TParent, TChild> boolean mappingAndIndiceWithChild(String indexName, Class<TParent> parentClazz, Class<TChild> childClazz) {
        CreateIndexResponse response = client.prepareCreateWithChild(indexName, parentClazz, childClazz);
        return response.isAcknowledged();
    }

    @Override
    public <TParent, TChild> boolean mappingAndIndiceWithChild(String indexName, Class<TParent> parentClazz, Class<TChild> childClazz, int shards, int replicas) {
        CreateIndexResponse response = client.prepareCreateWithChild(indexName, parentClazz, childClazz, shards, replicas);
        return response.isAcknowledged();
    }

    @Override
    public <T> boolean pushDoc(final String indexName, final String typeName, final T doc) {
        boolean isSuccess;

        IndexResponse response = client.index(indexName, typeName, doc);
        isSuccess = response.isCreated();

        if (!isSuccess) {
            return retry.retry(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    IndexResponse response = client.index(indexName, typeName, doc);
                    return response.isCreated();
                }
            });
        }
        return isSuccess;
    }

    @Override
    public <T> boolean bulkDoc(String indexName, String typeName, List<T> docs) {
        List<List<T>> docsLists = CollectionsUtil.spiltList2Lists(docs, 500);
        List<Callable<Boolean>> callList = new ArrayList<>(docsLists.size());
        for (List<T> list : docsLists) {
            callList.add(new BulkDocCallable<T>(client, indexName, typeName, list));
        }
        ParallelExecuteResult res = Parallel.foreach(callList, pool);

        if (!res.isSuccess()) {
            return retry.retry(res.getFailCall());
        }
        return res.isSuccess();
    }

    @Override
    public <T> boolean bulkDoc(String indexName, String typeName, IndexRoutingFunction<T> routingFunc, List<T> docs) {

        List<List<T>> docsLists = CollectionsUtil.spiltList2Lists(docs, 500);
        List<Callable<Boolean>> callList = new ArrayList<>(docsLists.size());
        for (List<T> list : docsLists) {
            callList.add(new BulkDocCallable<T>(client, indexName, typeName, list, routingFunc));
        }
        ParallelExecuteResult res = Parallel.foreach(callList, pool);

        if (!res.isSuccess()) {
            return retry.retry(res.getFailCall());
        }
        return res.isSuccess();

    }

    @Override
    public <T> boolean bulkDoc(String indexName, String typeName, IndexRoutingFunction<T> routingFunc, IndexParentFunction<T> parentFunc, List<T> docs) {

        if (docs == null || docs.size() == 0)
            return true;
        List<List<T>> docsLists = CollectionsUtil.spiltList2Lists(docs, 500);
        List<Callable<Boolean>> callList = new ArrayList<>(docsLists.size());
        for (List<T> list : docsLists) {
            callList.add(new BulkDocCallable<T>(client, indexName, typeName, list, parentFunc, routingFunc));
        }
        ParallelExecuteResult res = Parallel.foreach(callList, pool);

        if (!res.isSuccess()) {
            return retry.retry(res.getFailCall());
        }
        return res.isSuccess();
    }

    @Override
    public <T> boolean bulkDocByModels(String indexName, String typeName, List<BulkIndexModel<T>> bulkIndexModels) {

        if (bulkIndexModels == null || bulkIndexModels.size() == 0)
            return true;
        List<List<BulkIndexModel<T>>> docsLists = CollectionsUtil.spiltList2Lists(bulkIndexModels, 500);
        List<Callable<Boolean>> callList = new ArrayList<>(docsLists.size());
        for (List<BulkIndexModel<T>> list : docsLists) {
            callList.add(new BulkDocCallable<T>(list, client, indexName, typeName));
        }
        ParallelExecuteResult res = Parallel.foreach(callList, pool);

        if (!res.isSuccess()) {
            return retry.retry(res.getFailCall());
        }
        return res.isSuccess();
    }

    @Override
    public boolean deleteDoc(String indexName, String typeName, List<String> ids) {
        List<List<String>> docsLists = CollectionsUtil.spiltList2Lists(ids, 500);
        List<Callable<Boolean>> callList = new ArrayList<>(docsLists.size());
        for (List<String> list : docsLists) {
            callList.add(new BulkDeleteCallable(client, indexName, typeName, list));
        }
        ParallelExecuteResult res = Parallel.foreach(callList, pool);

        if (!res.isSuccess()) {
            return retry.retry(res.getFailCall());
        }
        return res.isSuccess();
    }

    @Override
    public SearchResponse seachDoc(SearchRequestBuilder searchRequestBuilder) {
        return client.prepareSearch(searchRequestBuilder);
    }

    @Override
    public boolean refresh(String... indexNames) {

        RefreshResponse response = client.refresh(indexNames);

        int fails = response.getFailedShards();

        return fails == 0;
    }

    @Override
    public ElasticsearchClient getRawClient() {
        return this.client;
    }

    @Override
    public void close() throws Exception {
        if (client != null)
            client.close();
    }
}
