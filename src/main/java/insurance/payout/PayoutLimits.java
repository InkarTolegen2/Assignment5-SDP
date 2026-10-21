package insurance.payout;

final class PayoutLimits {
    private PayoutLimits() {}

    static long apply(long payout, ClaimData claim) {
        return Math.min(claim.coverageLimit(), Math.max(0, payout));
    }
}
