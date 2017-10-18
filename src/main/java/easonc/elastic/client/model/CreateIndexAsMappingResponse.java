package easonc.elastic.client.model;


/**
 * Created by zhuqi on 2016/11/22.
 */
public class CreateIndexAsMappingResponse implements EsResponse {

    private boolean isSuccess = false;

    public CreateIndexAsMappingResponse(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }


    @Override
    public boolean isSuccess() {
        return false;
    }

    public  static CreateIndexAsMappingResponse createFailedResponse(){
        return new CreateIndexAsMappingResponse(false);
    }
}
