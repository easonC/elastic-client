package easonc.elastic.client.elasticsearch;

import easonc.elastic.client.elasticsearch.base.EsBaseConnectSetting;
import easonc.elastic.client.util.CommonFunc;

/**
 * Created by zhuqi on 2016/11/17.
 */
public class EsConnectSetting implements EsBaseConnectSetting {

    private String clusterName;
    private byte[] ipAddr;
    private int port = 9300;

    public EsConnectSetting(String ipAddr) {
        this.ipAddr = CommonFunc.ipv42ByteArr(ipAddr);
    }

    public EsConnectSetting(byte[] ipAddr) {
        this.ipAddr = ipAddr;
    }

    public EsConnectSetting(String clusterName, String ipAddr) {
        this(ipAddr);
        this.clusterName = clusterName;
    }

    public EsConnectSetting(String ipAddr, int port) {
        this.ipAddr = CommonFunc.ipv42ByteArr(ipAddr);
        this.port = port;
    }

    public EsConnectSetting(byte[] ipAddr, int port) {
        this.ipAddr = ipAddr;
        this.port = port;
    }

    public EsConnectSetting(String clusterName, String ipAddr, int port) {
        this(ipAddr, port);
        this.clusterName = clusterName;
    }

    @Override
    public byte[] ipAddress() {
        return ipAddr;
    }

    @Override
    public int port() {
        return this.port;
    }

    @Override
    public String clusterName() {
        return clusterName;
    }
}
