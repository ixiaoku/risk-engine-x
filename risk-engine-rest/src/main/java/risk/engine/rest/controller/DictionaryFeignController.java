package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import risk.engine.client.feign.DictionaryFeignClient;
import risk.engine.common.function.ValidatorHandler;
import risk.engine.dto.enums.ErrorCodeEnum;
import risk.engine.dto.param.DictionaryParam;
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
     * @param dictionaryParam 字典key数组
     * @return 结果
     */
    @PostMapping("/options")
    @Override
    public Map<String, Object> getDict(@RequestBody DictionaryParam dictionaryParam) {
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(ObjectUtils.isEmpty(dictionaryParam.getDictKeyList()));
        return dictionaryService.getList(dictionaryParam.getDictKeyList());
    }

    /**
     * 字典查询
     * @param dictionaryParam 参数
     * @return 参数
     */
    @PostMapping("/db")
    @Override
    public Map<String, Object> getDictDb(@RequestBody DictionaryParam dictionaryParam) {
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(ObjectUtils.isEmpty(dictionaryParam.getDictKeyList())
                );
        return dictionaryService.getDictDb(dictionaryParam.getDictKeyList(), dictionaryParam.getQueryCode());
    }

}
