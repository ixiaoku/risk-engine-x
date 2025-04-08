package risk.engine.common.grovvy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: X
 * @Date: 2025/3/14 15:10
 * @Version: 1.0
 */
@Slf4j
public class GroovyShellUtil {

    // 类级别静态变量
    private static final GroovyShell GROOVY_SHELL = new GroovyShell();
    private static final Map<String, Script> SCRIPT_CACHE = new ConcurrentHashMap<>();

    /**
     * groovy表达式
     * @param script 条件表达式
     * @param objectMap 指标map
     * @return 结果
     */
    public static boolean runGroovy(String script, Map<String, Object> objectMap) {
        try {
            // 从缓存获取或编译脚本
            Script groovyScript = SCRIPT_CACHE.computeIfAbsent(script, s -> {
                long start = System.currentTimeMillis();
                Script compiled = GROOVY_SHELL.parse(s);
                log.info("Script {} compile time: {}ms", s, System.currentTimeMillis() - start);
                return compiled;
            });
            // 设置参数
            Binding binding = new Binding();
            objectMap.forEach(binding::setVariable);
            groovyScript.setBinding(binding);
            // 执行
            long start = System.currentTimeMillis();
            boolean result = (boolean) groovyScript.run();
            log.info("Script {} execute time: {}ms", script, System.currentTimeMillis() - start);
            return result;
        } catch (Exception e) {
            log.error("表达式执行失败: {}, 错误: {}", script, e.getMessage(), e);
            throw new IllegalArgumentException("表达式执行失败: " + script + ", 错误: " + e.getMessage(), e);
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
