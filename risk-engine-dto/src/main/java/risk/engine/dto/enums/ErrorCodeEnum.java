package risk.engine.dto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: X
 * @Date: 2025/4/11 20:44
 * @Version: 1.0
 */

@Getter
@AllArgsConstructor
public enum ErrorCodeEnum {

    // 系统级别异常
    SYSTEM_ERROR(500, "系统异常，请稍后重试"),

    // 业务相关异常
    PARAMETER_IS_NULL(1001, "非法请求参数为空"),
    RULE_EXECUTION_ERROR(1002, "规则引擎执行失败"),
    MESSAGE_SEND_FAIL(1003, "消息发送失败"),
    DATA_SAVE_ERROR(1004, "数据保存失败"),
    INCIDENT_EXIST(1005, "事件标识已存在"),
    INCIDENT_EXIST_METRIC(1006, "非法操作，事件提交请先解析"),
    INCIDENT_EXIST_RULE(1007, "非法操作，事件存在关联规则"),
    RULE_LOGIC_ILLEGAL(1008, "非法操作，逻辑表达式不合法"),
    ONLINE_STATUS_RULE(1009, "非法操作，上线状态规则不能删除"),
    ONLINE_STATUS_COUNTER_METRIC(1009, "非法操作，启用状态计数器不能删除"),
    ;


    private final int code;
    private final String message;
}
