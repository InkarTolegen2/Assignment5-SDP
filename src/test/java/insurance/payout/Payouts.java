package insurance.payout;

import java.util.Map;

final class Payouts {
    private Payouts() {}

    static long calculate(String strategyName, ClaimData claim) {
        ExchangeRateProvider rates = new FixedRateProvider(
                Map.of("KZT", 1.0, "USD", 500.0, "EUR", 550.0));
        PayoutStrategy strategy = switch (strategyName) {
            case "STANDARD" -> new StandardPayout();
            case "TIERED" -> new TieredPayout();
            case "DEPRECIATED" -> new DepreciatedPayout();
            case "FOREIGN_CURRENCY" -> new ForeignCurrencyPayout(rates);
            default -> throw new IllegalArgumentException(strategyName);
        };
        return new PayoutCalculator(strategy).calculate(claim);
    }
}