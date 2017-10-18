package easonc.elastic.client.elasticsearch.base;

import easonc.elastic.client.elasticsearch.ElasticsearchClient;
import easonc.elastic.client.elasticsearch.callback.IndexParentFunction;
import easonc.elastic.client.elasticsearch.callback.IndexRoutingFunction;
import easonc.elastic.client.model.BulkIndexModel;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;

import java.util.List;

/**
 * Created by zhuqi on 2016/12/13.
 */
public interface DocExecutor {

    void init();

    <T> boolean mappingAndIndice(String indexName, Class<T> clazz);

    <T> boolean mappingAndIndice(String indexName, Class<T> clazz, int shards, int replicas);

    <TParent, TChild> boolean mappingAndIndiceWithChild(String indexName, Class<TParent> parentClazz, Class<TChild> childClazz);

    <TParent, TChild> boolean mappingAndIndiceWithChild(String indexName, Class<TParent> parentClazz, Class<TChild> childClazz, int shards, int replicas);

    <T> boolean pushDoc(String indexName, String typeName, T doc);

    /**
     * 批量写入es，内部会并行push，无需考虑docs大小，但需考虑内存是否足够。
     * 内部为500一批
     * 内部会重试
     * 如果有失败会返回false，但不代表全部失败
     *
     * @param indexName
     * @param typeName
     * @param docs
     * @param <T>
     * @return
     */
    <T> boolean bulkDoc(String indexName, String typeName, List<T> docs);

    <T> boolean bulkDoc(String indexName, String typeName, IndexRoutingFunction<T> routingFunc, List<T> docs);
    <T> boolean bulkDoc(String indexName, String typeName, IndexRoutingFunction<T> routingFunc, IndexParentFunction<T> parentFunc, List<T> docs);

    <T> boolean bulkDocByModels(String indexName, String typeName, List<BulkIndexModel<T>> bulkIndexModels);
    boolean deleteDoc(String indexName, String typeName, List<String> ids);
    SearchResponse seachDoc(SearchRequestBuilder searchRequestBuilder);

    /**
     * 刷新索引
     *
     * @param indexNames
     * @return
     */
    boolean refresh(String... indexNames);

    ElasticsearchClient getRawClient();
}
