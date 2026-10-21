package insurance.payout;

public class PayoutCalculator {
    private PayoutStrategy strategy;

    public PayoutCalculator(PayoutStrategy strategy) { this.strategy = strategy; }

    public void setStrategy(PayoutStrategy strategy) { this.strategy = strategy; }

    public long calculate(ClaimData claim) {
        return strategy.calculate(claim);
    }
}