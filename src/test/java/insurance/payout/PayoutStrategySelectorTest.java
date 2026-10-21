package insurance.payout;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PayoutStrategySelectorTest {
    private final PayoutStrategySelector selector = new PayoutStrategySelector(
            new FixedRateProvider(Map.of("KZT", 1.0, "USD", 500.0)));

    private Class<?> chosenFor(long amount, String currency, int age) {
        ClaimData claim = new ClaimData(amount, currency, age, 100_000, 5_000_000);
        return selector.select(claim).getClass();
    }

    @Test
    void foreignCurrencyClaimUsesForeignCurrencyPayout() {
        assertEquals(ForeignCurrencyPayout.class, chosenFor(1_000, "USD", 0));
    }

    @Test
    void usedItemUsesDepreciatedPayout() {
        assertEquals(DepreciatedPayout.class, chosenFor(500_000, "KZT", 2));
    }

    @Test
    void largeNewClaimUsesTieredPayout() {
        assertEquals(TieredPayout.class, chosenFor(2_000_000, "KZT", 0));
    }

    @Test
    void smallNewClaimUsesStandardPayout() {
        assertEquals(StandardPayout.class, chosenFor(500_000, "KZT", 0));
    }

    @Test
    void claimOfExactlyOneMillionIsStillStandard() {
        assertEquals(StandardPayout.class, chosenFor(1_000_000, "KZT", 0));
    }

    @Test
    void currencyRuleWinsOverItemAgeRule() {
        assertEquals(ForeignCurrencyPayout.class, chosenFor(1_000, "USD", 5));
    }
}
