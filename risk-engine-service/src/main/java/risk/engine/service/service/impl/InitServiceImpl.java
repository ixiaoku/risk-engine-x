package risk.engine.service.service.impl;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import risk.engine.db.entity.Incident;
import risk.engine.db.entity.Rule;
import risk.engine.dto.dto.block.ChainTransferDTO;
import risk.engine.dto.dto.engine.RuleExpressionDTO;
import risk.engine.dto.enums.FieldTypeEnum;
import risk.engine.dto.enums.IncidentStatusEnum;
import risk.engine.dto.enums.OperationSymbolEnum;
import risk.engine.dto.enums.RuleStatusEnum;
import risk.engine.service.handler.GroovyExpressionParser;
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
        rule.setRuleCode("ChainTransferB");
        rule.setRuleName("链上转账上线规则B");
        rule.setStatus(RuleStatusEnum.MOCK.getCode());
        rule.setScore(88);
        rule.setGroovyScript("amount>1 && fromAddress==3Q8StmtPCgxNeeeM6Ue9errkDgZ9SiLHE4");
        rule.setDecisionResult("0");
        rule.setExpiryTime(0);
        rule.setLabel("普通转账");
        rule.setPenaltyAction("加入观察名单");
        rule.setVersion(UUID.randomUUID().toString());
        rule.setResponsiblePerson("cherry.wang");
        rule.setOperator("cherry.wang");
        rule.setJsonScript(new Gson().toJson(getRuleExpressionDTOList()));
        rule.setLogicScript("1&&2||(3&&4)");
        String conditionScript = GroovyExpressionParser.parseToGroovyExpression(rule.getLogicScript(), rule.getJsonScript());
        rule.setGroovyScript(conditionScript);
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

    public List<RuleExpressionDTO> getRuleExpressionDTOList() {
        RuleExpressionDTO expressionDTO1 = new RuleExpressionDTO();
        expressionDTO1.setAttributeCode("fromAddress");
        expressionDTO1.setOperationSymbol(OperationSymbolEnum.EQUAL_TO.getCode());
        expressionDTO1.setAttributeValue("3Q8StmtPCgxNeeeM6Ue9errkDgZ9SiLHE4");
        expressionDTO1.setAttributeType(FieldTypeEnum.STRING.getCode());
        expressionDTO1.setSerialNumber(1);

        RuleExpressionDTO expressionDTO2 = new RuleExpressionDTO();
        expressionDTO2.setAttributeCode("amount");
        expressionDTO2.setOperationSymbol(OperationSymbolEnum.GREATER_THAN.getCode());
        expressionDTO2.setAttributeValue("5");
        expressionDTO2.setAttributeType(FieldTypeEnum.BIG_DECIMAL.getCode());
        expressionDTO2.setSerialNumber(2);

        RuleExpressionDTO expressionDTO3 = new RuleExpressionDTO();
        expressionDTO3.setAttributeCode("toAddress");
        expressionDTO3.setOperationSymbol(OperationSymbolEnum.EQUAL_TO.getCode());
        expressionDTO3.setAttributeValue("17qeFe3L7h5CMM1PS7cyjB32E9TT6RQeX6");
        expressionDTO3.setAttributeType(FieldTypeEnum.STRING.getCode());
        expressionDTO3.setSerialNumber(3);

        RuleExpressionDTO expressionDTO4 = new RuleExpressionDTO();
        expressionDTO4.setAttributeCode("uAmount");
        expressionDTO4.setOperationSymbol(OperationSymbolEnum.GREATER_THAN.getCode());
        expressionDTO4.setAttributeValue("5");
        expressionDTO4.setAttributeType(FieldTypeEnum.BIG_DECIMAL.getCode());
        expressionDTO4.setSerialNumber(4);

        List<RuleExpressionDTO> expressionList = new ArrayList<>();
        expressionList.add(expressionDTO1);
        expressionList.add(expressionDTO2);
        expressionList.add(expressionDTO3);
        expressionList.add(expressionDTO4);
        return expressionList;
    }

}
