package easonc.elastic.client.elasticsearch;

import easonc.elastic.client.elasticsearch.base.DocExecutor;
import easonc.elastic.client.elasticsearch.model.TestDoc;
import easonc.elastic.client.elasticsearch.model.TestNestDoc;
import easonc.elastic.client.elasticsearch.sql.exception.SqlParseException;
import easonc.elastic.client.elasticsearch.sql.query.DefaultQueryAction;
import easonc.elastic.client.elasticsearch.sql.query.ESActionFactory;
import easonc.elastic.client.elasticsearch.sql.query.QueryAction;
import easonc.elastic.client.util.CommonFunc;
import org.elasticsearch.action.search.SearchResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by zhuqi on 2017/1/23.
 */
public class EsDocExecutorTest {

    DocExecutor executor;//请保持单例

    /**
     * 初始化，请保持单例
     * 或用spring 单例注入
     */
    @Before
    public void init() {
        executor = new EsDocExecutor("es_groupproductsearch_new", "10.2.36.125", 9300);
        executor.init();
        ;
    }

    /**
     * 创建索引，建立mapping
     */
    @Test()
    public void testIndexCreate() {

        boolean f = executor.mappingAndIndice("testindex", TestDoc.class, 5, 2);//索引名字必须小写，索引类型为class的短名称
        Assert.assertTrue(f);
    }

    /**
     * 务必写对索引名
     */
    @Test
    public void testdel() {
        boolean f = executor.getRawClient().indexDelete("testindex");
        Assert.assertTrue(f);
    }

    /**
     * 写入文档，注意testdoc类的注解，很重要
     */
    @Test
    public void pushDoc() {
        List<TestDoc> list = new ArrayList<>();
        TestDoc doc = new TestDoc();
        doc.id = 1;
        doc.iids = new ArrayList<>();
        doc.iids.add(1);
        doc.iids.add(2);
        doc.name = "dads";
        doc.nestDoc = new TestNestDoc();
        doc.nestDoc.name = "i am nest";
        executor.bulkDoc("testindex", CommonFunc.getClassShortName(TestDoc.class), list);
        executor.refresh();//请在一批更新后再刷新索引或周期性刷新，不要实时刷新
    }

    /**
     * 如果不用sql可以自己看java api的组装
     */
    @Test
    public void search() {
        QueryAction queryAction = null;
        String sql = "select * from testindex/testdoc where id=1 and iids=2 order by id desc limit 0,10";
        try {
            queryAction = ESActionFactory.create(executor.getRawClient().getRawClient(), sql);
            queryAction.explain();//解释sql
        } catch (SqlParseException e) {
            e.printStackTrace();
        } catch (SQLFeatureNotSupportedException e) {
            e.printStackTrace();
        }

        String json = ((DefaultQueryAction) queryAction).getRequestBuilder().toString();

        SearchResponse response =
                executor.seachDoc(((DefaultQueryAction) queryAction).getRequestBuilder());

        Assert.assertNotNull(response);
    }
}
