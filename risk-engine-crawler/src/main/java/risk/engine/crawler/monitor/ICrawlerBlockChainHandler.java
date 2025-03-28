package risk.engine.crawler.monitor;

import risk.engine.dto.dto.block.ChainTransferDTO;

import java.io.IOException;
import java.util.List;

/**
 * 定义统一的解析器
 * @Author: X
 * @Date: 2025/3/16 16:40
 * @Version: 1.0
 */
public interface ICrawlerBlockChainHandler {

    List<ChainTransferDTO> getTransactions() throws IOException;
}
