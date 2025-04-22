package risk.engine.components.es;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author: X
 * @Date: 2025/3/15 19:31
 * @Version: 1.0
 */
@Slf4j
@Component
public class ElasticsearchClientApi {

    @Resource
    private RestHighLevelClient client;

    /**
     * 分页查询
     * @param index 索引
     * @param boolQuery 查询条件
     * @param pageNum 页数
     * @param pageSize 一页条数
     * @return 结果
     */
    public Pair<SearchHit[], Long> queryWithPaging(
            String index,
            BoolQueryBuilder boolQuery,
            int pageNum,
            int pageSize
    ) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolQuery);
        sourceBuilder.sort("createTime", SortOrder.DESC);
        if (pageNum != 0 && pageSize != 0) {
            sourceBuilder.from((pageNum - 1) * pageSize);
            sourceBuilder.size(pageSize);
        }
        sourceBuilder.trackTotalHits(true);
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            if (hits == null || hits.getTotalHits() == null) {
                return Pair.of(new SearchHit[0], 0L);
            }
            return Pair.of(hits.getHits(), hits.getTotalHits().value);
        } catch (IOException e) {
            log.error("ES 查询异常: {}", e.getMessage(), e);
            throw new RuntimeException("ES 查询异常", e);
        }
    }

    /**
     * es分组查询
     * @param index 索引
     * @param boolQuery 查询条件
     * @param aggregationBuilder 分组条件
     * @return 结果
     */
    public Pair<Aggregations,Long> queryWithAggregations(
            String index,
            BoolQueryBuilder boolQuery,
            AggregationBuilder aggregationBuilder
    ) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolQuery)
                .trackTotalHits(true)
                .size(0);
        if (aggregationBuilder != null) {
            sourceBuilder.aggregation(aggregationBuilder);
        }
        SearchRequest searchRequest = new SearchRequest(index);
        try {
            searchRequest.source(sourceBuilder);
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            if (hits == null || hits.getTotalHits() == null) {
                return null;
            }
            return Pair.of(searchResponse.getAggregations(), hits.getTotalHits().value);
        } catch (IOException e) {
            log.error("ES 查询异常: {}", e.getMessage(), e);
            throw new RuntimeException("ES 查询异常", e);
        }
    }

    /**
     * 通用保存文档方法
     *
     * @param index 索引名
     * @param mapList  存储的数据
     */
    public void saveDocument(String index, List<Map<String, Object>> mapList) {
        try {
            BulkRequest bulkRequest = new BulkRequest();
            mapList.forEach(map -> {
                IndexRequest indexRequest = new IndexRequest(index)
                        .id(map.get("id").toString())
                        .source(map,XContentType.JSON);
                bulkRequest.add(indexRequest);
            });
            BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (bulkResponse.hasFailures()) {
                log.error("Bulk response: {}", bulkResponse.buildFailureMessage());
                throw new RuntimeException("Bulk response: " + bulkResponse.buildFailureMessage());
            }
        } catch (Exception e) {
            log.error("Bulk response: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}


