package risk.engine.rest.filter;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * @Author: X
 * @Date: 2025/4/18 15:43
 * @Version: 1.0
 */
@Component
public class RequestIdFilter implements Filter {

    private static final String REQUEST_ID_KEY = "request_id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, javax.servlet.ServletException {
        try {
            // 生成唯一的 request_id
            long nanoTime = System.nanoTime();
            String requestId = "MDC:" + UUID.randomUUID() + ":" + nanoTime;
            // 注入 MDC
            MDC.put(REQUEST_ID_KEY, requestId);
            // 添加 request_id 到响应头（可选，便于调试）
            if (request instanceof HttpServletRequest) {
                ((HttpServletResponse) response).setHeader("X-Request-ID", requestId);
            }
            // 继续处理请求
            chain.doFilter(request, response);
        } finally {
            // 清理 MDC，避免线程复用导致上下文污染
            MDC.clear();
        }
    }
}
