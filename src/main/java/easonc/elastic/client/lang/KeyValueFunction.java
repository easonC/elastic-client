package easonc.elastic.client.lang;

/**
 * Created by zhuqi on 2016/11/29.
 */
public interface KeyValueFunction<K,V> {
    void execute(K k, V v);
}
