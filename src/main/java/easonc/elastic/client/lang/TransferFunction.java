package easonc.elastic.client.lang;

/**
 * Created by zhuqi on 2016/12/1.
 */
public interface TransferFunction<T1,T2> {
    T2 transfer(T1 t);
}
