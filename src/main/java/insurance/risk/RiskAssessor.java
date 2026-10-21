package insurance.risk;

public class RiskAssessor {
    private RiskStrategy strategy;

    public RiskAssessor(RiskStrategy strategy) { this.strategy = strategy; }

    public void setStrategy(RiskStrategy strategy) { this.strategy = strategy; }

    public int assess(RiskInput input) {
        return strategy.score(input);
    }
}
