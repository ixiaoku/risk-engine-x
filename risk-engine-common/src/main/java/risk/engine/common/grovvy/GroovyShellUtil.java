package risk.engine.common.grovvy;

import groovy.lang.Binding;
import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @Author: X
 * @Date: 2025/3/14 15:10
 * @Version: 1.0
 */
@Slf4j
public class GroovyShellUtil {

    /**
     * groovy表达式
     * @param groovyScript 条件表达式
     * @param objectMap 指标map
     * @return 结果
     */
    public static boolean runGroovy(Script groovyScript, Map<String, Object> objectMap) {
        try {
            Binding binding = new Binding();
            objectMap.forEach(binding::setVariable);
            groovyScript.setBinding(binding);
            return (boolean) groovyScript.run();
        } catch (Exception e) {
            log.error("表达式执行失败: {}, 错误: {}", groovyScript, e.getMessage(), e);
            throw new RuntimeException("表达式执行失败: " + groovyScript + ", 错误: " + e.getMessage(), e);
        }
    }

//    public static void main(String[] args) {
//        String expression = "amount1<amount2 || fromAddress==toAddress";
//        Map<String, Object> map = new HashMap<>();
//        map.put("amount1", 1);
//        map.put("amount2", 2);
//        map.put("fromAddress", "from");
//        map.put("toAddress", "to");
//        Binding binding = new Binding();
//        map.forEach(binding::setVariable);
//        GroovyShell groovyShell = new GroovyShell(binding);
//        boolean flag = (boolean) groovyShell.evaluate(expression);;
//        System.out.println(flag);
//    }

}
