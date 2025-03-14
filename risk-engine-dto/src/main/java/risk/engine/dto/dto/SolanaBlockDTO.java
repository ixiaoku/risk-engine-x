package risk.engine.dto.dto;

import lombok.Data;

/**
 * sol区块信息
 * {"blockHeight":355294817,"blockTime":1741970719,"blockhash":"5maS7xXSrqN8U5FYtbfBc4wWMLBPe62cEb6whbmDBZPM","parentSlot":367315028,"previousBlockhash":"33dpWQmQGZM6c3TYYFPqbzWh1eM7iYqoowT35UykPW91"}
 * @Author: X
 * @Date: 2025/3/15 00:42
 * @Version: 1.0
 */
@Data
public class SolanaBlockDTO {

    /**
     * 区块高度
     */
    private long blockHeight;
    /**
     * 时间戳
     */
    private long blockTime;
    /**
     * 区块哈希
     */
    private String blockhash;
    /**
     * 父区块哈希
     */
    private String previousBlockhash;
    /**
     * 父区块的slot
     */
    private String parentSlot;

}
