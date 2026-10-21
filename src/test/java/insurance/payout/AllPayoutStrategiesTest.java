package insurance.payout;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AllPayoutStrategiesTest {
    static Stream<Arguments> strategies() {
        ExchangeRateProvider rates = new FixedRateProvider(Map.of("KZT", 1.0, "USD", 500.0));
        return Stream.of(
                Arguments.of("standard", new StandardPayout()),
                Arguments.of("tiered", new TieredPayout()),
                Arguments.of("depreciated", new DepreciatedPayout()),
                Arguments.of("foreign currency", new ForeignCurrencyPayout(rates)));
    }

    @ParameterizedTest(name = "{0}: payout is never negative and never above the coverage limit")
    @MethodSource("strategies")
    void payoutStaysBetweenZeroAndLimit(String name, PayoutStrategy strategy) {
        long[] amounts = {0, 50_000, 100_000, 1_000_000, 50_000_000};
        int[] ages = {0, 5, 20};
        long[] deductibles = {0, 100_000, 10_000_000};
        for (long amount : amounts) {
            for (int age : ages) {
                for (long deductible : deductibles) {
                    ClaimData claim = new ClaimData(amount, "KZT", age, deductible, 5_000_000);
                    long payout = strategy.calculate(claim);
                    assertTrue(payout >= 0, name + " gave negative payout " + payout);
                    assertTrue(payout <= 5_000_000, name + " exceeded limit with " + payout);
                }
            }
        }
    }
}
