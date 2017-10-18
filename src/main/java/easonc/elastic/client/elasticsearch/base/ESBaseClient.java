package easonc.elastic.client.elasticsearch.base;

import org.elasticsearch.client.Client;

/**
 * Created by zhuqi on 2016/11/17.
 */
public interface ESBaseClient {

    Client client();
    void init();
    void close();
}
