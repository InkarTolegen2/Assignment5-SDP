package insurance.app;

import insurance.payout.*;
import insurance.risk.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final long DEDUCTIBLE = 100_000;
    private static final long COVERAGE_LIMIT = 5_000_000;

    public static void main(String[] args) {
        // Swap the data source here: any other ExchangeRateProvider works.
        ExchangeRateProvider rates = new SimulatedExchangeRateProvider();

        Map<String, PayoutStrategy> payouts = new LinkedHashMap<>();
        payouts.put("Standard", new StandardPayout());
        payouts.put("Tiered", new TieredPayout());
        payouts.put("Depreciated", new DepreciatedPayout());
        payouts.put("Foreign currency", new ForeignCurrencyPayout(rates));

        Map<String, RiskStrategy> risks = new LinkedHashMap<>();
        risks.put("Amount ratio", new AmountRatioRisk());
        risks.put("Claim history", new ClaimHistoryRisk());
        risks.put("Policy age", new PolicyAgeRisk());

        PayoutStrategySelector payoutSelector = new PayoutStrategySelector(rates);
        RiskStrategySelector riskSelector = new RiskStrategySelector();

        String payoutName = "Standard";
        String riskName = "Amount ratio";
        PayoutCalculator calculator = new PayoutCalculator(payouts.get(payoutName));
        RiskAssessor assessor = new RiskAssessor(risks.get(riskName));

        Scanner in = new Scanner(System.in);
        while (true) {
            printMenu();
            String choice = in.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> {
                        ClaimData claim = readClaim(in);
                        payouts.forEach((name, strategy) -> {
                            try {
                                System.out.println(name + ": " + strategy.calculate(claim));
                            } catch (RuntimeException e) {
                                System.out.println(name + ": not available (" + e.getMessage() + ")");
                            }
                        });
                    }
                    case "2" -> {
                        ClaimData claim = readClaim(in);
                        String name = pick(in, payouts.keySet());
                        System.out.println(name + ": " + new PayoutCalculator(payouts.get(name)).calculate(claim));
                    }
                    case "3" -> {
                        ClaimData claim = readClaim(in);
                        PayoutStrategy chosen = payoutSelector.select(claim);
                        System.out.println("Selector chose " + chosen.getClass().getSimpleName());
                        System.out.println("Payout: " + new PayoutCalculator(chosen).calculate(claim));
                    }
                    case "4" -> {
                        ClaimData claim = readClaim(in);
                        System.out.println(payoutName + " -> " + calculator.calculate(claim));
                        String newName = pick(in, payouts.keySet());
                        calculator.setStrategy(payouts.get(newName));
                        System.out.println("Strategy changed: " + payoutName + " -> " + newName);
                        payoutName = newName;
                        System.out.println(payoutName + " -> " + calculator.calculate(claim));
                    }
                    case "5" -> {
                        RiskInput input = readRisk(in);
                        risks.forEach((name, strategy) ->
                                System.out.println(name + ": " + strategy.score(input)));
                    }
                    case "6" -> {
                        RiskInput input = readRisk(in);
                        String name = pick(in, risks.keySet());
                        System.out.println(name + ": " + new RiskAssessor(risks.get(name)).assess(input));
                    }
                    case "7" -> {
                        RiskInput input = readRisk(in);
                        RiskStrategy chosen = riskSelector.select(input);
                        System.out.println("Selector chose " + chosen.getClass().getSimpleName());
                        System.out.println("Risk: " + new RiskAssessor(chosen).assess(input));
                    }
                    case "8" -> {
                        RiskInput input = readRisk(in);
                        System.out.println(riskName + " -> " + assessor.assess(input));
                        String newName = pick(in, risks.keySet());
                        assessor.setStrategy(risks.get(newName));
                        System.out.println("Strategy changed: " + riskName + " -> " + newName);
                        riskName = newName;
                        System.out.println(riskName + " -> " + assessor.assess(input));
                    }
                    case "0" -> {
                        return;
                    }
                    default -> System.out.println("Unknown option");
                }
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("PAYOUT strategies");
        System.out.println("1  Same claim through ALL strategies");
        System.out.println("2  Manual choice");
        System.out.println("3  Automatic choice (Selector)");
        System.out.println("4  Switch strategy at runtime");
        System.out.println("RISK strategies");
        System.out.println("5  Same claim through ALL strategies");
        System.out.println("6  Manual choice");
        System.out.println("7  Automatic choice (Selector)");
        System.out.println("8  Switch strategy at runtime");
        System.out.println("0  Exit");
        System.out.print("> ");
    }

    private static ClaimData readClaim(Scanner in) {
        long amount = Long.parseLong(ask(in, "Amount"));
        String currency = ask(in, "Currency (KZT, USD, EUR, RUB)").toUpperCase();
        int age = Integer.parseInt(ask(in, "Item age in years"));
        return new ClaimData(amount, currency, age, DEDUCTIBLE, COVERAGE_LIMIT);
    }

    private static RiskInput readRisk(Scanner in) {
        long amount = Long.parseLong(ask(in, "Claim amount"));
        int claims = Integer.parseInt(ask(in, "Claims in last 12 months"));
        int days = Integer.parseInt(ask(in, "Days since policy start"));
        return new RiskInput(amount, COVERAGE_LIMIT, claims, days);
    }

    private static String pick(Scanner in, Collection<String> names) {
        List<String> list = new ArrayList<>(names);
        for (int i = 0; i < list.size(); i++) {
            System.out.println((i + 1) + "  " + list.get(i));
        }
        System.out.print("Number: ");
        return list.get(Integer.parseInt(in.nextLine().trim()) - 1);
    }

    private static String ask(Scanner in, String label) {
        System.out.print(label + ": ");
        return in.nextLine().trim();
    }
}
