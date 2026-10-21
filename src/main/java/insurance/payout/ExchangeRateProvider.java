package insurance.payout;

public interface ExchangeRateProvider {
    double rateToKzt(String currency);
}
