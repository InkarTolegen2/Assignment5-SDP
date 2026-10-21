package insurance.risk;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AllRiskStrategiesTest {
    static Stream<Arguments> strategies() {
        return Stream.of(
                Arguments.of("amount ratio", new AmountRatioRisk()),
                Arguments.of("claim history", new ClaimHistoryRisk()),
                Arguments.of("policy age", new PolicyAgeRisk()));
    }

    @ParameterizedTest(name = "{0}: score is always between 0 and 100")
    @MethodSource("strategies")
    void scoreStaysInRange(String name, RiskStrategy strategy) {
        long[] amounts = {0, 1_000, 2_500_000, 5_000_000, 90_000_000};
        long[] limits = {0, 5_000_000};
        int[] claims = {0, 1, 2, 10};
        int[] days = {0, 29, 30, 365, 5_000};
        for (long amount : amounts) {
            for (long limit : limits) {
                for (int c : claims) {
                    for (int d : days) {
                        int score = strategy.score(new RiskInput(amount, limit, c, d));
                        assertTrue(score >= 0 && score <= 100, name + " gave " + score);
                    }
                }
            }
        }
    }
}
