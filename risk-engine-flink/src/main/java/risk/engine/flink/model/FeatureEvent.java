package risk.engine.flink.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author: X
 * @Date: 2025/5/17 18:01
 * @Version: 1.0
 */
@Data
public class FeatureEvent {
    @JsonProperty("incident_code")
    private String incidentCode;

    @JsonProperty("uid")
    private String uid;

    @JsonProperty("condition_script")
    private String conditionScript;

    @JsonProperty("attributes")
    private Map<String, Object> attributes;

    @JsonProperty("metric_codes")
    private List<String> metricCodes;

}
