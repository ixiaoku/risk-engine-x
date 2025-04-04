package risk.engine.service.service.impl;

import com.alibaba.fastjson2.JSON;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.springframework.stereotype.Service;
import risk.engine.components.es.ElasticsearchClientApi;
import risk.engine.components.es.EngineExecutorBoolQuery;
import risk.engine.db.dao.EngineResultMapper;
import risk.engine.db.entity.EngineResult;
import risk.engine.dto.constant.BusinessConstant;
import risk.engine.dto.param.EngineExecutorParam;
import risk.engine.dto.vo.EngineExecutorVO;
import risk.engine.service.service.IEngineResultService;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: X
 * @Date: 2025/3/12 21:19
 * @Version: 1.0
 */
@Service
public class EngineResultServiceImpl implements IEngineResultService {

    @Resource
    private EngineResultMapper engineResultMapper;

    @Resource
    private ElasticsearchClientApi elasticsearchClientApi;

    @Override
    public boolean deleteByPrimaryKey(Long id) {
        return engineResultMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public boolean insert(EngineResult record) {
        return engineResultMapper.insert(record) > 0;
    }

    @Override
    public List<EngineResult> selectByExample(EngineResult engineResult) {
        return engineResultMapper.selectByExample(engineResult);
    }

    @Override
    public EngineResult selectByPrimaryKey(Long id) {
        return engineResultMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<EngineExecutorVO> list(EngineExecutorParam executorParam) {
        BoolQueryBuilder boolQuery = EngineExecutorBoolQuery.getBoolQuery(executorParam);
        SearchHit[] searchHits = elasticsearchClientApi.queryRestHighLevelClient(BusinessConstant.ENGINE_INDEX, boolQuery);
        if (searchHits == null || searchHits.length == 0) {
            return List.of();
        }
        return Arrays.stream(searchHits)
                .map(searchHit -> JSON.parseObject(searchHit.getSourceAsString(), EngineExecutorVO.class))
                .collect(Collectors.toList());

    }

}
