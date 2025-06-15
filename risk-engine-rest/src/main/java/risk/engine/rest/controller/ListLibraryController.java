package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import risk.engine.common.function.ValidatorHandler;
import risk.engine.dto.enums.ErrorCodeEnum;
import risk.engine.dto.param.ListLibraryParam;
import risk.engine.dto.vo.ResponseVO;
import risk.engine.service.service.IListLibraryService;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @Author: X
 * @Date: 2025/6/15 19:24
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/list/library")
public class ListLibraryController {

    @Resource
    private IListLibraryService listLibraryService;

    @PostMapping("/insert")
    public ResponseVO insert(@RequestBody ListLibraryParam param) {
        log.info("insert listLibrary: {}", param);
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(StringUtils.isEmpty(param.getListLibraryCode())
                        || StringUtils.isEmpty(param.getListLibraryName())
                        || ObjectUtils.isEmpty(param.getStatus())
                        || ObjectUtils.isEmpty(param.getListCategory())
                );
        return ResponseVO.success(listLibraryService.insert(param));
    }

    @PostMapping("/delete")
    public ResponseVO delete(@RequestBody ListLibraryParam param) {
        log.info("delete listLibrary: {}", param);
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(Objects.isNull(param.getId())
                        || StringUtils.isEmpty(param.getOperator()));
        return ResponseVO.success(listLibraryService.insert(param));
    }

    @PostMapping("/update")
    public ResponseVO update(@RequestBody ListLibraryParam param) {
        log.info("update listLibrary: {}", param);
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(StringUtils.isEmpty(param.getListLibraryCode())
                        || StringUtils.isEmpty(param.getListLibraryName())
                        || ObjectUtils.isEmpty(param.getStatus())
                        || ObjectUtils.isEmpty(param.getListCategory())
                        || StringUtils.isEmpty(param.getOperator())
                        || Objects.isNull(param.getId())
                );
        return ResponseVO.success(listLibraryService.insert(param));
    }

    @GetMapping("/detail")
    public ResponseVO detail(@ModelAttribute ListLibraryParam param) {
        log.info("detail listLibrary: {}", param);
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(ObjectUtils.isEmpty(param.getId()));
        return ResponseVO.success(listLibraryService.insert(param));
    }

    @GetMapping("/list")
    public ResponseVO list(@ModelAttribute ListLibraryParam param) {
        log.info("list listLibrary: {}", param);
        return ResponseVO.success(listLibraryService.insert(param));
    }
}
