package risk.engine.common.grovvy;

import com.alibaba.fastjson2.JSON;
import org.apache.commons.collections4.CollectionUtils;
import risk.engine.common.function.ValidatorHandler;
import risk.engine.dto.dto.rule.RuleMetricDTO;
import risk.engine.dto.enums.ErrorCodeEnum;
import risk.engine.dto.param.RuleParam;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author: X
 * @Date: 2025/4/11 22:33
 * @Version: 1.0
 */
public class ExpressionValidator {

    private static final Pattern DIGIT_PATTERN = Pattern.compile("[1-9]");

    public static List<Integer> extractDigits(String expr) {
        List<Integer> digits = new ArrayList<>();
        Matcher matcher = DIGIT_PATTERN.matcher(expr);
        while (matcher.find()) {
            digits.add(Integer.parseInt(matcher.group()));
        }
        return digits;
    }

    public static boolean isWellFormed(String expr) {
        // 去除所有空格
        expr = expr.replaceAll("\\s+", "");

        // 初步字符合法性检查
        if (!expr.matches("^([()!]|[1-9]|&&|\\|\\|)+$")) {
            return false;
        }

        // 括号配对检查
        int balance = 0;
        for (char c : expr.toCharArray()) {
            if (c == '(') balance++;
            if (c == ')') balance--;
            if (balance < 0) return false; // 提前闭合
        }
        if (balance != 0) return false; // 括号不平衡

        // 结构合法性检查（通过 token 分析）
        String tokenPattern = "(\\()|(\\))|(!)|(&&)|(\\|\\|)|([1-9])";
        List<String> tokens = new ArrayList<>();
        Matcher m = Pattern.compile(tokenPattern).matcher(expr);
        while (m.find()) {
            tokens.add(m.group());
        }

        // 遍历 token 判断逻辑合法性
        String prev = null;
        for (String token : tokens) {
            if (prev != null) {
                // 连续两个数字
                if (prev.matches("[1-9]") && token.matches("[1-9]")) return false;
                // 运算符连着运算符
                if ((prev.equals("&&") || prev.equals("||")) &&
                        (token.equals("&&") || token.equals("||"))) return false;
                // 数字后跟左括号
                if (prev.matches("[1-9]") && token.equals("(")) return false;
                // 右括号后跟数字
                if (prev.equals(")") && token.matches("[1-9]")) return false;
            }
            prev = token;
        }

        // 不能以逻辑符号开头或结尾
        if (tokens.get(0).matches("&&|\\|\\|") || tokens.get(tokens.size() - 1).matches("&&|\\|\\|")) {
            return false;
        }

        return true;
    }

    public static void verify(RuleParam ruleParam) {
        //规则校验
        List<Integer> ids = extractDigits(ruleParam.getLogicScript());
        List<RuleMetricDTO> metrics = JSON.parseArray(ruleParam.getJsonScript(), RuleMetricDTO.class);
        List<Integer> indexes = metrics.stream().map(RuleMetricDTO::getSerialNumber).collect(Collectors.toList());
        System.out.println(CollectionUtils.isEqualCollection(ids, indexes));
        ValidatorHandler.verify(ErrorCodeEnum.RULE_LOGIC_ILLEGAL)
                .validateException(!ExpressionValidator.isWellFormed(ruleParam.getLogicScript())
                        || !CollectionUtils.isEqualCollection(ids, indexes)
                );
    }

}
