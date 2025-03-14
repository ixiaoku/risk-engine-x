package risk.engine.common.grovvy;

import java.util.Map;

/**
 * @Author: X
 * @Date: 2025/3/14 17:45
 * @Version: 1.0
 */
public class ExpressionParser {

    // Token 类型
    enum TokenType {
        NUMBER, AND, OR, NOT, LPAREN, RPAREN, EOF
    }

    static class Token {
        TokenType type;
        String value;

        Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    // 词法分析器
    static class Lexer {
        private final String input;
        private int pos;

        Lexer(String input) {
            this.input = input.replaceAll("\\s", ""); // 去空格
            this.pos = 0;
        }

        Token nextToken() {
            if (pos >= input.length()) {
                return new Token(TokenType.EOF, "");
            }

            char c = input.charAt(pos);
            if (Character.isDigit(c)) {
                StringBuilder num = new StringBuilder();
                while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
                    num.append(input.charAt(pos++));
                }
                return new Token(TokenType.NUMBER, num.toString());
            } else if (c == '&' && peek() == '&') {
                pos += 2;
                return new Token(TokenType.AND, "&&");
            } else if (c == '|' && peek() == '|') {
                pos += 2;
                return new Token(TokenType.OR, "||");
            } else if (c == '!') {
                pos++;
                return new Token(TokenType.NOT, "!");
            } else if (c == '(') {
                pos++;
                return new Token(TokenType.LPAREN, "(");
            } else if (c == ')') {
                pos++;
                return new Token(TokenType.RPAREN, ")");
            }
            throw new IllegalArgumentException("无效字符: " + c);
        }

        private char peek() {
            return pos + 1 < input.length() ? input.charAt(pos + 1) : '\0';
        }
    }

    // 解析器
    public static class Parser {
        private final Lexer lexer;
        private Token currentToken;
        private final Map<Integer, String> conditionMap;

        public Parser(String input, Map<Integer, String> conditionMap) {
            this.lexer = new Lexer(input);
            this.conditionMap = conditionMap;
            this.currentToken = lexer.nextToken();
        }

        public String parse() {
            String expr = parseExpression();
            if (currentToken.type != TokenType.EOF) {
                throw new IllegalArgumentException("表达式未结束");
            }
            return expr;
        }

        // 表达式: term (|| term)*
        private String parseExpression() {
            String left = parseTerm();
            while (currentToken.type == TokenType.OR) {
                currentToken = lexer.nextToken();
                String right = parseTerm();
                left = left + " || " + right;
            }
            return left;
        }

        // 项: factor (&& factor)*
        private String parseTerm() {
            String left = parseFactor();
            while (currentToken.type == TokenType.AND) {
                currentToken = lexer.nextToken();
                String right = parseFactor();
                left = left + " && " + right;
            }
            return left;
        }

        // 因子: !factor | (expression) | number
        private String parseFactor() {
            if (currentToken.type == TokenType.NOT) {
                currentToken = lexer.nextToken();
                String factor = parseFactor();
                return "!(" + factor + ")";
            } else if (currentToken.type == TokenType.LPAREN) {
                currentToken = lexer.nextToken();
                String expr = parseExpression();
                if (currentToken.type != TokenType.RPAREN) {
                    throw new IllegalArgumentException("缺少右括号");
                }
                currentToken = lexer.nextToken();
                return "(" + expr + ")";
            } else if (currentToken.type == TokenType.NUMBER) {
                String num = currentToken.value;
                currentToken = lexer.nextToken();
                return conditionMap.getOrDefault(Integer.parseInt(num), "true");
            }
            throw new IllegalArgumentException("无效因子: " + currentToken.value);
        }
    }
}