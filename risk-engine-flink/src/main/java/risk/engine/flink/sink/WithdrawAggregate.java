package risk.engine.flink.sink;

import lombok.Data;

@Data
public class WithdrawAggregate {
    private String uid;
    private double totalAmount;

    public WithdrawAggregate(String uid, double totalAmount) {
        this.uid = uid;
        this.totalAmount = totalAmount;
    }
}
