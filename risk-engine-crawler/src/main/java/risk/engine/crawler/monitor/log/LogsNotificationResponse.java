package risk.engine.crawler.monitor.log;

import java.util.List;
/**
 * @Author: X
 * @Date: 2025/3/10 02:44
 * @Version: 1.0
 */

public class LogsNotificationResponse {

    private String jsonrpc;

    private String method;

    private Params params;

    // Getters and Setters

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public static class Params {

        private Result result;

        // Getters and Setters

        public Result getResult() {
            return result;
        }

        public void setResult(Result result) {
            this.result = result;
        }

        public static class Result {

            private Context context;

            private Value value;

            // Getters and Setters

            public Context getContext() {
                return context;
            }

            public void setContext(Context context) {
                this.context = context;
            }

            public Value getValue() {
                return value;
            }

            public void setValue(Value value) {
                this.value = value;
            }

            public static class Context {

                private long slot;

                // Getters and Setters

                public long getSlot() {
                    return slot;
                }

                public void setSlot(long slot) {
                    this.slot = slot;
                }
            }

            public static class Value {

                private String signature;

                private Object err; // 这里使用Object类型，因为err可能是null或者其他类型，具体类型需要根据实际情况确定

                private List<String> logs;

                // Getters and Setters

                public String getSignature() {
                    return signature;
                }

                public void setSignature(String signature) {
                    this.signature = signature;
                }

                public Object getErr() {
                    return err;
                }

                public void setErr(Object err) {
                    this.err = err;
                }

                public List<String> getLogs() {
                    return logs;
                }

                public void setLogs(List<String> logs) {
                    this.logs = logs;
                }
            }
        }
    }
}

