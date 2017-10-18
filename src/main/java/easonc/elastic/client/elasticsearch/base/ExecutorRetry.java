package easonc.elastic.client.elasticsearch.base;


import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by zhuqi on 2016/12/13.
 */
public abstract class ExecutorRetry {


    protected abstract int getRetryTimes();

    protected abstract boolean execute(Callable<Boolean> retryCall);
    protected abstract boolean execute(List<Callable<Boolean>> retryCall);

    public boolean retry(Callable<Boolean> retryCall) {
        int times = getRetryTimes();
        boolean flag = false;
        do {
            flag = execute(retryCall);
        } while (times-- > 0 && !flag);

        return flag;
    }

    public boolean retry(List<Callable<Boolean>> retryCall) {
        int times = getRetryTimes();
        boolean flag = false;
        do {
            flag = execute(retryCall);
        } while (times-- > 0 && !flag);

        return flag;
    }
}
