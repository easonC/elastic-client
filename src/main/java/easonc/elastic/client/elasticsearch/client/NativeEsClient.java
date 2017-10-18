package easonc.elastic.client.elasticsearch.client;


import easonc.elastic.client.exceptions.EntityReflectionException;
import easonc.elastic.client.JsonSerializer;
import easonc.elastic.client.Strings;
import easonc.elastic.client.elasticsearch.ElasticsearchClient;
import easonc.elastic.client.elasticsearch.SearchRequestParams;
import easonc.elastic.client.elasticsearch.base.ESBaseClient;
import easonc.elastic.client.elasticsearch.callback.IndexParentFunction;
import easonc.elastic.client.elasticsearch.callback.IndexRoutingFunction;
import easonc.elastic.client.elasticsearch.mapping.DocMapper;
import easonc.elastic.client.elasticsearch.mapping.ElasticIndicesDocMapper;
import easonc.elastic.client.model.BulkIndexModel;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.action.admin.cluster.health.ClusterIndexHealth;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.optimize.OptimizeResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.sort.SortBuilders;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class NativeEsClient implements ElasticsearchClient, AutoCloseable {

    private Client client = null;
    private ESBaseClient baseClient;
    private static Logger logger = LogManager.getLogger(NativeEsClient.class);

    public NativeEsClient(ESBaseClient client) {
        this.baseClient = client;
        client.init();
        this.client = baseClient.client();
    }

    @Override
    public boolean indexDelete(String indexName) {
        return client.admin().indices().prepareDelete(indexName).get().isAcknowledged();
    }

    @Override
    public boolean indexExists(String indexName) {
        return client.admin().indices().prepareExists(indexName).get().isExists();
    }

    @Override
    public boolean typeExists(String indexName, String typeName) {
        return client.admin().indices().prepareTypesExists(indexName).setTypes(typeName).get().isExists();
    }

    @Override
    public <T> CreateIndexResponse prepareCreate(String indexName, Class<T> clazz) {
//        try {
//            DocMapper mapper = new ElasticIndicesDocMapper<>(clazz);
//            XContentBuilder mapping = jsonBuilder();
//            mapper.map(mapping);
//            System.out.println(mapping.string());
//            return client.admin().indices().prepareCreate(indexName).setTimeout(TimeValue.timeValueSeconds(300)).setSource(mapping).get();
//        } catch (EntityReflectionException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            logger.error(e);
//        }
//        return null;
        return prepareCreate(indexName, clazz, 0, 0);
    }

    @Override
    public <T> CreateIndexResponse prepareCreate(String indexName, Class<T> clazz, int shards, int replicas) {
        try {
            ElasticIndicesDocMapper mapper = new ElasticIndicesDocMapper<>(clazz);
            XContentBuilder mapping = jsonBuilder();
            mapper.map(mapping);
            System.out.println(mapping.string());

            CreateIndexRequestBuilder requestBuilder = client.admin().indices().prepareCreate(indexName);
            if (shards > 0 || replicas > 0) {
                ImmutableSettings.Builder settingsBuilder = ImmutableSettings.settingsBuilder();
                if (shards > 0)
                    settingsBuilder.put("number_of_shards", shards);
                if (replicas > 0)
                    settingsBuilder.put("number_of_replicas", replicas);
                requestBuilder.setSettings(settingsBuilder.build());
            }

            return requestBuilder.addMapping(mapper.getIndexType(), mapping).get();
        } catch (EntityReflectionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            logger.error(e);
        }
        return null;
    }

    @Override
    public <TParent, TChild> CreateIndexResponse prepareCreateWithChild(String indexName, Class<TParent> parentClazz, Class<TChild> childClazz) {
        return prepareCreateWithChild(indexName, parentClazz, childClazz, 0, 0);
//        try {
//            ElasticIndicesDocMapper mapper = new ElasticIndicesDocMapper<TParent>(parentClazz);
//            XContentBuilder mapping = jsonBuilder();
//            mapper.map(mapping);
//            System.out.println(mapping.string());
//            CreateIndexResponse response = client.admin().indices().prepareCreate(indexName).setSource(mapping).setTimeout(TimeValue.timeValueSeconds(300)).get();
//            if (response.isAcknowledged()) {
//                String ptype = mapper.getIndexType();
//                if (!Strings.isWhitespaceOrNull(ptype)) {
//                    mapper = new ElasticIndicesDocMapper(childClazz, ptype);
//                    mapping = jsonBuilder();
//                    mapper.map(mapping);
//                    System.out.println(mapping.string());
//
//                    PutMappingResponse map_response = client.admin().indices().preparePutMapping(indexName).setType(mapper.getIndexType()).setSource(
//                            mapping
//                    ).get();
//
//                    return new CreateIndexAsMappingResponse(map_response.isAcknowledged());
//                }
//            }
//        } catch (EntityReflectionException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return CreateIndexAsMappingResponse.createFailedResponse();
    }

    @Override
    public <TParent, TChild> CreateIndexResponse prepareCreateWithChild(String indexName, Class<TParent> parentClazz, Class<TChild> childClazz, int shards, int replicas) {
        try {
            ElasticIndicesDocMapper mapper = new ElasticIndicesDocMapper<TParent>(parentClazz);
            XContentBuilder mapping = jsonBuilder();
            mapper.map(mapping);
            System.out.println(mapping.string());
            ImmutableSettings.Builder settingsBuilder = null;
            if (shards > 0 || replicas > 0) {
                settingsBuilder = ImmutableSettings.settingsBuilder();
                if (shards > 0)
                    settingsBuilder.put("number_of_shards", shards);
                if (replicas > 0)
                    settingsBuilder.put("number_of_replicas", replicas);

            }
            CreateIndexRequestBuilder requestBuilder = client.admin().indices().prepareCreate(indexName);
            if (shards > 0 || replicas > 0)
                requestBuilder.setSettings(settingsBuilder.build());
            // CreateIndexResponse response = requestBuilder.setSource(mapping).setTimeout(TimeValue.timeValueSeconds(300)).get();
            String ptype = mapper.getIndexType();
            //child mapper
            ElasticIndicesDocMapper cmapper = new ElasticIndicesDocMapper(childClazz, ptype);
            XContentBuilder cmapping = jsonBuilder();
            cmapper.map(cmapping);

            CreateIndexResponse response = requestBuilder.addMapping(ptype, mapping)
                    .addMapping(cmapper.getIndexType(), cmapping).setTimeout(TimeValue.timeValueSeconds(300)).get();
            ;
            return response;
//            if (response.isAcknowledged()) {
//                String ptype = mapper.getIndexType();
//                if (!Strings.isWhitespaceOrNull(ptype)) {
//                    mapper = new ElasticIndicesDocMapper(childClazz, ptype);
//                    mapping = jsonBuilder();
//                    mapper.map(mapping);
//                    System.out.println(mapping.string());
//                    PutMappingResponse map_response = client.admin().indices().preparePutMapping(indexName).setType(mapper.getIndexType()).setSource(
//                            mapping
//                    ).get();
//
//                    return new CreateIndexAsMappingResponse(map_response.isAcknowledged());
//                }
//            }
        } catch (EntityReflectionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> PutMappingResponse preparePutMapping(String indexName, String typeName, Class<T> clazz) {

        try {
            DocMapper mapper = new ElasticIndicesDocMapper<>(clazz);

            XContentBuilder mapping = jsonBuilder();

            mapper.map(mapping);
            PutMappingResponse response = client.admin().indices().preparePutMapping(indexName).setType(typeName).setSource(
                    mapping
            ).get();
            return response;
        } catch (EntityReflectionException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }
        return null;
    }

    @Override
    public <T> IndexResponse index(String indexName, String typeName, T docEntity) {
        return index(indexName, typeName, null, docEntity);
    }

    @Override
    public <T> IndexResponse index(String indexName, String typeName, String parent, T docEntity) {
        try {
            String string = JsonSerializer.toJson(docEntity);
            IndexRequestBuilder requestBuilder = client.prepareIndex(indexName, typeName).setSource(string);
            if (!Strings.isWhitespaceOrNull(parent)) {
                requestBuilder.setParent(parent);
            }
            IndexResponse rs = requestBuilder.execute().actionGet();
            return rs;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error(e);
        }
        return null;
    }

    @Override
    public <T> IndexResponse indexWithRouting(String indexName, String typeName, String routing, T docEntity) {

        return indexWithRouting(indexName, typeName, routing, null, docEntity);
    }

    @Override
    public <T> IndexResponse indexWithRouting(String indexName, String typeName, String routing, String parent, T docEntity) {

        String string = JsonSerializer.toJson(docEntity);

        IndexRequestBuilder requestBuilder = client.prepareIndex(indexName, typeName).setSource(string);
        if (!Strings.isWhitespaceOrNull(routing)) {
            requestBuilder.setRouting(routing);
        }
        if (!Strings.isWhitespaceOrNull(parent)) {
            requestBuilder.setParent(parent);
        }

        return requestBuilder.execute().actionGet();
    }


    @Override
    public GetResponse prepareGet(String indexName, String typeName, String id, boolean threaded) {
        GetResponse response = client.prepareGet(indexName, typeName, id)
                .setOperationThreaded(threaded)
                .execute()
                .actionGet();
        return response;
    }

    @Override
    public DeleteResponse prepareDelete(String indexName, String typeName, String id, boolean threaded) {
        DeleteResponse response = client.prepareDelete(indexName, typeName, id)
                .setOperationThreaded(threaded)
                .execute()
                .actionGet();
        return response;
    }

    @Override
    public UpdateResponse prepareUpdate(String indexName, String typeName, String id, String source) {
        UpdateResponse response = client.prepareUpdate(indexName, typeName, id)
                .setDoc(source).get();
        return response;
    }

    @Override
    public UpdateResponse prepareUpdate(UpdateRequest updateRequest) {
        try {
            return client.update(updateRequest).get();
        } catch (InterruptedException e) {
            logger.error(e);
        } catch (ExecutionException e) {
            logger.error(e);
        }
        return null;
    }

    @Override
    public <T> BulkResponse bulkIndex(String indexName, String typeName, List<T> docs) {
        return bulkIndexWithRouting(indexName, typeName, null, null, docs);
    }

    @Override
    public <T> BulkResponse bulkIndex(String indexName, String typeName, String parent, List<T> docs) {

        return bulkIndexWithRouting(indexName, typeName, null, parent, docs);
    }

    @Override
    public <T> BulkResponse bulkIndexWithRouting(String indexName, String typeName, String routing, List<T> docs) {
        return bulkIndexWithRouting(indexName, typeName, routing, null, docs);
    }

    @Override
    public <T> BulkResponse bulkIndexWithRouting(String indexName, String typeName, String routing, String parent, List<T> docs) {
        if (docs == null || docs.size() == 0) {
            return null;
        }
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        IndexRequestBuilder indexRequest;
        String string;
        for (T doc : docs) {

            string = JsonSerializer.toJson(doc);
            indexRequest = client.prepareIndex(indexName, typeName).setSource(string);

            if (!Strings.isWhitespaceOrNull(routing)) {
                indexRequest.setRouting(routing);
            }
            if (!Strings.isWhitespaceOrNull(parent)) {
                indexRequest.setParent(parent);
            }

            bulkRequest.add(indexRequest);
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        return bulkResponse;
    }

    @Override
    public <T> BulkResponse bulkIndexWithRoutings(String indexName, String typeName, IndexRoutingFunction<T> routing, List<T> docs) {
        return bulkIndexWithRoutings(indexName, typeName, routing, null, docs);
    }

    @Override
    public <T> BulkResponse bulkIndexWithRoutings(String indexName, String typeName, IndexRoutingFunction<T> routing, IndexParentFunction<T> parent, List<T> docs) {
        if (docs == null || docs.size() == 0) {
            return null;
        }
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        IndexRequestBuilder indexRequest;
        String string;
        for (T doc : docs) {

            string = JsonSerializer.toJson(doc);
            indexRequest = client.prepareIndex(indexName, typeName).setSource(string);

            if (routing != null) {
                indexRequest.setRouting(routing.routing(doc));
            }
            if (parent != null) {
                indexRequest.setParent(parent.parent(doc));
            }

            bulkRequest.add(indexRequest);
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        return bulkResponse;
    }

    @Override
    public <T> BulkResponse bulkIndexWithModels(String indexName, String typeName, List<BulkIndexModel<T>> bulkIndexModels) {
        if (bulkIndexModels == null || bulkIndexModels.size() == 0) {
            return null;
        }
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        IndexRequestBuilder indexRequest;
        String string;
        for (BulkIndexModel<T> bulkModel : bulkIndexModels) {

            string = JsonSerializer.toJson(bulkModel.getDoc());
            indexRequest = client.prepareIndex(indexName, typeName).setSource(string);

            if (!Strings.isWhitespaceOrNull(bulkModel.getRouting())) {
                indexRequest.setRouting(bulkModel.getRouting());
            }
            if (!Strings.isWhitespaceOrNull(bulkModel.getParent())) {
                indexRequest.setParent(bulkModel.getParent());
            }

            bulkRequest.add(indexRequest);
        }

        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        return bulkResponse;
    }

    @Override
    public BulkResponse bulkUpdate(String indexName, String typeName, HashMap<String, String> sources) {
        if (sources == null || sources.size() == 0) {
            return null;
        }
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        UpdateRequestBuilder reequest;
        for (Map.Entry<String, String> s : sources.entrySet()) {
            reequest = client.prepareUpdate(indexName, typeName, s.getKey()).setDoc(s.getValue());
            bulkRequest.add(reequest);
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        return bulkResponse;
    }

    @Override
    public BulkResponse bulkDelete(String indexName, String typeName, List<String> ids) {
        if (ids == null || ids.size() == 0) {
            return null;
        }
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        DeleteRequestBuilder delRequest;
        for (String id : ids) {
            delRequest = client.prepareDelete(indexName, typeName, id);
            bulkRequest.add(delRequest);
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        return bulkResponse;
    }

    @Override
    public SearchResponse prepareSearch(SearchRequestParams searchParams) {

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(searchParams.getIndexName()).setSearchType(searchParams.getSearchType());
        if (!Strings.isWhitespaceOrNull(searchParams.getTypeName())) {
            searchRequestBuilder.setTypes(searchParams.getTypeName());
        }
        if (searchParams.getQueryBuilder() != null) {
            searchRequestBuilder.setQuery(searchParams.getQueryBuilder());// Query
        }
        if (searchParams.getPostFilter() != null) {
            searchRequestBuilder.setPostFilter(searchParams.getPostFilter());// Filter
        }
        if (searchParams.getSort() != null) {
            searchRequestBuilder.addSort(SortBuilders.fieldSort("d").order(searchParams.getOrder()));
        }
        searchRequestBuilder.setFrom(searchParams.getFrom()).setSize(searchParams.getSize()).setExplain(true);

        return searchRequestBuilder.execute().actionGet();
    }

    @Override
    public SearchResponse prepareSearch(SearchRequestBuilder searchRequestBuilder) {
        return searchRequestBuilder.setExplain(true).execute().actionGet();
    }

    @Override
    public RefreshResponse refresh(String... indexName) {
        RefreshResponse response = this.client.admin().indices().prepareRefresh(indexName).execute().actionGet();

        return response;
    }

    @Override
    public OptimizeResponse optimize(String... indexName) {
        OptimizeResponse actionGet = client.admin().indices().prepareOptimize(indexName).setMaxNumSegments(1).setForce(false).execute().actionGet();

        return actionGet;
    }

    @Override
    public ClusterHealthResponse checkClusterHealth(String... indexName) {
        ClusterHealthResponse healths = client.admin().cluster().prepareHealth(indexName).get(); //1
        return healths;
    }

    @Override
    public boolean clusterHealthCheck(String... indexName) {
        ClusterHealthResponse healths = checkClusterHealth();

        try {
            for (ClusterIndexHealth health : healths) {
                String index = health.getIndex();
                int numberOfShards = health.getNumberOfShards();
                int numberOfReplicas = health.getNumberOfReplicas();
                ClusterHealthStatus status = health.getStatus();
                if (ClusterHealthStatus.RED == ClusterHealthStatus.fromValue(status.value()))
                    return false;
            }
        } catch (Exception ex) {

            return  false;
        }

        return true;
    }

    @Override
    public Client getRawClient() {
        // TODO Auto-generated method stub
        return this.client;
    }


    @Override
    public void close() {
        // TODO Auto-generated method stub
        if (baseClient != null)
            baseClient.close();
    }


}
