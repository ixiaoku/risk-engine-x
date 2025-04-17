package risk.engine.dto.constant;

import java.util.List;

/**
 * @Author: X
 * @Date: 2025/4/9 00:15
 * @Version: 1.0
 */
public interface BinanceConstant {

    String COUNTER = "risk:counter:";

    String INTERVAL_15M = "15m";

    List<String> SYMBOLS = List.of("BTCUSDT", "ETHUSDT", "BNBUSDT", "XRPUSDT", "ADAUSDT", "SOLUSDT", "DOGEUSDT");

    Integer LIMIT_100 = 100;
}
