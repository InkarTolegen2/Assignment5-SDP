# Strategy Pattern: Insurance Claims (SDP Assignment 5)

Student: Ingkar, Astana IT University. Domain: Insurance Claims (same variant as Assignment 4).

Two families of interchangeable algorithms, one context per family, one selector per family.

| Family | Interface | Strategies | Context | Selector |
|---|---|---|---|---|
| Payout calculation | `PayoutStrategy` | `StandardPayout`, `TieredPayout`, `DepreciatedPayout`, `ForeignCurrencyPayout` | `PayoutCalculator` | `PayoutStrategySelector` |
| Fraud risk scoring | `RiskStrategy` | `AmountRatioRisk`, `ClaimHistoryRisk`, `PolicyAgeRisk` | `RiskAssessor` | `RiskStrategySelector` |

## Build and run

Requirements: Java 17 or newer, Maven.

```
mvn test
mvn compile exec:java "-Dexec.mainClass=insurance.app.Main"
```

The console menu shows: the same input through all strategies of a family (options 1 and 5), manual choice (2 and 6), automatic choice by the selector (3 and 7), and switching the strategy at runtime (4 and 8).

## Classes and responsibilities

| Class | Responsibility |
|---|---|
| `ClaimData` | Holds amount, currency, item age, deductible, coverage limit of one claim. |
| `ExchangeRateProvider` | Gives the exchange rate to tenge for a currency code, hiding the source of rates. |
| `SimulatedExchangeRateProvider` | Returns fixed sample exchange rates for the running demo program. |
| `FixedRateProvider (test)` | Returns exchange rates chosen by the test, so results never change. |
| `PayoutStrategy` | Declares one method that turns a claim into a payout amount. |
| `PayoutLimits` | Clamps a payout into the range from zero to the coverage limit. |
| `StandardPayout` | Pays the claimed amount minus the deductible. |
| `TieredPayout` | Pays the first million in full, then sixty percent of the remainder. |
| `DepreciatedPayout` | Reduces the claimed value by ten percent for each year of item age. |
| `ForeignCurrencyPayout` | Converts the claimed amount into tenge using the provider rate, then subtracts the deductible. |
| `PayoutCalculator` | Context: holds the current payout strategy, replaces it on request, delegates the calculation to it. |
| `PayoutStrategySelector` | Picks the payout strategy matching currency, item age, claim size. |
| `RiskInput` | Holds amount, limit, recent claim count, days since policy start for one claim. |
| `RiskStrategy` | Declares one method that turns a claim into a fraud risk from zero to one hundred. |
| `AmountRatioRisk` | Scores risk as the claimed share of the coverage limit. |
| `ClaimHistoryRisk` | Scores risk by the number of claims filed during the last twelve months. |
| `PolicyAgeRisk` | Scores risk higher for policies that started only a few days ago. |
| `RiskAssessor` | Context: holds the current risk strategy, replaces it on request, delegates scoring to it. |
| `RiskStrategySelector` | Picks the risk strategy matching policy age, claim frequency. |
| `Main` | Console menu showing all strategies, manual choice, automatic choice, runtime switching. |

## Selector rules

`PayoutStrategySelector` (first matching rule wins):

1. Currency is not KZT: `ForeignCurrencyPayout`.
2. Item age is above 0 years: `DepreciatedPayout`.
3. Amount is above 1 000 000: `TieredPayout`.
4. Otherwise: `StandardPayout`.

`RiskStrategySelector` (first matching rule wins):

1. Policy started less than 30 days ago: `PolicyAgeRisk`.
2. Two or more claims in the last 12 months: `ClaimHistoryRisk`.
3. Otherwise: `AmountRatioRisk`.

`if` is used only in the selectors. `ContextPurityTest` reads the source of both contexts and fails if they contain `if`, `switch` or `instanceof`.

## Isolation from change (Clean Code, chapter 10)

`ForeignCurrencyPayout` needs exchange rates. It depends only on our own interface `ExchangeRateProvider`, never on a concrete source. The demo uses `SimulatedExchangeRateProvider`. Tests use `FixedRateProvider` (fixed values) or a lambda. To switch to another source (a file, a real API), write one new class that implements the interface and change one line in `Main`. No strategy changes.

## Why Strategy, not State or Template Method

**Not State.** In State, an object changes behaviour because its own internal state changes, and the states usually trigger the transitions themselves. A claim here has no such life cycle. Our payout and risk algorithms never switch each other. They are independent options for the same input, and somebody from outside (the user or the selector) chooses one. That is the intent of Strategy: interchangeable algorithms, chosen by the client.

**Not Template Method.** Template Method fixes the skeleton of an algorithm in a base class, and subclasses override some steps. The choice is made when the object is created, by picking the subclass, so it cannot change later for the same object. Our strategies share no skeleton: each one implements the whole calculation in its own way. The context holds the algorithm by composition (a field, not inheritance), and `setStrategy()` replaces it while the program runs. `PayoutCalculatorTest` and `RiskAssessorTest` check exactly this.

**Open/Closed.** A new payout rule is a new class implementing `PayoutStrategy` plus one rule in the selector. The contexts and the existing strategies stay unchanged.

## Refactoring with Git history

The payout family was first written the "bad" way: one class `PayoutCalculator` with a `switch` over a `PayoutType` enum. It was then refactored into Strategy. The same nine behaviour tests (`PayoutBehaviourTest`) pass on both commits. Only the one-line test helper `Payouts` changes, because it builds the calculator.

- Before (switch): https://github.com/InkarTolegen2/Assignment5-SDP/tree/before-strategy
- After (Strategy): https://github.com/InkarTolegen2/Assignment5-SDP/tree/after-strategy

## Tests (JUnit 5)

- A test for every strategy: `PayoutStrategiesTest`, `RiskStrategiesTest`.
- A test for every selector rule, including boundaries and rule priority: `PayoutStrategySelectorTest`, `RiskStrategySelectorTest`.
- Context tests, including runtime replacement: `PayoutCalculatorTest`, `RiskAssessorTest`.
- Parameterized tests that run one check on all strategies of a family: `AllPayoutStrategiesTest` (payout is never negative and never above the limit), `AllRiskStrategiesTest` (score is always between 0 and 100).
- `ContextPurityTest`: contexts contain no `if`, `switch` or `instanceof`.
