import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// Module 1: RateManager - Manages exchange rates (in a real app, this would fetch from an API)
class RateManager {
    private Map<String, Double> rates; // Rates relative to base currency USD
    private final DecimalFormat df = new DecimalFormat("#,##0.00"); // For pretty-printing rates

    public RateManager() {
        rates = new HashMap<>();
        // Sample exchange rates (update with real-time data in production)
        // Format: 1 USD = X [Currency]
        rates.put("EUR", 0.92);
        rates.put("GBP", 0.81);
        rates.put("JPY", 150.50);
        rates.put("INR", 83.50);
        rates.put("USD", 1.00); // Base currency
    }

    public double getRate(String currency) {
        return rates.getOrDefault(currency.toUpperCase(), 1.0);
    }

    public boolean hasRate(String currency) {
        return rates.containsKey(currency.toUpperCase());
    }

    public void displaySupportedCurrencies() {
        System.out.println("\nSupported currencies:");
        System.out.println("• USD (US Dollar) - Base currency");
        System.out.println("• EUR (Euro)");
        System.out.println("• GBP (British Pound)");
        System.out.println("• JPY (Japanese Yen)");
        System.out.println("• INR (Indian Rupee)");
        System.out.println("Tip: Enter the 3-letter code (case-insensitive).");
    }
}

// Module 2: Converter - Handles the conversion logic with clear calculations
class Converter {
    private RateManager rateManager;

    public Converter(RateManager rateManager) {
        this.rateManager = rateManager;
    }

    public double convert(double amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            return amount; // No conversion needed
        }
        
        double fromRate = rateManager.getRate(fromCurrency.toUpperCase());
        double toRate = rateManager.getRate(toCurrency.toUpperCase());
        
        // Formula: (amount / fromRate) * toRate
        // This first converts to base (USD), then to target
        double inBase = amount / fromRate;
        double result = inBase * toRate;
        
        return result;
    }

    public String getConversionExplanation(double amount, String fromCurrency, String toCurrency, double result) {
        double fromRate = rateManager.getRate(fromCurrency.toUpperCase());
        double toRate = rateManager.getRate(toCurrency.toUpperCase());
        double inBase = amount / fromRate;
        
        return String.format(
            "How it works: %.2f %s ÷ %.2f (USD rate) = %.2f USD × %.2f (%s rate) = %.2f %s",
            amount, fromCurrency, fromRate, inBase, toRate, toCurrency, result, toCurrency
        );
    }
}

// Module 3: CurrencyConverterApp - User-friendly console interface
public class CurrencyConverterApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DecimalFormat df = new DecimalFormat("#,##0.00");

    public static void main(String[] args) {
        RateManager rateManager = new RateManager();
        Converter converter = new Converter(rateManager);

        printWelcomeMessage();
        rateManager.displaySupportedCurrencies();

        while (true) {
            try {
                System.out.print("\nWhat amount would you like to convert? (or type 'quit' to exit): ");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("quit")) {
                    printGoodbyeMessage();
                    break;
                }

                double amount;
                try {
                    amount = Double.parseDouble(input);
                    if (amount <= 0) {
                        System.out.println("Please enter a positive amount. Let's try again!");
                        continue;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Oops! That doesn't look like a valid number. Please try again.");
                    continue;
                }

                System.out.print("From which currency? (e.g., USD): ");
                String fromCurrency = scanner.nextLine().trim().toUpperCase();

                if (!rateManager.hasRate(fromCurrency)) {
                    System.out.println("Sorry, we don't support '" + fromCurrency + "' yet. Check the list above and try again!");
                    continue;
                }

                System.out.print("To which currency? (e.g., EUR): ");
                String toCurrency = scanner.nextLine().trim().toUpperCase();

                if (!rateManager.hasRate(toCurrency)) {
                    System.out.println("Sorry, we don't support '" + toCurrency + "' yet. Check the list above and try again!");
                    continue;
                }

                if (fromCurrency.equals(toCurrency)) {
                    System.out.println("No need to convert—it's the same currency! 😊");
                    continue;
                }

                double result = converter.convert(amount, fromCurrency, toCurrency);
                printConversionResult(amount, fromCurrency, toCurrency, result, converter.getConversionExplanation(amount, fromCurrency, toCurrency, result));

            } catch (Exception e) {
                System.out.println("Something went wrong. Please try again or type 'quit' to exit.");
            }
        }

        scanner.close();
    }

    private static void printWelcomeMessage() {
        System.out.println("🌍 Welcome to the Friendly Currency Converter! 🌍");
        System.out.println("I'll help you convert between currencies easily. Rates are approximate and based on USD.");
        System.out.println("Note: These are sample rates—real apps fetch live data!");
    }

    private static void printConversionResult(double amount, String fromCurrency, String toCurrency, double result, String explanation) {
        System.out.println("\n✨ Conversion Complete! ✨");
        System.out.printf("  %s %.2f %s → %.2f %s%n", df.format(amount), amount, fromCurrency, result, toCurrency);
        System.out.println("\n" + explanation);
        System.out.println("\nReady for another conversion? Just enter the next amount!");
    }

    private static void printGoodbyeMessage() {
        System.out.println("\nThanks for stopping by! Safe travels (and smart exchanges)! ✈️💰");
    }
}