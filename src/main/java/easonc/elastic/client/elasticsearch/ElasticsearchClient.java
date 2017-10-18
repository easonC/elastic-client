package easonc.elastic.client.elasticsearch;

import easonc.elastic.client.elasticsearch.callback.IndexParentFunction;
import easonc.elastic.client.elasticsearch.callback.IndexRoutingFunction;
import easonc.elastic.client.model.BulkIndexModel;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.optimize.OptimizeResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;

import java.util.HashMap;
import java.util.List;

/**
 * Created by zhuqi on 2016/11/17.
 */
public interface ElasticsearchClient {

    /**
     * 删除索引
     *
     * @param indexName
     * @return
     */
    boolean indexDelete(String indexName);

    /**
     * 索引是否存在
     *
     * @param indexName
     * @return
     */
    boolean indexExists(String indexName);

    /**
     * 类型是否存在
     *
     * @param indexName
     * @param typeName
     * @return
     */
    boolean typeExists(String indexName, String typeName);

    /**
     * 创建索引，内部自动mapping
     *
     * @param indexName
     * @param clazz
     * @param <T>
     * @return
     */
    <T> CreateIndexResponse prepareCreate(String indexName, Class<T> clazz);


    /**
     * 创建索引，内部自动mapping
     *
     * @param indexName
     * @param clazz
     * @param shards
     * @param replicas
     * @param <T>
     * @return
     */
    <T> CreateIndexResponse prepareCreate(String indexName, Class<T> clazz, int shards, int replicas);


    /**
     * 创建索引(parent-childs)，内部自动mapping
     *
     * @param indexName
     * @param parentClazz
     * @param childClazz
     * @param <TParent>
     * @param <TChild>
     * @return
     */
    <TParent, TChild> CreateIndexResponse prepareCreateWithChild(String indexName, Class<TParent> parentClazz, Class<TChild> childClazz);


    /**
     * 创建索引(parent-childs)，内部自动mapping
     *
     * @param indexName
     * @param parentClazz
     * @param childClazz
     * @param shards
     * @param replicas
     * @param <TParent>
     * @param <TChild>
     * @return
     */
    <TParent, TChild> CreateIndexResponse prepareCreateWithChild(String indexName, Class<TParent> parentClazz, Class<TChild> childClazz, int shards, int replicas);

    /**
     * 写入mapping，upsert操作
     *
     * @param indexName
     * @param typeName
     * @param clazz
     * @param <T>
     * @return
     */
    <T> PutMappingResponse preparePutMapping(String indexName, String typeName, Class<T> clazz);

    /**
     * 建立索引
     *
     * @param indexName 索引名
     * @param typeName  索引类型
     * @param docEntity 索引对象
     * @param <T>       类型T
     * @return
     */
    <T> IndexResponse index(String indexName, String typeName, T docEntity);

    /**
     * 建立索引
     *
     * @param indexName 索引名
     * @param typeName  索引类型
     * @param parent    父文档id
     * @param docEntity 索引对象
     * @param <T>       类型T
     * @return
     */
    <T> IndexResponse index(String indexName, String typeName, String parent, T docEntity);


    <T> IndexResponse indexWithRouting(String indexName, String typeName, String routing, T docEntity);

    <T> IndexResponse indexWithRouting(String indexName, String typeName, String routing, String parent, T docEntity);

    /**
     * id获取doc
     *
     * @param indexName 索引名
     * @param typeName  索引类型
     * @param id        索引id
     * @param threaded  是否独立线程， 一般false
     * @return
     */
    GetResponse prepareGet(String indexName, String typeName, String id, boolean threaded);

    /**
     * 删除文档
     *
     * @param indexName 索引名
     * @param typeName  索引类型
     * @param id        索引id
     * @param threaded  是否独立线程， 一般false
     * @return
     */
    DeleteResponse prepareDelete(String indexName, String typeName, String id, boolean threaded);

    /**
     * 更新文档
     *
     * @param indexName 索引名
     * @param typeName  索引类型
     * @param id        索引id
     * @param source    更新的文档内容
     * @return
     */
    UpdateResponse prepareUpdate(String indexName, String typeName, String id, String source);

    /**
     * 更新文档（自定义请求）
     *
     * @param updateRequest
     * @return
     */
    UpdateResponse prepareUpdate(UpdateRequest updateRequest);

    /**
     * 批量索引
     *
     * @param indexName 索引名
     * @param typeName  索引类型
     * @param docs      文档集
     * @param <T>
     * @return
     */
    <T> BulkResponse bulkIndex(String indexName, String typeName, List<T> docs);

    <T> BulkResponse bulkIndex(String indexName, String typeName, String parent, List<T> docs);

    <T> BulkResponse bulkIndexWithRouting(String indexName, String typeName, String routing, List<T> docs);

    <T> BulkResponse bulkIndexWithRouting(String indexName, String typeName, String routing, String parent, List<T> docs);

    <T> BulkResponse bulkIndexWithRoutings(String indexName, String typeName, IndexRoutingFunction<T> routing, List<T> docs);

    <T> BulkResponse bulkIndexWithRoutings(String indexName, String typeName, IndexRoutingFunction<T> routing, IndexParentFunction<T> parent, List<T> docs);

    <T> BulkResponse bulkIndexWithModels(String indexName, String typeName, List<BulkIndexModel<T>> bulkIndexModels);
    /**
     * 批量更新
     *
     * @param indexName 索引名
     * @param typeName  索引类型
     * @param sources
     * @return
     */
    BulkResponse bulkUpdate(String indexName, String typeName, HashMap<String, String> sources);

    /**
     * 批量删除
     *
     * @param indexName 索引名
     * @param typeName  索引类型
     * @param ids
     * @return
     */
    BulkResponse bulkDelete(String indexName, String typeName, List<String> ids);

    /**
     * @param searchParams
     * @return
     */
    SearchResponse prepareSearch(SearchRequestParams searchParams);


    SearchResponse prepareSearch(SearchRequestBuilder searchRequestBuilder);

    RefreshResponse refresh(String... indexName);

    OptimizeResponse optimize(String... indexName);

    ClusterHealthResponse checkClusterHealth(String... indexName);

    boolean clusterHealthCheck(String... indexName);

    /**
     * 获得原始client
     *
     * @return
     */
    Client getRawClient();


    void close();
}
