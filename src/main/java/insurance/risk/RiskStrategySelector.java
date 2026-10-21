package insurance.risk;

public class RiskStrategySelector {
    private static final int NEW_POLICY_DAYS = 30;
    private static final int FREQUENT_CLAIMS = 2;

    public RiskStrategy select(RiskInput input) {
        if (input.daysSincePolicyStart() < NEW_POLICY_DAYS) {
            return new PolicyAgeRisk();
        }
        if (input.claimsLast12Months() >= FREQUENT_CLAIMS) {
            return new ClaimHistoryRisk();
        }
        return new AmountRatioRisk();
    }
}
