package easonc.elastic.client.lang;

import java.util.Map;

/**
 * Created by zhuqi on 2016/12/1.
 */
public interface  KVTransferFunction<K, T1, T2>   {
    T2 transfer(Map.Entry<K, T1> t1);
    boolean canTrasfer(Map.Entry<K, T1> t1);
}
