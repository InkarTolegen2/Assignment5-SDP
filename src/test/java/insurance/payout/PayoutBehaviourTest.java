package insurance.payout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PayoutBehaviourTest {
    private ClaimData claim(long amount, String currency, int age) {
        return new ClaimData(amount, currency, age, 100_000, 5_000_000);
    }

    @Test
    void standardSubtractsDeductible() {
        assertEquals(900_000, Payouts.calculate("STANDARD", claim(1_000_000, "KZT", 0)));
    }

    @Test
    void standardIsCappedByCoverageLimit() {
        assertEquals(5_000_000, Payouts.calculate("STANDARD", claim(9_000_000, "KZT", 0)));
    }

    @Test
    void standardIsNeverNegative() {
        assertEquals(0, Payouts.calculate("STANDARD", claim(50_000, "KZT", 0)));
    }

    @Test
    void tieredPaysEverythingBelowThreshold() {
        assertEquals(500_000, Payouts.calculate("TIERED", claim(600_000, "KZT", 0)));
    }

    @Test
    void tieredPaysPartAboveThreshold() {
        assertEquals(2_140_000, Payouts.calculate("TIERED", claim(3_000_000, "KZT", 0)));
    }

    @Test
    void depreciatedReducesValueByItemAge() {
        assertEquals(600_000, Payouts.calculate("DEPRECIATED", claim(1_000_000, "KZT", 3)));
    }

    @Test
    void depreciationStopsAtFloor() {
        assertEquals(200_000, Payouts.calculate("DEPRECIATED", claim(1_000_000, "KZT", 10)));
    }

    @Test
    void foreignCurrencyIsConvertedWithUsdRate() {
        assertEquals(900_000, Payouts.calculate("FOREIGN_CURRENCY", claim(2_000, "USD", 0)));
    }

    @Test
    void foreignCurrencyUsesRateOfItsOwnCurrency() {
        assertEquals(1_000_000, Payouts.calculate("FOREIGN_CURRENCY", claim(2_000, "EUR", 0)));
    }
}