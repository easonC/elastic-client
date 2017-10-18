package easonc.elastic.client.elasticsearch;

import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;

/**
 * Created by zhuqi on 2016/11/17.
 */
public class SearchRequestParams {
    private String indexName;
    private String typeName;
    private SearchType searchType = SearchType.DFS_QUERY_THEN_FETCH;
    private QueryBuilder queryBuilder;
    private FilterBuilder postFilter;
    private int size = 10;
    private int from = 0;
    private SortBuilder sort;
    private SortOrder order  = SortOrder.ASC;

    public SearchRequestParams(String indexName){
        this.indexName  = indexName;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public SearchType getSearchType() {
        return searchType;
    }

    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }

    public QueryBuilder getQueryBuilder() {
        return queryBuilder;
    }

    public void setQueryBuilder(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public FilterBuilder getPostFilter() {
        return postFilter;
    }

    public void setPostFilter(FilterBuilder postFilter) {
        this.postFilter = postFilter;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public SortBuilder getSort() {
        return sort;
    }

    public void setSort(SortBuilder sort) {
        this.sort = sort;
    }

    public SortOrder getOrder() {
        return order;
    }

    public void setOrder(SortOrder order) {
        this.order = order;
    }
}
