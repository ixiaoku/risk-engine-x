package risk.engine.service.service.impl;

import com.alibaba.fastjson2.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.page.PageMethod;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import risk.engine.common.function.ValidatorHandler;
import risk.engine.common.util.DateTimeUtil;
import risk.engine.db.dao.CounterMetricMapper;
import risk.engine.db.entity.CounterMetricPO;
import risk.engine.dto.PageResult;
import risk.engine.dto.enums.CounterStatusEnum;
import risk.engine.dto.enums.ErrorCodeEnum;
import risk.engine.dto.param.CounterMetricParam;
import risk.engine.dto.vo.CounterMetricVO;
import risk.engine.service.service.ICounterMetricService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: X
 * @Date: 2025/4/29 01:29
 * @Version: 1.0
 */
@Service
public class CounterMetricServiceImpl implements ICounterMetricService {

    @Resource
    private CounterMetricMapper counterMetricMapper;

    @Override
    public boolean insert(CounterMetricParam param) {
        CounterMetricPO counterMetric = new CounterMetricPO();
        counterMetric.setMetricCode(param.getMetricCode());
        counterMetric.setMetricName(param.getMetricName());
        counterMetric.setMetricType(param.getMetricType());
        counterMetric.setIncidentCode(param.getIncidentCode());
        counterMetric.setAttributeKey(JSON.toJSONString(param.getAttributeKey()));
        counterMetric.setWindowSize(param.getWindowSize());
        counterMetric.setAggregationType(param.getAggregationType());
        counterMetric.setWindowType(param.getWindowType());
        counterMetric.setGroovyScript(param.getGroovyScript());
        counterMetric.setStatus(param.getStatus());
        counterMetric.setDescription(param.getDescription());
        counterMetric.setOperator(param.getOperator());
        counterMetric.setCreateTime(LocalDateTime.now());
        counterMetric.setUpdateTime(LocalDateTime.now());
        return counterMetricMapper.insert(counterMetric) > 0;
    }

    @Override
    public PageResult<CounterMetricVO> list(CounterMetricParam param) {
        PageResult<CounterMetricVO> pageResult = new PageResult<>();
        CounterMetricPO query = new CounterMetricPO();
        query.setIncidentCode(param.getIncidentCode());
        query.setMetricCode(param.getMetricCode());
        query.setMetricName(param.getMetricName());
        // 开启分页并查询
        Page<CounterMetricPO> counterMetricPage = PageMethod.startPage(param.getPageNum(), param.getPageSize())
                .doSelectPage(() -> counterMetricMapper.selectByExample(query));
        if (CollectionUtils.isEmpty(counterMetricPage.getResult())) return pageResult;
        List<CounterMetricVO> counterMetricVOS = counterMetricPage.getResult()
                        .stream()
                        .map(this::convertVO)
                        .collect(Collectors.toList());
        pageResult.setPageSize(param.getPageSize());
        pageResult.setPageNum(param.getPageNum());
        pageResult.setList(counterMetricVOS);
        pageResult.setTotal(counterMetricPage.getTotal());
        return pageResult;
    }

    @Override
    public List<CounterMetricPO> selectExample(CounterMetricPO param) {
        return counterMetricMapper.selectByExample(param);
    }

    @Override
    public boolean updateByPrimaryKey(CounterMetricParam param) {
        CounterMetricPO counterMetric = new CounterMetricPO();
        counterMetric.setId(param.getId());
        counterMetric.setMetricName(param.getMetricName());
        counterMetric.setMetricType(param.getMetricType());
        counterMetric.setWindowSize(param.getWindowSize());
        counterMetric.setAggregationType(param.getAggregationType());
        counterMetric.setAggregationType(param.getAggregationType());
        counterMetric.setWindowType(param.getWindowType());
        counterMetric.setStatus(param.getStatus());
        counterMetric.setDescription(param.getDescription());
        counterMetric.setOperator(param.getOperator());
        counterMetric.setUpdateTime(LocalDateTime.now());
        return false;
    }

    @Override
    public CounterMetricVO getOne(Long id) {
        CounterMetricPO counterMetric = counterMetricMapper.selectByPrimaryKey(id);
        return convertVO(counterMetric);
    }

    @Override
    public boolean delete(Long id) {
        CounterMetricPO counterMetric = counterMetricMapper.selectByPrimaryKey(id);
        ValidatorHandler.verify(ErrorCodeEnum.ONLINE_STATUS_COUNTER_METRIC)
                .validateException(Objects.isNull(counterMetric) || Objects.equals(counterMetric.getStatus(), CounterStatusEnum.ONLINE.getCode()));
        return counterMetricMapper.deleteByPrimaryKey(id) > 0;
    }

    private CounterMetricVO convertVO(CounterMetricPO counterMetric) {
        CounterMetricVO counterMetricVO = new CounterMetricVO();
        counterMetricVO.setId(counterMetric.getId());
        counterMetricVO.setMetricCode(counterMetric.getMetricCode());
        counterMetricVO.setMetricName(counterMetric.getMetricName());
        counterMetricVO.setMetricType(counterMetric.getMetricType());
        counterMetricVO.setIncidentCode(counterMetric.getIncidentCode());
        if (Objects.nonNull(counterMetric.getAttributeKey())) {
            counterMetricVO.setAttributeKey(JSON.parseArray(counterMetric.getAttributeKey(), String.class));
        }
        counterMetricVO.setWindowSize(counterMetric.getWindowSize());
        counterMetricVO.setAggregationType(counterMetric.getAggregationType());
        counterMetricVO.setAggregationType(counterMetric.getAggregationType());
        counterMetricVO.setWindowType(counterMetric.getWindowType());
        counterMetricVO.setStatus(counterMetric.getStatus());
        counterMetricVO.setDescription(counterMetric.getDescription());
        counterMetricVO.setCreateTime(DateTimeUtil.getTimeByLocalDateTime(counterMetric.getCreateTime()));
        counterMetricVO.setUpdateTime(DateTimeUtil.getTimeByLocalDateTime(counterMetric.getUpdateTime()));
        return counterMetricVO;
    }

}
