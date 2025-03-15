package risk.engine.dto.dto.block;

import lombok.Data;

import java.util.List;
/**
 * Solana区块交易信息响应类
 * @Author: X
 * @Date: 2025/3/15 01:03
 * @Version: 1.0
 */
@Data
public class SolanaTransactionDTO {

    /**
     * 交易元数据
     */
    private Meta meta;

    /**
     * 交易信息
     */
    private Transaction transaction;

    /**
     * 版本信息
     */
    private String version;

    /**
     * 交易元数据类
     */
    public static class Meta {
        /**
         * 消耗的计算单元数量
         */
        private int computeUnitsConsumed;

        /**
         * 错误信息（如果有的话）
         */
        private Object err;

        /**
         * 交易费用
         */
        private int fee;

        /**
         * 内部指令列表（如果有的话）
         */
        private List<String> innerInstructions;

        /**
         * 加载的地址信息
         */
        private LoadedAddresses loadedAddresses;

        /**
         * 日志消息列表
         */
        private List<String> logMessages;

        /**
         * 交易后的余额列表
         */
        private List<Long> postBalances;

        /**
         * 交易后的代币余额列表（如果有的话）
         */
        private List<Object> postTokenBalances;

        /**
         * 交易前的余额列表
         */
        private List<Long> preBalances;

        /**
         * 交易前的代币余额列表（如果有的话）
         */
        private List<Object> preTokenBalances;

        /**
         * 奖励信息
         */
        private Object rewards;

        /**
         * 交易状态
         */
        private Status status;

        // Getters and Setters for Meta fields (省略，以保持简洁)

        /**
         * 加载的地址信息类
         */
        public static class LoadedAddresses {
            /**
             * 只读地址列表
             */
            private List<String> readonly;

            /**
             * 可写地址列表
             */
            private List<String> writable;

            // Getters and Setters for LoadedAddresses fields (省略，以保持简洁)
        }

        /**
         * 交易状态类
         */
        public static class Status {
            /**
             * 状态信息（通常为null，表示成功）
             */
            private Object Ok;
        }
    }

    /**
     * 交易信息类
     */
    public static class Transaction {
        /**
         * 交易消息
         */
        private Message message;

        /**
         * 交易签名列表
         */
        private List<String> signatures;

        /**
         * 交易消息类
         */
        public static class Message {
            /**
             * 账户密钥列表
             */
            private List<String> accountKeys;

            /**
             * 消息头
             */
            private Header header;

            /**
             * 指令列表
             */
            private List<Instruction> instructions;

            /**
             * 最近的区块哈希
             */
            private String recentBlockhash;

            /**
             * 消息头类
             */
            public static class Header {
                /**
                 * 只读签名账户数量
                 */
                private int numReadonlySignedAccounts;

                /**
                 * 只读未签名账户数量
                 */
                private int numReadonlyUnsignedAccounts;

                /**
                 * 所需的签名数量
                 */
                private int numRequiredSignatures;

            }

            /**
             * 指令类
             */
            public static class Instruction {
                /**
                 * 指令涉及的账户索引列表
                 */
                private List<Integer> accounts;

                /**
                 * 指令数据
                 */
                private String data;

                /**
                 * 程序ID索引
                 */
                private int programIdIndex;

                /**
                 * 栈高度（通常为null）
                 */
                private Object stackHeight;
            }
        }
    }
}

