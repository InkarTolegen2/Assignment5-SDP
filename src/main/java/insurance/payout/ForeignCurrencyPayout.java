package insurance.payout;

public class ForeignCurrencyPayout implements PayoutStrategy {
    private final ExchangeRateProvider rates;

    public ForeignCurrencyPayout(ExchangeRateProvider rates) { this.rates = rates; }

    @Override
    public long calculate(ClaimData claim) {
        long converted = Math.round(claim.amount() * rates.rateToKzt(claim.currency()));
        return PayoutLimits.apply(converted - claim.deductible(), claim);
    }
}