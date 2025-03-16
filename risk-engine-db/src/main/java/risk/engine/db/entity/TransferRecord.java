package risk.engine.db.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransferRecord {

    private Long id;

    private String sendAddress;

    private String receiveAddress;

    private BigDecimal amount;

    private BigDecimal uAmount;

    private String hash;

    private Integer height;

    private String chain;

    private String token;

    private BigDecimal fee;

    private Long transferTime;

    private LocalDateTime createdTime;

    private Integer status;

}