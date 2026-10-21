package insurance.payout;

import java.util.Map;

public class FixedRateProvider implements ExchangeRateProvider {
    private final Map<String, Double> rates;
    
    public FixedRateProvider(Map<String, Double> rates) {
        this.rates = rates;
    }

    @Override 
    public double rateToKzt(String currency) {
        Double rate = rates.get(currency);
        if (rate == null) throw new IllegalArgumentException("No rate for" + currency);
        return rate;
    }
}
