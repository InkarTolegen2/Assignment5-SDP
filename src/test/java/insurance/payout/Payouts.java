package insurance.payout;

import java.util.Map;

final class Payouts {
    private Payouts() {}

    static long calculate(String strategyName, ClaimData claim){
        ExchangeRateProvider rates = new FixedRateProvider(
            Map.of("KZT", 1.0, "USD", 500.0, "EUR", 550.0));
            return new PayoutCalculator(PayoutType.valueOf(strategyName), rates).calculate(claim);
    }

}
