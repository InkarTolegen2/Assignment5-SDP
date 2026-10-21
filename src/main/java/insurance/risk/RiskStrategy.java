package insurance.risk;

public interface RiskStrategy {
    /** Fraud risk from 0 (safe) to 100 (very suspicious). */
    int score(RiskInput input);
}
