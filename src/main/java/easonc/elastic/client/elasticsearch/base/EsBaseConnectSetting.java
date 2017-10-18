package easonc.elastic.client.elasticsearch.base;

/**
 * Created by zhuqi on 2016/11/17.
 */
public interface EsBaseConnectSetting {
    byte[] ipAddress();
    int port();
    String clusterName();

}
