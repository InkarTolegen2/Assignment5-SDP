package insurance.payout;

public class PayoutStrategySelector {
    private static final long LARGE_CLAIM = 1_000_000;

    private final ExchangeRateProvider rates;

    public PayoutStrategySelector(ExchangeRateProvider rates) { this.rates = rates; }

    public PayoutStrategy select(ClaimData claim) {
        if (!"KZT".equals(claim.currency())) {
            return new ForeignCurrencyPayout(rates);
        }
        if (claim.itemAgeYears() > 0) {
            return new DepreciatedPayout();
        }
        if (claim.amount() > LARGE_CLAIM) {
            return new TieredPayout();
        }
        return new StandardPayout();
    }
}
