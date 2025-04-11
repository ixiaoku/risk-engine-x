package risk.engine.rest.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import risk.engine.dto.exception.RiskException;
import risk.engine.dto.vo.ResponseVO;

/**
 * @Author: X
 * @Date: 2025/4/11 20:38
 * @Version: 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RiskException.class)
    public ResponseVO handleBizException(RiskException ex) {
        log.error("错误信息：{}", ex.getMessage(), ex);
        return ResponseVO.fail(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseVO handleException(Exception ex) {
        log.error("错误信息：{}", ex.getMessage(), ex);
        return ResponseVO.fail("系统异常：" + ex.getMessage());
    }
}
