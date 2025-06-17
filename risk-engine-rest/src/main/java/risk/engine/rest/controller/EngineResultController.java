package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.*;
import risk.engine.client.feign.EngineResultFeignClient;
import risk.engine.common.function.ValidatorHandler;
import risk.engine.dto.PageResult;
import risk.engine.dto.enums.ErrorCodeEnum;
import risk.engine.dto.param.EngineExecutorParam;
import risk.engine.dto.vo.EngineExecutorVO;
import risk.engine.dto.vo.ReplyRuleVO;
import risk.engine.service.service.IEngineResultService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 风控引擎结果查询
 * @Author: X
 * @Date: 2025/3/24 17:05
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/engine")
public class EngineResultController implements EngineResultFeignClient {

    @Resource
    private IEngineResultService engineResultService;

    @PostMapping("/result/list")
    @Override
    public PageResult<EngineExecutorVO> list(@RequestBody EngineExecutorParam executorParam) {
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(StringUtils.isEmpty(executorParam.getStartTime())
                        || StringUtils.isEmpty(executorParam.getEndTime()));
        PageResult<EngineExecutorVO> pageResult = new PageResult<>();
        Pair<List<EngineExecutorVO>, Long> pair = engineResultService.list(executorParam);
        pageResult.setList(pair.getLeft());
        pageResult.setTotal(pair.getRight());
        pageResult.setPageNum(executorParam.getPageNum());
        pageResult.setPageSize(executorParam.getPageSize());
        return pageResult;
    }

    @PostMapping("/result/dashboard")
    @Override
    public Map<String, BigDecimal> dashboard(@RequestBody EngineExecutorParam executorParam) {
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(StringUtils.isEmpty(executorParam.getStartTime())
                        || StringUtils.isEmpty(executorParam.getEndTime()));
        return engineResultService.getDashboard(executorParam);
    }

    @PostMapping("/result/replay")
    @Override
    public ReplyRuleVO replay(@RequestBody EngineExecutorParam executorParam) {
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(StringUtils.isEmpty(executorParam.getRiskFlowNo())
                );
        return engineResultService.replay(executorParam);
    }

    @PostMapping("/result/snapshot")
    @Override
    public EngineExecutorVO snapshot(@RequestBody EngineExecutorParam executorParam) {
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(StringUtils.isEmpty(executorParam.getRiskFlowNo()));
        return engineResultService.getOne(executorParam);
    }

}
