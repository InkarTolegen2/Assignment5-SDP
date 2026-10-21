package insurance.payout;

public class DepreciatedPayout implements PayoutStrategy {
    private static final int PERCENT_PER_YEAR = 10;
    private static final int MAX_DEPRECIATION = 70;

    @Override
    public long calculate(ClaimData claim) {
        int depreciation = Math.min(MAX_DEPRECIATION, claim.itemAgeYears() * PERCENT_PER_YEAR);
        long value = claim.amount() * (100 - depreciation) / 100;
        return PayoutLimits.apply(value - claim.deductible(), claim);
    }
}