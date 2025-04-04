package risk.engine.rest.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import risk.engine.db.entity.TransferRecord;
import risk.engine.dto.param.RiskEngineParam;
import risk.engine.dto.vo.RiskEngineExecuteVO;
import risk.engine.service.service.IRiskEngineExecuteService;
import risk.engine.service.service.ITransferRecordService;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 * @Author: X
 * @Date: 2025/3/17 13:24
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/business")
public class BusinessController {

    @Resource
    private ITransferRecordService transferRecordService;

    @Resource
    private IRiskEngineExecuteService engineExecuteService;

    @PostMapping("/request")
    public void execute() {

        try {
            TransferRecord transferRecord = new TransferRecord();
            transferRecord.setStatus(0);
            List<TransferRecord> transferRecordList = transferRecordService.selectByExample(transferRecord);
            if (CollectionUtils.isEmpty(transferRecordList)) {
                return;
            }
            transferRecordList.forEach(transferRecord1 -> {
                RiskEngineParam engineParam = new RiskEngineParam();
                engineParam.setFlowNo(UUID.randomUUID().toString());
                engineParam.setIncidentCode("ChainTransfer");
                engineParam.setRequestPayload(new Gson().toJson(transferRecord1));
                RiskEngineExecuteVO result = engineExecuteService.execute(engineParam);
                log.info("风控引擎执行 RiskEngineController execute result {}", result.getDecisionResult());
            });
        } catch (Exception e) {
            log.error("错误信息：{}", e.getMessage(), e);
            throw e;
        }
    }
}
