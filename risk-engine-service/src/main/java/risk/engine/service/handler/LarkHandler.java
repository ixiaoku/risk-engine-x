package risk.engine.service.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Component;
import risk.engine.common.util.OkHttpUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: X
 * @Date: 2025/4/22 22:26
 * @Version: 1.0
 */
@Component
public class LarkHandler {

    public boolean sendMessage(String message, String url) {
        Map<String, Object> map = new HashMap<>();
        map.put("msg_type", "text");
        Map<String, Object> msgMap = new HashMap<>();
        msgMap.put("text", message);
        map.put("content", msgMap);
        String result = OkHttpUtil.postJson(url, JSON.toJSONString(map));
        JSONObject jsonObject = JSON.parseObject(result);
        return Objects.nonNull(jsonObject) && "0".equals(jsonObject.getString("code"));
    }

}
