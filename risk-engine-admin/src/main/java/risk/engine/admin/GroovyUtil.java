package risk.engine.admin;

/**
 * @Author: X
 * @Date: 2025/6/11 17:53
 * @Version: 1.0
 */

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class GroovyUtil {
    public static boolean evaluateExpression(String expression, RiskContext context) {
        try {
            Binding binding = new Binding();
            context.getData().forEach(binding::setVariable);
            GroovyShell shell = new GroovyShell(binding);
            Object result = shell.evaluate(expression);
            return result instanceof Boolean ? (Boolean) result : false;
        } catch (Exception e) {
            System.err.println("Error evaluating expression: " + expression + ", " + e.getMessage());
            return false;
        }
    }
}
