package risk.engine.service.service.impl;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import risk.engine.db.dao.IncidentMapper;
import risk.engine.db.entity.*;
import risk.engine.dto.dto.block.ChainTransferDTO;
import risk.engine.dto.dto.rule.RuleIndicatorDTO;
import risk.engine.dto.enums.*;
import risk.engine.service.handler.GroovyExpressionParser;
import risk.engine.service.service.*;

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
    private IncidentMapper incidentMapper;

    @Resource
    private IIndicatorService indicatorService;

    @Resource
    private IListLibraryService listLibraryService;

    @Resource
    private IListDataService listDataService;

    @Resource
    private IPenaltyService penaltyService;

    @Resource
    private IPenaltyRecordService penaltyRecordService;

    /**
     * 事件初始化
     */
    public void initIncident() {
        Incident entity = new Incident();
        entity.setIncidentCode("ChainTransfer11");
        entity.setIncidentName("链上转账");
        entity.setDecisionResult(new Gson().toJson(List.of(0, 1)));
        entity.setStatus(IncidentStatusEnum.ONLINE.getCode());
        entity.setResponsiblePerson("cherry.wang");
        entity.setOperator("cherry.wang");
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        ChainTransferDTO chainTransferDTO = getChainTransferDTO();
        entity.setRequestPayload(new Gson().toJson(chainTransferDTO));
        System.out.println(new Gson().toJson(entity));
        //incidentMapper.insert(entity);
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
        //ruleService.insert(rule);
    }

    private static ChainTransferDTO getChainTransferDTO() {
        ChainTransferDTO chainTransferDTO = new ChainTransferDTO();
        chainTransferDTO.setSendAddress("3Q8StmtPCgxNeeeM6Ue9errkDgZ9SiLHE4");
        chainTransferDTO.setReceiveAddress("17qeFe3L7h5CMM1PS7cyjB32E9TT6RQeX6");
        chainTransferDTO.setAmount(new BigDecimal("4.5238"));
        chainTransferDTO.setUAmount(new BigDecimal(320000));
        chainTransferDTO.setHash("5a4f29e1c388156e238099d44cc11ed503cb04a484e4c5fb33aa9094be11fe1d");
        chainTransferDTO.setHeight(887622);
        chainTransferDTO.setChain("Bitcoin");
        chainTransferDTO.setToken("BTC");
        chainTransferDTO.setFee(new BigDecimal("0.01845659"));
        return chainTransferDTO;
    }

    public List<RuleIndicatorDTO> getRuleExpressionDTOList() {
        RuleIndicatorDTO expressionDTO1 = new RuleIndicatorDTO();
        expressionDTO1.setIndicatorCode("fromAddress");
        expressionDTO1.setOperationSymbol(OperationSymbolEnum.EQUAL_TO.getCode());
        expressionDTO1.setIndicatorValue("3Q8StmtPCgxNeeeM6Ue9errkDgZ9SiLHE4");
        expressionDTO1.setIndicatorType(FieldTypeEnum.STRING.getCode());
        expressionDTO1.setSerialNumber(1);

        RuleIndicatorDTO expressionDTO2 = new RuleIndicatorDTO();
        expressionDTO2.setIndicatorCode("amount");
        expressionDTO2.setOperationSymbol(OperationSymbolEnum.GREATER_THAN.getCode());
        expressionDTO2.setIndicatorValue("5");
        expressionDTO2.setIndicatorType(FieldTypeEnum.BIG_DECIMAL.getCode());
        expressionDTO2.setSerialNumber(2);

        RuleIndicatorDTO expressionDTO3 = new RuleIndicatorDTO();
        expressionDTO3.setIndicatorCode("toAddress");
        expressionDTO3.setOperationSymbol(OperationSymbolEnum.EQUAL_TO.getCode());
        expressionDTO3.setIndicatorValue("17qeFe3L7h5CMM1PS7cyjB32E9TT6RQeX6");
        expressionDTO3.setIndicatorType(FieldTypeEnum.STRING.getCode());
        expressionDTO3.setSerialNumber(3);

        RuleIndicatorDTO expressionDTO4 = new RuleIndicatorDTO();
        expressionDTO4.setIndicatorCode("uAmount");
        expressionDTO4.setOperationSymbol(OperationSymbolEnum.GREATER_THAN.getCode());
        expressionDTO4.setIndicatorValue("5");
        expressionDTO4.setIndicatorType(FieldTypeEnum.BIG_DECIMAL.getCode());
        expressionDTO4.setSerialNumber(4);

        List<RuleIndicatorDTO> expressionList = new ArrayList<>();
        expressionList.add(expressionDTO1);
        expressionList.add(expressionDTO2);
        expressionList.add(expressionDTO3);
        expressionList.add(expressionDTO4);
        return expressionList;
    }

    private void insertIndicator() {
        Indicator indicator = new Indicator();
        indicator.setId(0L);
        indicator.setIncidentCode("1");
        indicator.setIndicatorName("1");
        indicator.setIndicatorValue("1");
        indicator.setIndicatorDesc("1");
        indicator.setIndicatorSource(0);
        indicator.setIndicatorType(0);
        indicator.setOperator("1");
        indicator.setCreateTime(LocalDateTime.now());
        indicator.setUpdateTime(LocalDateTime.now());
        indicatorService.insert(indicator);
    }

    private void insertListDataLibrary() {
        ListLibrary listDataLibrary = new ListLibrary();
        listDataLibrary.setListLibraryCode("white_address_lib");
        listDataLibrary.setListLibraryName("白名单地址库");
        listDataLibrary.setStatus(1);
        listDataLibrary.setListCategory(1);
        listDataLibrary.setOperator("cherry.li");
        listDataLibrary.setCreateTime(LocalDateTime.now());
        listDataLibrary.setUpdateTime(LocalDateTime.now());
        listDataLibrary.setListLibraryDesc("这是一个白名单库 安全放行");
        listLibraryService.insert(listDataLibrary);
        System.out.println(new Gson().toJson(listDataLibrary));
    }

    private void insertListData() {
        ListData listData = new ListData();
        listData.setListLibraryCode("high_black_address_lib");
        listData.setListLibraryName("高频黑名单地址库");
        listData.setListName("交易地址2");
        listData.setListCode("eth_transfer_code_02");
        listData.setListValue("0x59bd104a9ec7057d3a8fb8fc71148ec5c08c0bae");
        listData.setStatus(1);
        listData.setListType(ListTypeEnum.ADDRESS.getCode());
        listData.setOperator("cherry.li");
        listData.setCreateTime(LocalDateTime.now());
        listData.setUpdateTime(LocalDateTime.now());
        listData.setListDesc("这是一条名单数据");
        System.out.println(new Gson().toJson(listData));
        listDataService.insert(listData);
    }

    private void insertPenalty() {
        Penalty penalty = new Penalty();
        penalty.setPenaltyCode("add_list");
        penalty.setPenaltyName("加名单");
        penalty.setPenaltyDef("risk.engine.service.handler.penalty.AppendListDataHandler");
        penalty.setStatus(0);
        penalty.setOperator("cherry.li");
        penalty.setPenaltyDescription("这是一个加名单的方法");
        penalty.setPenaltyJson("{\"list_library_code\": \"white_address_lib\",\"list_library_name\": \"白名单地址库\",\"status\": 1,\"list_type\": 1}");
        penalty.setCreateTime(LocalDateTime.now());
        penalty.setUpdateTime(LocalDateTime.now());
        penaltyService.insert(penalty);
    }

    private void insertPenaltyAction() {
        PenaltyRecord penaltyRecord = new PenaltyRecord();
        penaltyRecord.setId(0L);
        penaltyRecord.setFlowNo("1");
        penaltyRecord.setRuleCode("1");
        penaltyRecord.setRuleName("1");
        penaltyRecord.setIncidentCode("1");
        penaltyRecord.setIncidentName("1");
        penaltyRecord.setPenaltyCode("1");
        penaltyRecord.setPenaltyName("1");
        penaltyRecord.setPenaltyDef("1");
        penaltyRecord.setPenaltyReason("1");
        penaltyRecord.setPenaltyResult("1");
        penaltyRecord.setStatus(PenaltyStatusEnum.WAIT.getCode());
        penaltyRecord.setRetry(0);
        penaltyRecord.setPenaltyDescription("qq");
        penaltyRecord.setPenaltyJson("qq");
        penaltyRecord.setPenaltyTime(LocalDateTime.now());
        penaltyRecord.setCreateTime(LocalDateTime.now());
        penaltyRecord.setUpdateTime(LocalDateTime.now());
        penaltyRecordService.insert(penaltyRecord);
    }

    public void init() {
        //insertIndicator();
        //insertListDataLibrary();
        //insertListData();
        //insertPenalty();
        //insertPenaltyAction();
    }

}
