package easonc.elastic.client.lang;

/**
 * Created by zhuqi on 2016/11/28.
 */
public interface Function <T> {
    void execute(T data);
}
