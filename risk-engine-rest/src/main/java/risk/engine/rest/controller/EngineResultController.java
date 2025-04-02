package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import risk.engine.dto.PageResult;
import risk.engine.dto.param.EngineExecutorParam;
import risk.engine.dto.result.EngineExecutorResult;
import risk.engine.dto.result.ResponseResult;
import risk.engine.service.service.IEngineResultService;

import javax.annotation.Resource;
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
    public ResponseResult list(@RequestBody EngineExecutorParam executorParam) {
        PageResult<EngineExecutorResult> pageResult = new PageResult<>();
        pageResult.setList(engineResultService.list(executorParam));
        pageResult.setTotal(engineResultService.list(executorParam).size());
        return ResponseResult.success(pageResult);
    }

    @PostMapping("/result/dashboard")
    public ResponseResult dashboard(@RequestBody EngineExecutorParam executorParam) {
        return ResponseResult.success(Map.of());
    }

}
