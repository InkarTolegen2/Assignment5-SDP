package insurance.payout;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PayoutStrategiesTest {
    private static ClaimData claim(long amount, int age) {
        return new ClaimData(amount, "KZT", age, 100_000, 5_000_000);
    }

    @Test
    void standardSubtractsDeductible() {
        assertEquals(900_000, new StandardPayout().calculate(claim(1_000_000, 0)));
    }

    @Test
    void standardIsCappedByCoverageLimit() {
        assertEquals(5_000_000, new StandardPayout().calculate(claim(9_000_000, 0)));
    }

    @Test
    void tieredPaysEverythingBelowThreshold() {
        assertEquals(500_000, new TieredPayout().calculate(claim(600_000, 0)));
    }

    @Test
    void tieredPaysSixtyPercentAboveThreshold() {
        assertEquals(2_140_000, new TieredPayout().calculate(claim(3_000_000, 0)));
    }

    @Test
    void depreciatedReducesValueByItemAge() {
        assertEquals(600_000, new DepreciatedPayout().calculate(claim(1_000_000, 3)));
    }

    @Test
    void depreciationStopsAtFloor() {
        assertEquals(200_000, new DepreciatedPayout().calculate(claim(1_000_000, 10)));
    }

    @Test
    void foreignCurrencyConvertsWithProviderRate() {
        PayoutStrategy strategy = new ForeignCurrencyPayout(currency -> 400.0);
        ClaimData usdClaim = new ClaimData(2_000, "USD", 0, 100_000, 5_000_000);
        assertEquals(700_000, strategy.calculate(usdClaim));
    }

    @Test
    void foreignCurrencyAsksProviderForTheClaimCurrency() {
        AtomicReference<String> asked = new AtomicReference<>();
        PayoutStrategy strategy = new ForeignCurrencyPayout(currency -> {
            asked.set(currency);
            return 1.0;
        });
        strategy.calculate(new ClaimData(1_000, "EUR", 0, 0, 5_000_000));
        assertEquals("EUR", asked.get());
    }
}
