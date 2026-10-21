package insurance.risk;

public record RiskInput(long claimAmount, long coverageLimit,
                        int claimsLast12Months, int daysSincePolicyStart) {}
