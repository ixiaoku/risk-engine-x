package risk.engine.service.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import risk.engine.common.grovvy.ExpressionParser;
import risk.engine.common.grovvy.GroovyShellUtil;
import risk.engine.dto.dto.rule.RuleIndicatorDTO;
import risk.engine.dto.enums.IndicatorTypeEnum;
import risk.engine.dto.enums.OperationSymbolEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: X
 * @Date: 2025/3/14 17:45
 * @Version: 1.0
 */
public class GroovyExpressionParser {

    /**
     * 解析
     * @param logicString 逻辑表达式 !(1&&2||(3&&4))
     * @param jsonScript json指标结构
     * @return 返回groovy可执行表达式 amount > 5
     */
    public static String parseToGroovyExpression(String logicString, String jsonScript) {
        // 解析 JSON
        Gson gson = new Gson();
        List<RuleIndicatorDTO> conditions = gson.fromJson(jsonScript, new TypeToken<List<RuleIndicatorDTO>>(){}.getType());

        // 构建条件映射
        Map<Integer, String> conditionMap = new HashMap<>();
        for (RuleIndicatorDTO condition : conditions) {
            String expr = buildConditionExpression(condition);
            conditionMap.put(condition.getSerialNumber(), expr);
        }

        // 解析逻辑字符串
        ExpressionParser.Parser parser = new ExpressionParser.Parser(logicString, conditionMap);
        return parser.parse();
    }

    /**
     * 根据指标类型和操作符号处理
     * @param expressionDTO 指标
     * @return 返回指标表达式
     */
    private static String buildConditionExpression(RuleIndicatorDTO expressionDTO) {
        String attributeCode = expressionDTO.getIndicatorCode();
        String attributeValue = expressionDTO.getIndicatorValue();
        //操作逻辑符号
        String operator = OperationSymbolEnum.getOperationSymbolEnumByCode(expressionDTO.getOperationSymbol()).getName();
        //指标类型
        boolean isString = Objects.equals(expressionDTO.getIndicatorType(), IndicatorTypeEnum.STRING.getCode());
        String value = isString ? "'" + attributeValue + "'" : attributeValue;
        return attributeCode + " " + operator + " " + value;
    }

    public static void main(String[] args) {
        String logicString = "!(1&&2||(3&&4))";
        String jsonScript = "[{\"serialNumber\":1,\"attributeCode\":\"fromAddress\",\"attributeValue\":\"3Q8StmtPCgxNeeeM6Ue9errkDgZ9SiLHE4\",\"attributeType\":1,\"operationSymbol\":3},{\"serialNumber\":2,\"attributeCode\":\"amount\",\"attributeValue\":\"5\",\"attributeType\":10,\"operationSymbol\":1},{\"serialNumber\":3,\"attributeCode\":\"toAddress\",\"attributeValue\":\"17qeFe3L7h5CMM1PS7cyjB32E9TT6RQeX6\",\"attributeType\":1,\"operationSymbol\":3},{\"serialNumber\":4,\"attributeCode\":\"uAmount\",\"attributeValue\":\"5\",\"attributeType\":10,\"operationSymbol\":1}]";
        String groovyExpr = parseToGroovyExpression(logicString, jsonScript);
        System.out.println("生成的 Groovy 表达式: " + groovyExpr);

        // 验证
        Map<String, Object> variables = new HashMap<>();
        variables.put("amount", 1);
        variables.put("uAmount", 1);
        variables.put("fromAddress", "3Q8StmtPCgxNeeeM6Ue9errkDgZ9SiLHE4");
        variables.put("toAddress", "active");
        boolean result = GroovyShellUtil.runGroovy(groovyExpr, variables);
        System.out.println("执行结果: " + result);
    }

}
