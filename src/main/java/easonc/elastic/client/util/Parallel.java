package easonc.elastic.client.util;

import easonc.elastic.client.LogCommon;
import easonc.elastic.client.model.ParallelExecuteResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by zhuqi on 2016/12/13.
 */
public class Parallel {

    static ExecutorService def_pool = Executors.newFixedThreadPool(10);

    public static boolean foreach(List<Future<?>> futureList) {
        return false;//foreach(futureList);
    }

    public static ParallelExecuteResult foreach(List<Callable<Boolean>> callList, ExecutorService pool) {
        ExecutorService threadPool = pool == null ? def_pool : pool;

        int count = callList.size();
        boolean flag = true;
        List<Future<Boolean>> futures = new ArrayList<>(count);
        for (Callable<Boolean> callback : callList) {
            futures.add(threadPool.submit(callback));
        }
        List<Callable<Boolean>> fails = new ArrayList<>(count);

        int i = 0;
        for (Future<Boolean> f : futures) {
            try {
                boolean _flag = f.get();

                if (!_flag) {
                    fails.add(callList.get(i));
                }

                flag &= _flag;
                i++;
            } catch (InterruptedException e) {
                LogCommon.logError(e);
                flag = false;
            } catch (ExecutionException e) {
                LogCommon.logError(e);
                flag = false;
            }
        }


        return new ParallelExecuteResult(flag, fails);
    }
}
