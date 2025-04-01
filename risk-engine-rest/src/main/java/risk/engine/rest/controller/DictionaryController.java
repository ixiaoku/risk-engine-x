package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import risk.engine.dto.param.DictionaryParam;
import risk.engine.dto.result.ResponseResult;
import risk.engine.service.service.IDictionaryService;

import javax.annotation.Resource;

/**
 * @Author: X
 * @Date: 2025/4/1 19:41
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/dict")
public class DictionaryController {


    @Resource
    private IDictionaryService dictionaryService;

    /**
     * 字典 带查询参数
     * @param dictionaryParam 参数
     * @return 结果
     */
    @PostMapping("/options/parameter")
    public ResponseResult indicatorOptions(@RequestBody DictionaryParam dictionaryParam) {
        log.info("detail indicator: {}", dictionaryParam);
        return ResponseResult.success(dictionaryService.getList(dictionaryParam.getDictKeyList(), dictionaryParam.getQueryCode()));
    }

    /**
     * 字典 不带查询条件
     * @param dictKeyList 字典key数组
     * @return 结果
     */
    @GetMapping("/options")
    public ResponseResult operationOptions(@RequestParam("dictKeyList") String[] dictKeyList) {
        return ResponseResult.success(dictionaryService.getList(dictKeyList));
    }

}
