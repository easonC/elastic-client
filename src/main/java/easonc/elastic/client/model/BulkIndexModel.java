package easonc.elastic.client.model;

/**
 * Created by zhuqi on 2016/12/29.
 */
public class BulkIndexModel<T> {
    private String routing;

    private String parent;

    private T doc;

    public String getRouting() {
        return routing;
    }

    public void setRouting(String routing) {
        this.routing = routing;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public T getDoc() {
        return doc;
    }

    public void setDoc(T doc) {
        this.doc = doc;
    }
}
