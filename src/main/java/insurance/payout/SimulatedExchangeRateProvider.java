package insurance.payout;

import java.util.Map;

public class SimulatedExchangeRateProvider implements ExchangeRateProvider {
    private static final Map<String, Double> RATES =
            Map.of("KZT", 1.0, "USD", 505.5, "EUR", 560.2, "RUB", 5.6);

    @Override
    public double rateToKzt(String currency) {
        Double rate = RATES.get(currency);
        if (rate == null) throw new IllegalArgumentException("Unknown currency: " + currency);
        return rate;
    }
}
