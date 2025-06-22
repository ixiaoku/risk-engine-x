package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import risk.engine.client.feign.DictionaryFeignClient;
import risk.engine.common.function.ValidatorHandler;
import risk.engine.dto.enums.ErrorCodeEnum;
import risk.engine.service.service.IDictionaryService;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author: X
 * @Date: 2025/4/1 19:41
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/dict")
public class DictionaryFeignController implements DictionaryFeignClient {

    @Resource
    private IDictionaryService dictionaryService;


    /**
     * 字典 不带查询条件
     * @param dictKeyList 字典key数组
     * @return 结果
     */
    @GetMapping("/options")
    @Override
    public Map<String, Object> getDict(@RequestParam("dictKeyList") String[] dictKeyList) {
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(ObjectUtils.isEmpty(dictKeyList));
        return dictionaryService.getList(dictKeyList);
    }

    /**
     * 字典查询
     * @param dictKeyList 参数
     * @return 参数
     */
    @GetMapping("/db")
    @Override
    public Map<String, Object> getDictDb(@RequestParam("dictKeyList") String[] dictKeyList, @RequestParam("queryCode") String queryCode) {
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(ObjectUtils.isEmpty(dictKeyList)
                );
        return dictionaryService.getDictDb(dictKeyList, queryCode);
    }

}
