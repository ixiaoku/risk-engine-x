package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import risk.engine.dto.param.DictionaryParam;
import risk.engine.dto.vo.ResponseVO;
import risk.engine.service.service.IDictionaryService;
import risk.engine.service.service.IIncidentService;
import risk.engine.service.service.IRuleVersionService;

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

    @Resource
    private IRuleVersionService ruleVersionService;

    @Resource
    private IIncidentService incidentService;

    /**
     * 字典 带查询参数
     * @param dictionaryParam 参数
     * @return 结果
     */
    @PostMapping("/options/parameter")
    public ResponseVO indicatorOptions(@RequestBody DictionaryParam dictionaryParam) {
        log.info("detail indicator: {}", dictionaryParam);
        return ResponseVO.success(dictionaryService.getList(dictionaryParam.getDictKeyList(), dictionaryParam.getQueryCode()));
    }

    /**
     * 字典 不带查询条件
     * @param dictKeyList 字典key数组
     * @return 结果
     */
    @GetMapping("/options")
    public ResponseVO operationOptions(@RequestParam("dictKeyList") String[] dictKeyList) {
        return ResponseVO.success(dictionaryService.getList(dictKeyList));
    }

    @GetMapping("/options/db")
    public ResponseVO getDictIncidents(@ModelAttribute DictionaryParam dictionaryParam) {
        return ResponseVO.success(dictionaryService.getDictByDB(dictionaryParam));
    }
}
