package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import risk.engine.common.function.ValidatorHandler;
import risk.engine.dto.enums.ErrorCodeEnum;
import risk.engine.dto.param.DictionaryParam;
import risk.engine.dto.vo.ResponseVO;
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
    @GetMapping("/options/parameter")
    public ResponseVO indicatorOptions(@ModelAttribute DictionaryParam dictionaryParam) {
        log.info("detail indicator: {}", dictionaryParam);
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(ObjectUtils.isEmpty(dictionaryParam.getDictKeyList())
                        || StringUtils.isEmpty(dictionaryParam.getQueryCode()));
        return ResponseVO.success(dictionaryService.getList(dictionaryParam.getDictKeyList(), dictionaryParam.getQueryCode()));
    }

    /**
     * 字典 不带查询条件
     * @param dictKeyList 字典key数组
     * @return 结果
     */
    @GetMapping("/options")
    public ResponseVO operationOptions(@RequestParam("dictKeyList") String[] dictKeyList) {
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(ObjectUtils.isEmpty(dictKeyList));
        return ResponseVO.success(dictionaryService.getList(dictKeyList));
    }

    /**
     * 字典
     * @param dictionaryParam 参数
     * @return 结果
     */
    @GetMapping("/options/db")
    public ResponseVO getDictIncidents(@ModelAttribute DictionaryParam dictionaryParam) {
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(ObjectUtils.isEmpty(dictionaryParam.getDictKeyList()));
        return ResponseVO.success(dictionaryService.getDictByDB(dictionaryParam));
    }
}
