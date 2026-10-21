package insurance.payout;

public class StandardPayout implements PayoutStrategy  {
    @Override public long calculate(ClaimData claim) {
        return PayoutLimits.apply(claim.amount() - claim.deductible(), claim);
    }
}
