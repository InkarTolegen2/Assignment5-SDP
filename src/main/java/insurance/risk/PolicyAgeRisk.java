package insurance.risk;

public class PolicyAgeRisk implements RiskStrategy {
    private static final int FLOOR = 10;
    private static final int DAYS_PER_POINT = 3;

    @Override
    public int score(RiskInput input) {
        int decayed = 100 - input.daysSincePolicyStart() / DAYS_PER_POINT;
        return Math.max(FLOOR, Math.min(100, decayed));
    }
}
