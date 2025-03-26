package risk.engine.components.es;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
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
public class ElasticsearchRestApi {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Resource
    private RestHighLevelClient client;

    /**
     * 通用保存文档方法
     *
     * @param index 索引名
     * @param mapList  存储的数据
     */
    public void saveDocument(String index, List<Map<String, Object>> mapList) {
        try {
            BulkRequest bulkRequest = new BulkRequest();
            bulkRequest.add(new IndexRequest("users").id("1").source(Map.of("name", "Alice", "age", 25), XContentType.JSON));
            bulkRequest.add(new IndexRequest("users").id("2").source(Map.of("name", "Bob", "age", 30), XContentType.JSON));
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

    /**
     * 通用查询文档方法
     *
     * @param index 索引名
     * @param id    文档 ID
     * @return Map<String, Object> 查询结果
     * @throws IOException ES 交互异常
     */
    public Map<String, Object> getDocument(String index, String id) throws IOException {
        GetRequest getRequest = new GetRequest(index, id);
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);

        if (getResponse.isExists()) {
            return getResponse.getSourceAsMap();
        } else {
            return null;
        }
    }

    /**
     * 关闭 ES 客户端
     */
    public void closeClient() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


