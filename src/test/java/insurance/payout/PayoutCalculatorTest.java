package insurance.payout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PayoutCalculatorTest {
    private final ClaimData claim = new ClaimData(1_000_000, "KZT", 0, 100_000, 5_000_000);

    @Test
    void callsTheStrategyItWasGiven() {
        assertEquals(42, new PayoutCalculator(c -> 42).calculate(claim));
    }

    @Test
    void setStrategyReplacesTheAlgorithmAtRuntime() {
        PayoutCalculator calculator = new PayoutCalculator(new StandardPayout());
        assertEquals(900_000, calculator.calculate(claim));

        calculator.setStrategy(new DepreciatedPayout());
        ClaimData oldItem = new ClaimData(1_000_000, "KZT", 3, 100_000, 5_000_000);
        assertEquals(600_000, calculator.calculate(oldItem));
    }
}
