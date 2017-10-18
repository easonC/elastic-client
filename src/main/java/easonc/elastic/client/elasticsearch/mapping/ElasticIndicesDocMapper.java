package easonc.elastic.client.elasticsearch.mapping;

import easonc.elastic.client.exceptions.EntityReflectionException;
import easonc.elastic.client.Strings;
import easonc.elastic.client.annotation.ElasticType;
import easonc.elastic.client.enums.FieldIndexOption;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;

/**
 * Created by zhuqi on 2016/11/18.
 */
public class ElasticIndicesDocMapper<T> implements DocMapper {

    private Class<T> clazz = null;
    ElasticType esType = null;
    DefaultElasticDocMapper<T> defaultElasticDocMapper = null;
    private String parentType = null;
    private String indexType = null;

    public ElasticIndicesDocMapper(Class<T> clazz) throws EntityReflectionException {
        this.esType = getElasticType(clazz);
        this.clazz = clazz;
        this.defaultElasticDocMapper = new DefaultElasticDocMapper<T>(clazz);
    }

    public ElasticIndicesDocMapper(Class<T> clazz, String parent) throws EntityReflectionException {
        this.esType = getElasticType(clazz);
        this.clazz = clazz;
        this.defaultElasticDocMapper = new DefaultElasticDocMapper<T>(clazz);
        this.parentType = parent;
    }

    private ElasticType getElasticType(Class<T> clazz) {
        ElasticType esType = clazz.getAnnotation(ElasticType.class);
        return esType;
    }

    public String getIndexType() {
        return this.indexType;
    }

    @Override
    public void map(XContentBuilder builder) throws IOException {
        String className;
        if (this.esType != null && !Strings.isWhitespaceOrNull(esType.name())) {
            className = esType.name();
        } else {
            className = clazz.getName().toLowerCase();
            int index = className.lastIndexOf(".");
            if (index > 0)
                className = className.substring(index + 1);
        }
        indexType = className;
        builder.startObject().startObject(className);
        if (!Strings.isWhitespaceOrNull(this.parentType)) {
            builder.startObject("_parent")
                    .field("type", this.parentType).endObject();
        }
        if (this.esType != null && !Strings.isWhitespaceOrNull(this.esType.idProperty())) {
            builder.startObject("_id")
                    .field("path", this.esType.idProperty())
                    .field("index", FieldIndexOption.NotAnalyzed.toString())
                    .field("store", false)
                    .endObject();
        }
        defaultElasticDocMapper.map(builder);
        builder.endObject().endObject();
    }
}
