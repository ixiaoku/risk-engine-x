package risk.engine.rest.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import okhttp3.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @Author: X
 * @Date: 2025/6/5 00:26
 * @Version: 1.0
 */
public class ExchangeMockDataGenerator {
    private static final Random random = new Random();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final OkHttpClient client = new OkHttpClient();

    public static void main(String[] args) throws IOException {
        // 风控系统API地址
        String riskControlApiUrl = "http://your-risk-control-system/api/analyze";

        // 生成并发送多种场景的mock数据
        for (int i = 0; i < 10; i++) {
            Object data;
            switch (random.nextInt(6)) {
                case 0: data = generateRegistration(); break;
                case 1: data = generateLogin(); break;
                case 2: data = generateDeposit(); break;
                case 3: data = generateWithdrawal(); break;
                case 4: data = generateSpotOrder(); break;
                case 5: data = generateFuturesOrder(); break;
                default: data = generateTrade(); break;
            }
            sendToRiskControl(riskControlApiUrl, data);
        }
    }

    // 生成用户注册数据
    private static Registration generateRegistration() {
        Registration reg = new Registration();
        reg.setUserId(UUID.randomUUID().toString());
        reg.setEmail("user" + random.nextInt(10000) + "@example.com");
        reg.setPhone("+86" + (100000000 + random.nextInt(900000000)));
        reg.setRegisterTime(System.currentTimeMillis());
        reg.setIpAddress(randomIp());
        reg.setDeviceId(UUID.randomUUID().toString());
        reg.setCountryCode(randomCountry());
        reg.setReferralId(random.nextBoolean() ? UUID.randomUUID().toString() : null);
        reg.setKycStatus(randomKycStatus());
        reg.setAccountType(randomAccountType());
        return reg;
    }

    // 生成登录数据
    private static Login generateLogin() {
        Login login = new Login();
        login.setUserId(UUID.randomUUID().toString());
        login.setLoginTime(System.currentTimeMillis());
        login.setIpAddress(randomIp());
        login.setDeviceId(UUID.randomUUID().toString());
        login.setLoginType(randomLoginType());
        login.setStatus(randomLoginStatus());
        login.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
        login.setTwoFactorAuth(random.nextBoolean());
        login.setListenKey(UUID.randomUUID().toString());
        return login;
    }

    // 生成充值数据
    private static Deposit generateDeposit() {
        Deposit deposit = new Deposit();
        deposit.setUserId(UUID.randomUUID().toString());
        deposit.setTxId(UUID.randomUUID().toString().replace("-", ""));
        deposit.setCurrency(randomCurrency());
        deposit.setAmount(new BigDecimal(random.nextDouble() * 10).setScale(8, BigDecimal.ROUND_DOWN));
        deposit.setAddress("0x" + UUID.randomUUID().toString().replace("-", ""));
        deposit.setDepositTime(System.currentTimeMillis());
        deposit.setNetwork(randomNetwork());
        deposit.setStatus(randomDepositStatus());
        deposit.setConfirmations(random.nextInt(12));
        deposit.setTag(random.nextBoolean() ? String.valueOf(random.nextInt(1000000)) : null);
        deposit.setSourceAddress(random.nextBoolean() ? "0x" + UUID.randomUUID().toString().replace("-", "") : null);
        return deposit;
    }

    // 生成提币数据
    private static Withdrawal generateWithdrawal() {
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setUserId(UUID.randomUUID().toString());
        withdrawal.setTxId(UUID.randomUUID().toString().replace("-", ""));
        withdrawal.setCurrency(randomCurrency());
        withdrawal.setAmount(new BigDecimal(random.nextDouble() * 10).setScale(8, BigDecimal.ROUND_DOWN));
        withdrawal.setAddress("0x" + UUID.randomUUID().toString().replace("-", ""));
        withdrawal.setWithdrawalTime(System.currentTimeMillis());
        withdrawal.setNetwork(randomNetwork());
        withdrawal.setStatus(randomWithdrawStatus());
        withdrawal.setFee(new BigDecimal(random.nextDouble() * 0.01).setScale(8, BigDecimal.ROUND_DOWN));
        withdrawal.setFeeCurrency(withdrawal.getCurrency());
        withdrawal.setTag(random.nextBoolean() ? String.valueOf(random.nextInt(1000000)) : null);
        withdrawal.setWithdrawOrderId(UUID.randomUUID().toString());
        withdrawal.setClientWithdrawId(random.nextBoolean() ? UUID.randomUUID().toString() : null);
        return withdrawal;
    }

    // 生成现货交易订单
    private static SpotOrder generateSpotOrder() {
        SpotOrder order = new SpotOrder();
        order.setUserId(UUID.randomUUID().toString());
        order.setOrderId(UUID.randomUUID().toString());
        order.setClientOrderId(random.nextBoolean() ? UUID.randomUUID().toString() : null);
        order.setSymbol(randomSymbol());
        order.setSide(randomSide());
        order.setType(randomOrderType());
        order.setPrice(new BigDecimal(random.nextDouble() * 50000).setScale(2, BigDecimal.ROUND_DOWN));
        order.setQuantity(new BigDecimal(random.nextDouble() * 100).setScale(8, BigDecimal.ROUND_DOWN));
        order.setQuoteOrderQty(random.nextBoolean() ? new BigDecimal(random.nextDouble() * 100000).setScale(2, BigDecimal.ROUND_DOWN) : null);
        order.setOrderTime(System.currentTimeMillis());
        order.setStatus(randomOrderStatus());
        order.setTimeInForce(randomTimeInForce());
        order.setIcebergQty(random.nextBoolean() ? new BigDecimal(random.nextDouble() * 10).setScale(8, BigDecimal.ROUND_DOWN) : null);
        order.setSelfTradePreventionMode(randomStpMode());
        order.setCommission(new BigDecimal(random.nextDouble() * 0.01).setScale(8, BigDecimal.ROUND_DOWN));
        order.setCommissionAsset(randomCurrency());
        return order;
    }

    // 生成合约交易订单
    private static FuturesOrder generateFuturesOrder() {
        FuturesOrder order = new FuturesOrder();
        order.setUserId(UUID.randomUUID().toString());
        order.setOrderId(UUID.randomUUID().toString());
        order.setClientOrderId(random.nextBoolean() ? UUID.randomUUID().toString() : null);
        order.setSymbol(randomSymbol() + "_PERP");
        order.setSide(randomSide());
        order.setPositionSide(randomPositionSide());
        order.setType(randomOrderType());
        order.setPrice(new BigDecimal(random.nextDouble() * 50000).setScale(2, BigDecimal.ROUND_DOWN));
        order.setQuantity(new BigDecimal(random.nextDouble() * 100).setScale(8, BigDecimal.ROUND_DOWN));
        order.setLeverage(random.nextInt(100) + 1);
        order.setMarginMode(randomMarginMode());
        order.setOrderTime(System.currentTimeMillis());
        order.setStatus(randomOrderStatus());
        order.setTimeInForce(randomTimeInForce());
        order.setReduceOnly(random.nextBoolean());
        order.setStopPrice(random.nextBoolean() ? new BigDecimal(random.nextDouble() * 50000).setScale(2, BigDecimal.ROUND_DOWN) : null);
        order.setWorkingType(randomWorkingType());
        return order;
    }

    // 生成成交记录
    private static Trade generateTrade() {
        Trade trade = new Trade();
        trade.setTradeId(UUID.randomUUID().toString());
        trade.setOrderId(UUID.randomUUID().toString());
        trade.setUserId(UUID.randomUUID().toString());
        trade.setSymbol(randomSymbol());
        trade.setSide(randomSide());
        trade.setPrice(new BigDecimal(random.nextDouble() * 50000).setScale(2, BigDecimal.ROUND_DOWN));
        trade.setQuantity(new BigDecimal(random.nextDouble() * 100).setScale(8, BigDecimal.ROUND_DOWN));
        trade.setTradeTime(System.currentTimeMillis());
        trade.setFee(new BigDecimal(random.nextDouble() * 0.01).setScale(8, BigDecimal.ROUND_DOWN));
        trade.setFeeCurrency(randomCurrency());
        trade.setIsMaker(random.nextBoolean());
        trade.setIsBuyerMaker(random.nextBoolean());
        trade.setRealizedPnl(random.nextBoolean() ? new BigDecimal(random.nextDouble() * 1000).setScale(2, BigDecimal.ROUND_DOWN) : null);
        return trade;
    }

    // 发送数据到风控系统
    private static void sendToRiskControl(String url, Object data) throws IOException {
        String json = objectMapper.writeValueAsString(data);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Data sent successfully: " + json);
                System.out.println("Response: " + response.body().string());
            } else {
                System.err.println("Failed to send data: " + response.code());
            }
        }
    }

    // 辅助方法：随机生成数据
    private static String randomIp() {
        return random.nextInt(256) + "." + random.nextInt(256) + "." + random.nextInt(256) + "." + random.nextInt(256);
    }

    private static String randomCurrency() {
        List<String> currencies = Arrays.asList("BTC", "ETH", "USDT", "BNB", "ADA", "XRP");
        return currencies.get(random.nextInt(currencies.size()));
    }

    private static String randomNetwork() {
        List<String> networks = Arrays.asList("ERC20", "TRC20", "BEP20", "BTC");
        return networks.get(random.nextInt(networks.size()));
    }

    private static String randomDepositStatus() {
        List<String> statuses = Arrays.asList("PENDING", "CONFIRMED", "FAILED");
        return statuses.get(random.nextInt(statuses.size()));
    }

    private static String randomWithdrawStatus() {
        List<String> statuses = Arrays.asList("PENDING", "APPROVED", "REJECTED");
        return statuses.get(random.nextInt(statuses.size()));
    }

    private static String randomSymbol() {
        List<String> symbols = Arrays.asList("BTCUSDT", "ETHUSDT", "BNBUSDT", "ADAUSDT", "XRPUSDT");
        return symbols.get(random.nextInt(symbols.size()));
    }

    private static String randomSide() {
        return random.nextBoolean() ? "BUY" : "SELL";
    }

    private static String randomOrderType() {
        List<String> types = Arrays.asList("LIMIT", "MARKET", "STOP_LOSS", "TAKE_PROFIT");
        return types.get(random.nextInt(types.size()));
    }

    private static String randomOrderStatus() {
        List<String> statuses = Arrays.asList("NEW", "PARTIALLY_FILLED", "FILLED", "CANCELED");
        return statuses.get(random.nextInt(statuses.size()));
    }

    private static String randomTimeInForce() {
        List<String> times = Arrays.asList("GTC", "IOC", "FOK");
        return times.get(random.nextInt(times.size()));
    }

    private static String randomStpMode() {
        List<String> modes = Arrays.asList("DECREMENT", "CANCEL_MAKER", "CANCEL_TAKER", "CANCEL_BOTH");
        return modes.get(random.nextInt(modes.size()));
    }

    private static String randomCountry() {
        List<String> countries = Arrays.asList("CN", "US", "JP", "KR", "EU");
        return countries.get(random.nextInt(countries.size()));
    }

    private static String randomKycStatus() {
        List<String> statuses = Arrays.asList("VERIFIED", "PENDING", "NONE");
        return statuses.get(random.nextInt(statuses.size()));
    }

    private static String randomAccountType() {
        List<String> types = Arrays.asList("SPOT", "FUTURES", "MARGIN");
        return types.get(random.nextInt(types.size()));
    }

    private static String randomLoginType() {
        List<String> types = Arrays.asList("EMAIL", "PHONE", "API");
        return types.get(random.nextInt(types.size()));
    }

    private static String randomLoginStatus() {
        List<String> statuses = Arrays.asList("SUCCESS", "FAILED");
        return statuses.get(random.nextInt(statuses.size()));
    }

    private static String randomPositionSide() {
        List<String> sides = Arrays.asList("LONG", "SHORT");
        return sides.get(random.nextInt(sides.size()));
    }

    private static String randomMarginMode() {
        List<String> modes = Arrays.asList("CROSS", "ISOLATED");
        return modes.get(random.nextInt(modes.size()));
    }

    private static String randomWorkingType() {
        List<String> types = Arrays.asList("CONTRACT_PRICE", "MARK_PRICE");
        return types.get(random.nextInt(types.size()));
    }

    // 数据类定义
    @Data
    static class Registration {
        private String userId;
        private String email;
        private String phone;
        private Long registerTime;
        private String ipAddress;
        private String deviceId;
        private String countryCode;
        private String referralId;
        private String kycStatus;
        private String accountType;
        // Getters and Setters
        // ...（为节省空间，省略Getter/Setter，可自动生成）
    }

    @Data
    static class Login {
        private String userId;
        private Long loginTime;
        private String ipAddress;
        private String deviceId;
        private String loginType;
        private String status;
        private String userAgent;
        private Boolean twoFactorAuth;
        private String listenKey;
        // Getters and Setters
    }

    @Data
    static class Deposit {
        private String userId;
        private String txId;
        private String currency;
        private BigDecimal amount;
        private String address;
        private Long depositTime;
        private String network;
        private String status;
        private Integer confirmations;
        private String tag;
        private String sourceAddress;
        // Getters and Setters
    }

    @Data
    static class Withdrawal {
        private String userId;
        private String txId;
        private String currency;
        private BigDecimal amount;
        private String address;
        private Long withdrawalTime;
        private String network;
        private String status;
        private BigDecimal fee;
        private String feeCurrency;
        private String tag;
        private String withdrawOrderId;
        private String clientWithdrawId;
        // Getters and Setters
    }

    @Data
    static class SpotOrder {
        private String userId;
        private String orderId;
        private String clientOrderId;
        private String symbol;
        private String side;
        private String type;
        private BigDecimal price;
        private BigDecimal quantity;
        private BigDecimal quoteOrderQty;
        private Long orderTime;
        private String status;
        private String timeInForce;
        private BigDecimal icebergQty;
        private String selfTradePreventionMode;
        private BigDecimal commission;
        private String commissionAsset;
        // Getters and Setters
    }

    @Data
    static class FuturesOrder {
        private String userId;
        private String orderId;
        private String clientOrderId;
        private String symbol;
        private String side;
        private String positionSide;
        private String type;
        private BigDecimal price;
        private BigDecimal quantity;
        private Integer leverage;
        private String marginMode;
        private Long orderTime;
        private String status;
        private String timeInForce;
        private Boolean reduceOnly;
        private BigDecimal stopPrice;
        private String workingType;
        // Getters and Setters
    }

    @Data
    static class Trade {
        private String tradeId;
        private String orderId;
        private String userId;
        private String symbol;
        private String side;
        private BigDecimal price;
        private BigDecimal quantity;
        private Long tradeTime;
        private BigDecimal fee;
        private String feeCurrency;
        private Boolean isMaker;
        private Boolean isBuyerMaker;
        private BigDecimal realizedPnl;
        // Getters and Setters
    }
}
