package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import risk.engine.dto.PageResult;
import risk.engine.dto.param.EngineExecutorParam;
import risk.engine.dto.vo.EngineExecutorVO;
import risk.engine.dto.vo.ResponseVO;
import risk.engine.service.service.IEngineResultService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/result/list")
    public ResponseVO list(@RequestBody EngineExecutorParam executorParam) {
        PageResult<EngineExecutorVO> pageResult = new PageResult<>();
        Pair<List<EngineExecutorVO>, Long> pair = engineResultService.list(executorParam);
        pageResult.setList(pair.getLeft());
        pageResult.setTotal(pair.getRight());
        pageResult.setPageNum(executorParam.getPageNum());
        pageResult.setPageSize(executorParam.getPageSize());
        return ResponseVO.success(pageResult);
    }

    @PostMapping("/result/dashboard")
    public ResponseVO dashboard(@RequestBody EngineExecutorParam executorParam) {
        EngineExecutorVO engineExecutorVO = engineResultService.getOne(executorParam);
        return ResponseVO.success(engineExecutorVO);
    }

    @PostMapping("/result/detail")
    public ResponseVO getOne(@RequestBody EngineExecutorParam executorParam) {
        return ResponseVO.success(Map.of());
    }

    @PostMapping("/result/snapshot")
    public ResponseVO snapshot(@RequestBody EngineExecutorParam executorParam) {
        return ResponseVO.success(Map.of());
    }

}
