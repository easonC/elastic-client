package easonc.elastic.client.elasticsearch.rawclient;

import easonc.elastic.client.elasticsearch.base.ESBaseClient;
import easonc.elastic.client.elasticsearch.base.EsBaseConnectSetting;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.Node;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

/**
 * Created by zhuqi on 2016/11/17.
 */
public class NodeClient implements ESBaseClient, AutoCloseable {
    private Client client;
    private Node node;
    private ImmutableSettings.Builder settings;
    private final String clusterName;

    public NodeClient(EsBaseConnectSetting connectSetting) {
        this.clusterName = connectSetting.clusterName();
        this.settings = ImmutableSettings.settingsBuilder().put("http.enabled", false);
    }

    @Override
    public Client client() {
        return client;
    }


    @Override
    public void init() {
        this.node = nodeBuilder().clusterName(this.clusterName).client(true).node();
        this.client = node.client();
    }

    @Override
    public void close() {
        if (node != null)
            node.close();
    }


}
