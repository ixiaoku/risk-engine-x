package risk.engine.components.es;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public SearchHit[] queryRestHighLevelClient(String index, BoolQueryBuilder boolQuery) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolQuery);
        log.info(sourceBuilder.toString());
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            log.info(searchResponse.getHits().toString());
            if (Objects.isNull(searchResponse.getHits())) {
                return null;
            }
            return searchResponse.getHits().getHits();
        } catch (IOException e) {
            log.error("Bulk response: {}", e.getMessage(), e);
            throw new RuntimeException(e);
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


