package easonc.elastic.client.util;

import easonc.elastic.client.lang.Function;
import easonc.elastic.client.lang.KVTransferFunction;
import easonc.elastic.client.lang.KeyValueFunction;
import easonc.elastic.client.lang.TransferFunction;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.*;

/**
 * Created by zhuqi on 2016/11/28.
 */
public final class CollectionsUtil {
    public static <T> List<List<T>> spiltList2Lists(List<T> list, int count) {
        if (list == null || list.size() == 0)
            return null;

        int total = list.size();

        int g = (int) Math.ceil(total * 1.0d / count);

        List<List<T>> lists = new ArrayList<>(g);
        List<T> item;
        for (int i = 0; i < g; i++) {
            if (i == g - 1) {
                item = list.subList(i * count, list.size());
            } else {
                item = list.subList(i * count, i * count + count);
            }
            lists.add(item);
        }
        return lists;
    }

    public static <T> void forEach(Iterable<T> iterator, Function<T> executor) {
        if (iterator == null || executor == null)
            return;
        for (T t : iterator) {
            executor.execute(t);
        }
    }

    public static <K, V> void forEach(Map<K, V> map, KeyValueFunction<K, V> executor) {
        if (map == null || map.size() == 0 || executor == null)
            return;
        for (Map.Entry<K, V> t : map.entrySet()) {
            executor.execute(t.getKey(), t.getValue());
        }
    }

    public static <K, V> Map<K, V> list2Map(List<V> list, MapKeySelector<K, V> keySelector) {
        if (keySelector == null || list == null || list.size() == 0)
            return null;
        Map<K, V> map = new HashMap<>();
        for (V v : list) {
            K k = keySelector.key(v);
            map.put(k, v);
        }
        return map;
    }

    public static <K, V> Map<K, List<V>> groupBy(List<V> list, MapKeySelector<K, V> keySelector) {
        if (keySelector == null || list == null || list.size() == 0)
            return null;
        Map<K, List<V>> map = new HashMap<>();
        for (V v : list) {
            K k = keySelector.key(v);
            if (!map.containsKey(k)) {
                map.put(k, new ArrayList<V>());
            }
            map.get(k).add(v);
        }
        return map;
    }

    public static <K, V> Multimap<K, V> toMultimap(List<V> list, com.google.common.base.Function<V, K> function) {
        if (function == null || list == null || list.size() == 0)
            return null;
        return Multimaps.index(list, function);
    }


    public static <K, T1, T2> Map<K, T2> trasferMap(Map<K, T1> from, KVTransferFunction<K, T1, T2> function) {
        if (from == null || from.size() == 0)
            return null
                    ;
        Map<K, T2> maps = new HashMap<>(from.size());
        for (Map.Entry<K, T1> t1 : from.entrySet()) {
            if (t1.getValue() != null) {
                if (function.canTrasfer(t1)) {
                    T2 t2 = function.transfer(t1);
                    if (t2 != null)
                        maps.put(t1.getKey(), t2);
                }
            }

        }
        return maps;
    }

    public static <T, V> List<V> trnasferList(List<T> from, TransferFunction<T, V> transfer) {
        if (from == null || from.size() == 0)
            return null;
        List<V> res = new ArrayList<>(from.size());
        for (T t : from) {
            V v = transfer.transfer(t);
            if (v != null) {
                res.add(v);
            }
        }
        return res;
    }

    public static <T, V> List<V> trnasferListDistinct(List<T> from, TransferFunction<T, V> transfer) {
        if (from == null || from.size() == 0)
            return null;
        Set<V> res = new HashSet<>();
        for (T t : from) {
            V v = transfer.transfer(t);
            if (v != null) {
                res.add(v);
            }
        }
        return Lists.newArrayList(res);
    }

    public static <K, V> V getFirstOrNull(Map<K, V> map) {
        V obj = null;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            obj = entry.getValue();
            if (obj != null) {
                break;
            }
        }
        return obj;
    }

    public static <V> V getFirstOrNull(List<V> list) {
        if (list == null || list.size() == 0)
            return null;
        return list.get(0);

    }

    public static <K, V> V tryGetValueFromMap(Map<K, V> map, K key) {
        if (map == null && map.size() == 0)
            return null;
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return null;
    }

    public static <K, V> boolean listContains(List<V> list, K contanin, MapKeySelector<K, V> keySelector) {
        if (list == null || list.size() == 0)
            return false;

        for (V v : list) {
            if (contanin.equals(keySelector.key(v))) {
                return true;
            }
        }
        return false;
    }

    public interface MapKeySelector<K, V> {
        K key(V v);
    }
}
