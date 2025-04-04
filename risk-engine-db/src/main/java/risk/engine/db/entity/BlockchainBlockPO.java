package risk.engine.db.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BlockchainBlockPO {

    private Long id;

    private String hash;

    private Integer height;

    private Integer version;

    private Long timestamp;

    private Integer txCount;

    private Integer size;

    private Integer weight;

    private String merkleRoot;

    private String previousBlockHash;

    private Long medianTime;

    private Long nonce;

    private Integer bits;

    private BigDecimal difficulty;

    private String coin;

    private String chain;

    private LocalDateTime createTime;

}