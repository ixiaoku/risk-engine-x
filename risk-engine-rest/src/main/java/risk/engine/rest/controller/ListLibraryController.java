package risk.engine.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import risk.engine.client.feign.ListLibraryClient;
import risk.engine.common.function.ValidatorHandler;
import risk.engine.dto.PageResult;
import risk.engine.dto.enums.ErrorCodeEnum;
import risk.engine.dto.param.ListLibraryParam;
import risk.engine.dto.vo.ListLibraryVO;
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
public class ListLibraryController implements ListLibraryClient {

    @Resource
    private IListLibraryService listLibraryService;

    @PostMapping("/insert")
    @Override
    public Boolean insert(@RequestBody ListLibraryParam param) {
        log.info("insert listLibrary: {}", param);
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(StringUtils.isEmpty(param.getListLibraryCode())
                        || StringUtils.isEmpty(param.getListLibraryName())
                        || ObjectUtils.isEmpty(param.getStatus())
                        || ObjectUtils.isEmpty(param.getListCategory())
                );
        return listLibraryService.insert(param);
    }

    @PostMapping("/delete")
    @Override
    public Boolean delete(@RequestBody ListLibraryParam param) {
        log.info("delete listLibrary: {}", param);
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(Objects.isNull(param.getId())
                        || StringUtils.isEmpty(param.getOperator()));
        return listLibraryService.deleteByPrimaryKey(param.getId());
    }

    @PostMapping("/update")
    @Override
    public Boolean update(@RequestBody ListLibraryParam param) {
        log.info("update listLibrary: {}", param);
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(StringUtils.isEmpty(param.getListLibraryCode())
                        || StringUtils.isEmpty(param.getListLibraryName())
                        || ObjectUtils.isEmpty(param.getStatus())
                        || ObjectUtils.isEmpty(param.getListCategory())
                        || StringUtils.isEmpty(param.getOperator())
                        || Objects.isNull(param.getId())
                );
        return listLibraryService.updateByPrimaryKey(param);
    }

    @GetMapping("/detail")
    @Override
    public ListLibraryVO detail(@RequestParam Long id) {
        log.info("detail listLibrary: {}", id);
        ValidatorHandler.verify(ErrorCodeEnum.PARAMETER_IS_NULL)
                .validateException(ObjectUtils.isEmpty(id));
        return listLibraryService.selectByPrimaryKey(id);
    }

    @GetMapping("/list")
    @Override
    public PageResult<ListLibraryVO> list(@ModelAttribute ListLibraryParam param) {
        log.info("list listLibrary: {}", param);
        return listLibraryService.list(param);
    }
}
