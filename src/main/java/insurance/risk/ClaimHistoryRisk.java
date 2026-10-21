package insurance.risk;

public class ClaimHistoryRisk implements RiskStrategy {
    private static final int POINTS_PER_CLAIM = 25;

    @Override
    public int score(RiskInput input) {
        int claims = Math.max(0, input.claimsLast12Months());
        return Math.min(100, claims * POINTS_PER_CLAIM);
    }
}
