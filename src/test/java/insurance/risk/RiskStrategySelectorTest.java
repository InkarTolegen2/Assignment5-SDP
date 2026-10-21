package insurance.risk;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RiskStrategySelectorTest {
    private final RiskStrategySelector selector = new RiskStrategySelector();

    private Class<?> chosenFor(int claims, int days) {
        return selector.select(new RiskInput(1_000_000, 5_000_000, claims, days)).getClass();
    }

    @Test
    void newPolicyUsesPolicyAgeRisk() {
        assertEquals(PolicyAgeRisk.class, chosenFor(0, 10));
    }

    @Test
    void newPolicyRuleWinsOverClaimHistoryRule() {
        assertEquals(PolicyAgeRisk.class, chosenFor(5, 10));
    }

    @Test
    void policyOfExactlyThirtyDaysIsNotNew() {
        assertEquals(AmountRatioRisk.class, chosenFor(0, 30));
    }

    @Test
    void frequentClaimantUsesClaimHistoryRisk() {
        assertEquals(ClaimHistoryRisk.class, chosenFor(3, 400));
    }

    @Test
    void twoClaimsAlreadyCountAsFrequent() {
        assertEquals(ClaimHistoryRisk.class, chosenFor(2, 400));
    }

    @Test
    void ordinaryCustomerUsesAmountRatioRisk() {
        assertEquals(AmountRatioRisk.class, chosenFor(1, 400));
    }
}
