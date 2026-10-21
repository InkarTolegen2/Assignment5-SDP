package insurance.payout;

public interface PayoutStrategy {
    long calculate(ClaimData claim);
}
