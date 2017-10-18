package easonc.elastic.client.elasticsearch.mapping;

import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;

/**
 * Created by zhuqi on 2016/11/18.
 */
public interface DocMapper {
    void map(XContentBuilder builder) throws IOException;
}
