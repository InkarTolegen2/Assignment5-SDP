package insurance.risk;

public class AmountRatioRisk implements RiskStrategy {
    @Override
    public int score(RiskInput input) {
        if (input.coverageLimit() <= 0) {
            return 100;
        }
        long percentOfLimit = input.claimAmount() * 100 / input.coverageLimit();
        return (int) Math.min(100, Math.max(0, percentOfLimit));
    }
}
