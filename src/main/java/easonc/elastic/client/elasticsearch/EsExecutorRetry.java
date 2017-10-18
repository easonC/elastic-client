package easonc.elastic.client.elasticsearch;

import easonc.elastic.client.LogCommon;
import easonc.elastic.client.elasticsearch.base.ExecutorRetry;
import easonc.elastic.client.model.ParallelExecuteResult;
import easonc.elastic.client.util.Parallel;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by zhuqi on 2016/12/13.
 */
public class EsExecutorRetry extends ExecutorRetry {

    private int retry = 3;
    private ExecutorService pool;

    public EsExecutorRetry(ExecutorService pool) {

        this.pool = pool;
    }

    public EsExecutorRetry(ExecutorService pool, int retry) {
        this(pool);
        this.retry = retry;
    }


    @Override
    protected int getRetryTimes() {
        return this.retry;
    }

    @Override
    protected boolean execute(Callable<Boolean> retryCall) {
        Future<Boolean> future = pool.submit(retryCall);
        try {
            return future.get();
        } catch (InterruptedException e) {
            LogCommon.logError(e);
        } catch (ExecutionException e) {
            LogCommon.logError(e);
        }
        return false;
    }

    @Override
    protected boolean execute(List<Callable<Boolean>> retryCall) {
        ParallelExecuteResult result =  Parallel.foreach(retryCall,pool);
        return  result.isSuccess();
    }
}
