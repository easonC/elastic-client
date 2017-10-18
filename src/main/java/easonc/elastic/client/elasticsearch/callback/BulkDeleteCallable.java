package easonc.elastic.client.elasticsearch.callback;

import easonc.elastic.client.elasticsearch.ElasticsearchClient;
import org.elasticsearch.action.bulk.BulkResponse;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by zhuqi on 2016/12/14.
 */
public class BulkDeleteCallable implements Callable<Boolean> {
    private ElasticsearchClient client = null;
    private String indexName;
    private String typeName;
    private List<String> ids;

    public List<String> getIds() {
        return ids;
    }

    public BulkDeleteCallable(ElasticsearchClient client, String indexName, String typeName, List<String> ids) {
        this.client = client;
        this.indexName = indexName;
        this.typeName = typeName;
        this.ids = ids;
    }

    @Override
    public Boolean call() throws Exception {
        BulkResponse responses = client.bulkDelete(indexName, typeName, ids);
        if (responses == null)
            return false;
        return responses.hasFailures();
    }
}
