package risk.engine.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: X
 * @Date: 2025/3/16 22:26
 * @Version: 1.0
 */
@Slf4j
@Component
public class ApplicationContextUtil {

    @Resource
    private ApplicationContext applicationContext;

    public Object getBeanByClassName(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return applicationContext.getBean(clazz);
        } catch (Exception e) {
            log.error("错误信息： {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
