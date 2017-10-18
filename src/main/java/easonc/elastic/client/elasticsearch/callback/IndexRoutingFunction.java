package easonc.elastic.client.elasticsearch.callback;

/**
 * Created by zhuqi on 2016/12/29.
 */
public interface IndexRoutingFunction<T> {
    String routing(T doc);
}
