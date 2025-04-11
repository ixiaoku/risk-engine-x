package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.*;
import risk.engine.common.function.ValidatorHandler;
import risk.engine.dto.PageResult;
import risk.engine.dto.enums.ErrorCodeEnum;
import risk.engine.dto.param.EngineExecutorParam;
import risk.engine.dto.vo.EngineExecutorVO;
import risk.engine.dto.vo.ResponseVO;
import risk.engine.service.service.IEngineResultService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: X
 * @Date: 2025/3/24 17:05
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/engine")
public class EngineResultController {

    @Resource
    private IEngineResultService engineResultService;

    @GetMapping("/result/list")
    public ResponseVO list(@ModelAttribute EngineExecutorParam executorParam) {
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(executorParam.getPageNum() == 0
                        || executorParam.getPageSize() == 0);
        PageResult<EngineExecutorVO> pageResult = new PageResult<>();
        Pair<List<EngineExecutorVO>, Long> pair = engineResultService.list(executorParam);
        pageResult.setList(pair.getLeft());
        pageResult.setTotal(pair.getRight());
        pageResult.setPageNum(executorParam.getPageNum());
        pageResult.setPageSize(executorParam.getPageSize());
        return ResponseVO.success(pageResult);
    }

    @GetMapping("/result/dashboard")
    public ResponseVO dashboard(@ModelAttribute EngineExecutorParam executorParam) {
        return ResponseVO.success(engineResultService.getDashboard(executorParam));
    }

    @GetMapping("/result/replay")
    public ResponseVO replay(@ModelAttribute EngineExecutorParam executorParam) {
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(StringUtils.isEmpty(executorParam.getRiskFlowNo()));
        return ResponseVO.success(engineResultService.replay(executorParam));
    }

    @GetMapping("/result/snapshot")
    public ResponseVO snapshot(@ModelAttribute EngineExecutorParam executorParam) {
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(StringUtils.isEmpty(executorParam.getRiskFlowNo()));
        return ResponseVO.success(engineResultService.getOne(executorParam));
    }

}
