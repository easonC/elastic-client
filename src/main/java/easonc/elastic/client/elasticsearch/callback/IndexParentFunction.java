package easonc.elastic.client.elasticsearch.callback;

/**
 * Created by zhuqi on 2016/12/29.
 */
public interface IndexParentFunction<T> {

    String parent(T doc);

}
