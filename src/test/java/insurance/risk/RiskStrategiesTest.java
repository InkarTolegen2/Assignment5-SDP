package insurance.risk;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RiskStrategiesTest {
    private static RiskInput input(long amount, int claims, int days) {
        return new RiskInput(amount, 5_000_000, claims, days);
    }

    @Test
    void amountRatioIsPercentOfLimit() {
        assertEquals(50, new AmountRatioRisk().score(input(2_500_000, 0, 400)));
    }

    @Test
    void amountRatioIsCappedAtHundred() {
        assertEquals(100, new AmountRatioRisk().score(input(9_000_000, 0, 400)));
    }

    @Test
    void amountRatioWithoutLimitIsMaximumRisk() {
        assertEquals(100, new AmountRatioRisk().score(new RiskInput(1_000, 0, 0, 400)));
    }

    @Test
    void historyWithoutClaimsIsZero() {
        assertEquals(0, new ClaimHistoryRisk().score(input(1_000, 0, 400)));
    }

    @Test
    void historyAddsPointsPerClaim() {
        assertEquals(50, new ClaimHistoryRisk().score(input(1_000, 2, 400)));
    }

    @Test
    void historyIsCappedAtHundred() {
        assertEquals(100, new ClaimHistoryRisk().score(input(1_000, 9, 400)));
    }

    @Test
    void brandNewPolicyIsMaximumRisk() {
        assertEquals(100, new PolicyAgeRisk().score(input(1_000, 0, 0)));
    }

    @Test
    void policyRiskFallsWithAge() {
        assertEquals(70, new PolicyAgeRisk().score(input(1_000, 0, 90)));
    }

    @Test
    void policyRiskStopsAtFloor() {
        assertEquals(10, new PolicyAgeRisk().score(input(1_000, 0, 1_000)));
    }
}
