package easonc.elastic.client.model;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by zhuqi on 2016/12/13.
 */
public class ParallelExecuteResult {
    private List<Callable<Boolean>> failCall;

    private boolean isSuccess = false;

    public ParallelExecuteResult() {
        this.isSuccess = true;
    }

    public ParallelExecuteResult(boolean result, List<Callable<Boolean>> failCall) {
        this.isSuccess = result;
        this.failCall = failCall;
    }

    public ParallelExecuteResult(List<Callable<Boolean>> failCall) {
        this.failCall = failCall;
    }

    public List<Callable<Boolean>> getFailCall() {
        return failCall;
    }


    public boolean isSuccess() {
        return isSuccess;
    }

}
