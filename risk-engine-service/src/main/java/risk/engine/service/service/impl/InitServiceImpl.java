package risk.engine.service.service.impl;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import risk.engine.db.entity.Incident;
import risk.engine.db.entity.Rule;
import risk.engine.dto.dto.ChainTransferDTO;
import risk.engine.dto.dto.RuleExpressionDTO;
import risk.engine.dto.enums.FieldTypeEnum;
import risk.engine.dto.enums.IncidentStatusEnum;
import risk.engine.dto.enums.OperationSymbolEnum;
import risk.engine.dto.enums.RuleStatusEnum;
import risk.engine.service.service.IIncidentService;
import risk.engine.service.service.IRuleService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Author: X
 * @Date: 2025/3/14 14:09
 * @Version: 1.0
 */
@Service
public class InitServiceImpl {

    @Resource
    private IRuleService ruleService;

    @Resource
    private IIncidentService incidentService;

    /**
     * 事件初始化
     */
    public void initIncident() {
        Incident entity = new Incident();
        entity.setIncidentCode("ChainTransfer");
        entity.setIncidentName("链上转账");
        entity.setDecisionResult(new Gson().toJson(List.of(0, 1)));
        entity.setStatus(IncidentStatusEnum.ONLINE.getCode());
        entity.setResponsiblePerson("cherry.wang");
        entity.setOperator("cherry.wang");
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        ChainTransferDTO chainTransferDTO = getChainTransferDTO();
        entity.setRequestPayload(new Gson().toJson(chainTransferDTO));
        System.out.println(new Gson().toJson(chainTransferDTO));
        incidentService.insert(entity);
    }

    public void initRule() {
        Rule rule = new Rule();
        rule.setIncidentCode("ChainTransfer");
        rule.setRuleCode("ChainTransferD");
        rule.setRuleName("链上转账D策略");
        rule.setStatus(RuleStatusEnum.ONLINE.getCode());
        rule.setScore(88);
        rule.setConditionScript("amount>1 && fromAddress==3Q8StmtPCgxNeeeM6Ue9errkDgZ9SiLHE4");
        rule.setDecisionResult("0");
        rule.setExpiryTime(0);
        rule.setLabel("普通转账");
        rule.setPenaltyAction("加入观察名单");
        rule.setVersion(UUID.randomUUID().toString());
        rule.setResponsiblePerson("cherry.wang");
        rule.setOperator("cherry.wang");

        RuleExpressionDTO ruleExpressionDTO = new RuleExpressionDTO();
        ruleExpressionDTO.setAttributeCode("fromAddress");
        ruleExpressionDTO.setOperationSymbol(OperationSymbolEnum.EQUAL_TO.getCode());
        ruleExpressionDTO.setAttributeValue("3Q8StmtPCgxNeeeM6Ue9errkDgZ9SiLHE4");
        ruleExpressionDTO.setAttributeType(FieldTypeEnum.STRING.getCode());
        ruleExpressionDTO.setSerialNumber(1);
        RuleExpressionDTO expressionDTO = new RuleExpressionDTO();
        expressionDTO.setAttributeCode("amount");
        expressionDTO.setOperationSymbol(OperationSymbolEnum.GREATER_THAN.getCode());
        expressionDTO.setAttributeValue("5");
        expressionDTO.setAttributeType(FieldTypeEnum.BIG_DECIMAL.getCode());
        expressionDTO.setSerialNumber(2);
        List<RuleExpressionDTO> expressionList = new ArrayList<>();
        expressionList.add(ruleExpressionDTO);
        rule.setJsonScript(new Gson().toJson(expressionList));
        rule.setExpression("1&&2");
        rule.setCreateTime(LocalDateTime.now());
        rule.setUpdateTime(LocalDateTime.now());
        ruleService.insert(rule);
    }

    private static ChainTransferDTO getChainTransferDTO() {
        ChainTransferDTO chainTransferDTO = new ChainTransferDTO();
        chainTransferDTO.setFromAddress("3Q8StmtPCgxNeeeM6Ue9errkDgZ9SiLHE4");
        chainTransferDTO.setToAddress("17qeFe3L7h5CMM1PS7cyjB32E9TT6RQeX6");
        chainTransferDTO.setAmount(new BigDecimal("4.5238"));
        chainTransferDTO.setUAmount(new BigDecimal(320000));
        chainTransferDTO.setHash("5a4f29e1c388156e238099d44cc11ed503cb04a484e4c5fb33aa9094be11fe1d");
        chainTransferDTO.setHeight(887622);
        chainTransferDTO.setChain("Bitcoin");
        chainTransferDTO.setCoin("BTC");
        chainTransferDTO.setFee(new BigDecimal("0.01845659"));
        return chainTransferDTO;
    }

}
