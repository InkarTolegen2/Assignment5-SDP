package insurance.payout;

public record ClaimData(long amount, String currency, int itemAgeYears, long deductible, long coverageLimit) {}
