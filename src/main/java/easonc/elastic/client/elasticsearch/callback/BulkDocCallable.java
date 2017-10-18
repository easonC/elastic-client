package easonc.elastic.client.elasticsearch.callback;

import easonc.elastic.client.elasticsearch.ElasticsearchClient;
import easonc.elastic.client.model.BulkIndexModel;
import org.elasticsearch.action.bulk.BulkResponse;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by zhuqi on 2016/12/13.
 */
public class BulkDocCallable<T> implements Callable<Boolean> {
    private ElasticsearchClient client = null;
    private String indexName;
    private String typeName;
    private List<T> data;
    private IndexParentFunction<T> parentFunction = null;
    private IndexRoutingFunction<T> routingFunction = null;

    public List<BulkIndexModel<T>> bulkIndexModels = null;

    public List<T> getData() {
        return data;
    }

    public BulkDocCallable(ElasticsearchClient client, String indexName, String typeName, List<T> data) {
        this.client = client;
        this.indexName = indexName;
        this.typeName = typeName;
        this.data = data;
    }

    public BulkDocCallable(ElasticsearchClient client, String indexName, String typeName, List<T> data, IndexParentFunction<T> parentFunction) {
        this(client, indexName, typeName, data);
        this.parentFunction = parentFunction;
    }

    public BulkDocCallable(ElasticsearchClient client, String indexName, String typeName, List<T> data, IndexRoutingFunction<T> routingFunction) {
        this(client, indexName, typeName, data);
        this.routingFunction = routingFunction;
    }

    public BulkDocCallable(ElasticsearchClient client, String indexName, String typeName, List<T> data, IndexParentFunction<T> parentFunction, IndexRoutingFunction<T> routingFunction) {
        this(client, indexName, typeName, data, parentFunction);
        this.routingFunction = routingFunction;
    }

    public BulkDocCallable(List<BulkIndexModel<T>> bulkIndexModels, ElasticsearchClient client, String indexName, String typeName) {
        this.client = client;
        this.indexName = indexName;
        this.typeName = typeName;
        this.bulkIndexModels = bulkIndexModels;
    }

    @Override
    public Boolean call() throws Exception {
        if (bulkIndexModels != null) {
            BulkResponse res = client.bulkIndexWithModels(indexName, typeName, bulkIndexModels);
            if (res == null)
                return false;
            return !res.hasFailures();
        } else {
            BulkResponse res = client.bulkIndexWithRoutings(indexName, typeName, routingFunction, parentFunction, data);
            if (res == null)
                return false;
            return !res.hasFailures();
        }
    }
}
