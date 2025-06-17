package risk.engine.rest.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import risk.engine.dto.exception.RiskException;
import risk.engine.dto.vo.ResponseResult;

/**
 * @Author: X
 * @Date: 2025/4/11 20:38
 * @Version: 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 业务异常
    @ExceptionHandler(RiskException.class)
    public ResponseResult<?> handleBizException(RiskException ex) {
        log.error("业务异常：{}", ex.getMessage(), ex);
        return ResponseResult.fail(ex.getCode(), ex.getMessage());
    }

    // 全局异常
    @ExceptionHandler(Exception.class)
    public ResponseResult<?> handleException(Exception ex) {
        log.error("系统异常：{}", ex.getMessage(), ex);
        return ResponseResult.fail(500, "系统内部错误，请联系管理员");
    }
}
