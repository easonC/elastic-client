package easonc.elastic.client.elasticsearch.model;

import easonc.elastic.client.annotation.ElasticProperty;
import easonc.elastic.client.enums.EsFieldType;

/**
 * Created by zhuqi on 2016/11/17.
 */
public class TestNestDoc {
    @ElasticProperty(fieldType = EsFieldType.String)
    public String name;
}
