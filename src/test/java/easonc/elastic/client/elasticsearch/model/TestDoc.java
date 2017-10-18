package easonc.elastic.client.elasticsearch.model;

import easonc.elastic.client.annotation.ESValueBinder;
import easonc.elastic.client.annotation.ElasticProperty;
import easonc.elastic.client.annotation.ElasticType;
import easonc.elastic.client.enums.EsFieldType;

import java.util.List;

/**
 * Created by zhuqi on 2016/11/17.
 */
@ElasticType(idProperty = "id",name = "testDoc")
public class TestDoc  {
    @ElasticProperty(fieldType = EsFieldType.Integer)
    public Integer id;
    @ElasticProperty(fieldType = EsFieldType.String)
    public String name;
    @ElasticProperty(fieldType = EsFieldType.Integer)
    public List<Integer> iids;
    @ElasticProperty(fieldType = EsFieldType.Nested)
    public TestNestDoc nestDoc;

}
