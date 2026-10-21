package insurance.payout;

public class PayoutCalculator {
    private PayoutType type;
    private final ExchangeRateProvider rates;

    public PayoutCalculator(PayoutType type, ExchangeRateProvider rates) {
        this.type = type;
        this.rates = rates;
    }

    public void setType(PayoutType type) { this.type = type; }

    public long calculate(ClaimData c) {
        long payout = switch (type) {
            case STANDARD -> c.amount() - c.deductible();
            case TIERED -> {
                long eligible = Math.max(0, c.amount() - c.deductible());
                long fullPart = Math.min(eligible, 1_000_000);
                long rest = eligible - fullPart;
                yield fullPart + rest * 60 / 100;
            }
            case DEPRECIATED -> {
                int depreciation = Math.min(70, c.itemAgeYears() * 10);
                yield c.amount() * (100 - depreciation) / 100 - c.deductible();
            }
            case FOREIGN_CURRENCY -> {
                long converted = Math.round(c.amount() * rates.rateToKzt(c.currency()));
                yield converted - c.deductible();
            }
        };
        return Math.min(c.coverageLimit(), Math.max(0, payout));
    }
}