package insurance.payout;

public class TieredPayout implements PayoutStrategy{
    private static final long FULL_COVER_UP_TO = 1_000_000;
    private static final long PERCENT_ABOVE = 60;

    @Override 
    public long calculate(ClaimData claim) {
        long eligible = Math.max(0, claim.amount() - claim.deductible());
        long fullPart = Math.min(eligible, FULL_COVER_UP_TO);
        long rest = eligible - fullPart;
        return PayoutLimits.apply(fullPart + rest * PERCENT_ABOVE / 100, claim);
    }
}
