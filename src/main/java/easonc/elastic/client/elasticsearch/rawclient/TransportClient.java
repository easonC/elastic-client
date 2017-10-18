package easonc.elastic.client.elasticsearch.rawclient;

import easonc.elastic.client.LogCommon;
import easonc.elastic.client.elasticsearch.base.ESBaseClient;
import easonc.elastic.client.elasticsearch.base.EsBaseConnectSetting;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by zhuqi on 2016/11/17.
 */
public class TransportClient implements ESBaseClient, AutoCloseable {
    private final String clusterName;
    private Client client;
    private Settings settings;
    private byte[] ipAddr;
    private int port = 9300;

    public TransportClient(EsBaseConnectSetting connectSetting) {
        this.clusterName = connectSetting.clusterName();
        this.ipAddr = connectSetting.ipAddress();
        this.settings = ImmutableSettings.settingsBuilder().put("cluster.name", clusterName).put("client.transport.sniff", true)
                 .build();
        this.port = connectSetting.port();
    }

    @Override
    public Client client() {
        return this.client;
    }

    @Override
    public void init() {

        try {
            client = new org.elasticsearch.client.transport.TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByAddress(ipAddr), this.port));
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            LogCommon.logError("esclient init error!", e);
        }
    }

    @Override
    public void close() {
        if (client != null)
            client.close();
    }

}
