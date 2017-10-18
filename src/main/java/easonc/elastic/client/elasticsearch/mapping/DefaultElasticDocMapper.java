package easonc.elastic.client.elasticsearch.mapping;

import easonc.elastic.client.EntityReflectManager;
import easonc.elastic.client.exceptions.EntityReflectionException;
import easonc.elastic.client.Strings;
import easonc.elastic.client.annotation.ElasticProperty;
import easonc.elastic.client.enums.EsFieldType;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Elasticsearch 文档mapping器
 * Created by zhuqi on 2016/11/18.
 */
public class DefaultElasticDocMapper<T> implements DocMapper {

    private Class<T> clazz = null;
    private Map<String, ElasticProperty> propertyMap = null;
    private List<Field> fields = null;

    public DefaultElasticDocMapper(Class<T> clazz) throws EntityReflectionException {
        EntityReflectManager<T> reflectManager = new EntityReflectManager<>(clazz);
        this.fields = reflectManager.getField();
        propertyMap = reflectManager.getAnnotations(ElasticProperty.class);
        propertyMap = propertyMap == null ? new HashMap<String, ElasticProperty>(0) : propertyMap;
        this.clazz = clazz;
    }

    private <TNest> void mapNest(XContentBuilder builder, String field, Class<TNest> clazz) throws IOException, EntityReflectionException {
        builder.startObject(field);
        builder.field("type", "nested");
        DocMapper nestMap = new DefaultElasticDocMapper<>(clazz);
        nestMap.map(builder);
        builder.endObject();
    }

    private <T> void mapObject(XContentBuilder builder, String field, Class<T> clazz) throws IOException, EntityReflectionException {
        builder.startObject(field);
       // builder.field("type", "object");
        DocMapper nestMap = new DefaultElasticDocMapper<>(clazz);
        nestMap.map(builder);
        builder.endObject();
    }

    private <T> void mapGeoPoint(XContentBuilder builder, String field) throws IOException {
        builder.startObject(field);
        builder.field("type", "geo_point")
                .field("lat_lon", true)
                .field("geohash", true).field("geohash_precision", 12)

                .endObject();
    }

    private void mapFields(XContentBuilder builder, ElasticProperty prop) throws IOException {
        builder.field("index", prop.index().toString());//index
        if (prop.docValues()) {
            builder.field("doc_values", true);
        } else {
            builder.field("doc_values", false);
        }
        if (!Strings.isWhitespaceOrNull(prop.dateFormat())) {
            builder.field("format", prop.dateFormat());
        } else if (prop.fieldType().equals(EsFieldType.Date)) {
            builder.field("format", "dateOptionalTime");
        }
        if (!Strings.isWhitespaceOrNull(prop.analyzer())) {
            builder.field("analyzer", prop.analyzer());
        }
        if (!Strings.isWhitespaceOrNull(prop.indexAnalyzer())) {
            builder.field("index_analyzer", prop.indexAnalyzer());
        }
        if (!Strings.isWhitespaceOrNull(prop.searchAnalyzer())) {
            builder.field("search_analyzer", prop.searchAnalyzer());
        }
        if (!Strings.isWhitespaceOrNull(prop.nullValue())) {
            builder.field("null_value", prop.nullValue());
        }
    }

    @Override
    public void map(XContentBuilder builder) throws IOException {
//        if (root)
//            builder.startObject(clazz.getName().toLowerCase());
        builder.startObject("properties");
        ElasticProperty prop;
        for (Field field : this.fields) {
            if (this.propertyMap.containsKey(field.getName())) {
                prop = this.propertyMap.get(field.getName());
                if (prop.fieldType().equals(EsFieldType.Nested)) {
                    try {
                        mapNest(builder, field.getName(), field.getType());
                    } catch (EntityReflectionException e) {
                        e.printStackTrace();
                    }
                } else if (prop.fieldType().equals(EsFieldType.Object)) {
                    try {
                        //List 要获取泛型类型
                        if(field.getType().equals(List.class)){
                            Class t = (Class) ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
                            mapObject(builder, field.getName(), t);
                        }else {
                            mapObject(builder, field.getName(), field.getType());
                        }
                    } catch (EntityReflectionException e) {
                        e.printStackTrace();
                    }
                } else if (prop.fieldType().equals(EsFieldType.GeoPoint)) {
                    mapGeoPoint(builder, field.getName());
                } else {
                    builder.startObject(field.getName())
                            .field("type", prop.fieldType().toString());
                    mapFields(builder, prop);//填充
                    builder.endObject();
                }
            }
        }
        builder.endObject();
//        if (root)
//            builder.endObject();
    }
}
