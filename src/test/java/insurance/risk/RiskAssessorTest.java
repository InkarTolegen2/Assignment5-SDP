package insurance.risk;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RiskAssessorTest {
    private final RiskInput input = new RiskInput(2_500_000, 5_000_000, 2, 90);

    @Test
    void callsTheStrategyItWasGiven() {
        assertEquals(7, new RiskAssessor(i -> 7).assess(input));
    }

    @Test
    void setStrategyReplacesTheAlgorithmAtRuntime() {
        RiskAssessor assessor = new RiskAssessor(new AmountRatioRisk());
        assertEquals(50, assessor.assess(input));

        assessor.setStrategy(new PolicyAgeRisk());
        assertEquals(70, assessor.assess(input));
    }
}
