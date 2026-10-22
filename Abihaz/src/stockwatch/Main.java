package stockwatch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Console menu for listing stocks, analysing price history, and managing a watchlist.
 */
public class Main {

    private static final int TOP_COUNT = 3;
    private static final int MOVING_AVERAGE_DAYS = 5;
    private static final String DEFAULT_REPORT_FILE = "gainers_losers.csv";

    private final Analyzer analyzer;
    private final Watchlist watchlist;
    private final Scanner scanner;

    public Main(Analyzer analyzer, Watchlist watchlist, Scanner scanner) {
        this.analyzer = analyzer;
        this.watchlist = watchlist;
        this.scanner = scanner;
    }

    public static void main(String[] args) {
        Path csvPath = args.length > 0 ? Paths.get(args[0]) : findPricesCsv();
        PriceDataSource source = new CsvPriceLoader(csvPath);

        try {
            List<PriceRecord> records = source.load();
            Analyzer analyzer = new Analyzer(records);
            System.out.println("Loaded " + records.size() + " rows from " + csvPath.toAbsolutePath());
            System.out.println();
            System.out.flush();
            Main app = new Main(analyzer, new Watchlist(), new Scanner(System.in));
            app.run();
        } catch (DataLoadException e) {
            System.out.println("Failed to load prices: " + e.getMessage());
            System.out.println("Run the program from the Abihaz folder, or pass the CSV path:");
            System.out.println("  java -cp out stockwatch.Main C:\\Users\\ina\\Desktop\\Abihaz\\prices.csv");
            System.out.flush();
            waitToClose();
            System.exit(1);
        }
    }

    private static Path findPricesCsv() {
        List<Path> candidates = new ArrayList<>();
        candidates.add(Paths.get("prices.csv"));
        candidates.add(Paths.get("..", "prices.csv"));
        String userDir = System.getProperty("user.dir");
        if (userDir != null) {
            Path cwd = Paths.get(userDir);
            candidates.add(cwd.resolve("prices.csv"));
            if (cwd.getParent() != null) {
                candidates.add(cwd.getParent().resolve("prices.csv"));
            }
        }
        try {
            Path codeSource = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            candidates.add(codeSource.resolve("prices.csv"));
            if (codeSource.getParent() != null) {
                candidates.add(codeSource.getParent().resolve("prices.csv"));
            }
        } catch (Exception ignored) {
            // Fall through to the list above.
        }
        candidates.add(Paths.get("C:\\Users\\ina\\Desktop\\Abihaz\\prices.csv"));

        for (Path candidate : candidates) {
            if (candidate != null && Files.exists(candidate)) {
                return candidate;
            }
        }
        return Paths.get("prices.csv");
    }

    private static void waitToClose() {
        System.out.println();
        System.out.print("Press Enter to close...");
        System.out.flush();
        try {
            new Scanner(System.in).nextLine();
        } catch (Exception ignored) {
            // No console attached.
        }
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = readLine("Choose an option: ");
            System.out.println();
            try {
                switch (choice) {
                    case "1":
                        listAllStocks();
                        break;
                    case "2":
                        showPriceHistory();
                        break;
                    case "3":
                        showPercentChanges();
                        break;
                    case "4":
                        showTopGainersAndLosers();
                        break;
                    case "5":
                        showHighLowCloses();
                        break;
                    case "6":
                        manageWatchlist();
                        break;
                    case "7":
                        exportGainersLosers();
                        break;
                    case "8":
                        showMovingAverage();
                        break;
                    case "0":
                        running = false;
                        System.out.println("Goodbye.");
                        waitToClose();
                        break;
                    default:
                        System.out.println("Invalid option. Enter a number from the menu.");
                }
            } catch (UnknownSymbolException e) {
                System.out.println(e.getMessage());
                System.out.println("Available symbols: " + String.join(", ", analyzer.listSymbols()));
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            }
            if (running) {
                System.out.println();
            }
        }
    }

    private void printMenu() {
        System.out.println("========================================");
        System.out.println("   Stock Watchlist & Analyzer");
        System.out.println("========================================");
        System.out.println("1. List all stocks");
        System.out.println("2. Show price history of one stock");
        System.out.println("3. Show % change (first day to last day)");
        System.out.println("4. Show top 3 gainers and top 3 losers");
        System.out.println("5. Show highest and lowest closing prices");
        System.out.println("6. Watchlist (add / remove / view)");
        System.out.println("7. Export top 3 gainers and losers to CSV");
        System.out.println("8. Show 5-day moving average");
        System.out.println("0. Exit");
        System.out.println("========================================");
    }

    private void listAllStocks() {
        List<Stock> stocks = analyzer.listStocks();
        System.out.println(String.format("%-12s %-12s %-12s %10s %10s",
                "SYMBOL", "FIRST DATE", "LAST DATE", "FIRST C", "LAST C"));
        System.out.println("----------------------------------------------------------------");
        for (Stock stock : stocks) {
            System.out.println(String.format(" %-11s %-12s %-12s %10.2f %10.2f",
                    stock.getSymbol(),
                    stock.firstDate(),
                    stock.lastDate(),
                    stock.firstClose(),
                    stock.lastClose()));
        }
        System.out.println("Total stocks: " + stocks.size());
    }

    private void showPriceHistory() throws UnknownSymbolException {
        String symbol = readLine("Enter stock symbol: ");
        Stock stock = analyzer.getStock(symbol);
        System.out.println("Price history for " + stock.getSymbol() + ":");
        System.out.println(String.format("%-12s %10s %10s %10s %10s %12s",
                "DATE", "OPEN", "HIGH", "LOW", "CLOSE", "VOLUME"));
        System.out.println("--------------------------------------------------------------------");
        for (PriceRecord record : stock.getHistory()) {
            System.out.println(String.format(" %-11s %10.2f %10.2f %10.2f %10.2f %,12d",
                    record.getDate(),
                    record.getOpen(),
                    record.getHigh(),
                    record.getLow(),
                    record.getClose(),
                    record.getVolume()));
        }
    }

    private void showPercentChanges() {
        List<Stock> ranked = analyzer.percentChangeRanking();
        System.out.println(String.format("%-12s %12s %12s %12s",
                "SYMBOL", "FIRST CLOSE", "LAST CLOSE", "% CHANGE"));
        System.out.println("------------------------------------------------------");
        for (Stock stock : ranked) {
            System.out.println(String.format(" %-11s %12.2f %12.2f %11.2f%%",
                    stock.getSymbol(),
                    stock.firstClose(),
                    stock.lastClose(),
                    stock.percentChange()));
        }
    }

    private void showTopGainersAndLosers() {
        List<Stock> gainers = analyzer.topGainers(TOP_COUNT);
        List<Stock> losers = analyzer.topLosers(TOP_COUNT);

        System.out.println("Top " + TOP_COUNT + " gainers:");
        printRankedMoves(gainers);
        System.out.println();
        System.out.println("Top " + TOP_COUNT + " losers:");
        printRankedMoves(losers);
    }

    private void printRankedMoves(List<Stock> stocks) {
        if (stocks.isEmpty()) {
            System.out.println("  (none)");
            return;
        }
        int rank = 1;
        for (Stock stock : stocks) {
            System.out.println(String.format("  %d. %-10s  %7.2f%%",
                    rank++, stock.getSymbol(), stock.percentChange()));
        }
    }

    private void showHighLowCloses() {
        System.out.println(String.format("%-12s %14s %14s", "SYMBOL", "HIGHEST CLOSE", "LOWEST CLOSE"));
        System.out.println("----------------------------------------------");
        for (Stock stock : analyzer.listStocks()) {
            System.out.println(String.format(" %-11s %14.2f %14.2f",
                    stock.getSymbol(),
                    stock.highestClose(),
                    stock.lowestClose()));
        }
    }

    private void exportGainersLosers() {
        String fileName = readLine("Output file [" + DEFAULT_REPORT_FILE + "]: ");
        if (fileName.isEmpty()) {
            fileName = DEFAULT_REPORT_FILE;
        }
        Path outputPath = Paths.get(fileName);
        try {
            new ReportExporter().exportGainersLosers(
                    outputPath,
                    analyzer.topGainers(TOP_COUNT),
                    analyzer.topLosers(TOP_COUNT));
            System.out.println("Report written to " + outputPath.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("Could not write report: " + e.getMessage());
        }
    }

    private void showMovingAverage() throws UnknownSymbolException {
        String symbol = readLine("Enter stock symbol (or ALL): ");
        if (symbol.equalsIgnoreCase("ALL")) {
            System.out.println(String.format("%-12s %12s %14s",
                    "SYMBOL", "LAST CLOSE", MOVING_AVERAGE_DAYS + "-DAY MA"));
            System.out.println("------------------------------------------");
            for (Stock stock : analyzer.listStocks()) {
                printLatestMovingAverage(stock);
            }
            return;
        }

        Stock stock = analyzer.getStock(symbol);
        List<MovingAveragePoint> points = stock.simpleMovingAverage(MOVING_AVERAGE_DAYS);
        System.out.println(MOVING_AVERAGE_DAYS + "-day moving average for " + stock.getSymbol() + ":");
        System.out.println(String.format("%-12s %10s %12s", "DATE", "CLOSE", "SMA-" + MOVING_AVERAGE_DAYS));
        System.out.println("--------------------------------------");
        for (MovingAveragePoint point : points) {
            String smaText = point.hasMovingAverage()
                    ? String.format("%12.2f", point.getMovingAverage())
                    : String.format("%12s", "n/a");
            System.out.println(String.format(" %-11s %10.2f %s",
                    point.getDate(), point.getClose(), smaText));
        }
    }

    private void printLatestMovingAverage(Stock stock) {
        List<MovingAveragePoint> points = stock.simpleMovingAverage(MOVING_AVERAGE_DAYS);
        if (points.isEmpty()) {
            System.out.println(String.format(" %-11s %12s %14s", stock.getSymbol(), "-", "-"));
            return;
        }
        MovingAveragePoint last = points.get(points.size() - 1);
        String smaText = last.hasMovingAverage()
                ? String.format("%14.2f", last.getMovingAverage())
                : "           n/a";
        System.out.println(String.format(" %-11s %12.2f %s",
                stock.getSymbol(), last.getClose(), smaText));
    }

    private void manageWatchlist() {
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("--- Watchlist ---");
            System.out.println("a. Add a stock");
            System.out.println("r. Remove a stock");
            System.out.println("v. View watchlist");
            System.out.println("b. Back to main menu");
            String choice = readLine("Choose: ").toLowerCase(Locale.ROOT);
            System.out.println();
            try {
                switch (choice) {
                    case "a":
                        addToWatchlist();
                        break;
                    case "r":
                        removeFromWatchlist();
                        break;
                    case "v":
                        viewWatchlist();
                        break;
                    case "b":
                        inMenu = false;
                        break;
                    default:
                        System.out.println("Invalid option. Use a, r, v, or b.");
                }
            } catch (UnknownSymbolException e) {
                System.out.println(e.getMessage());
                System.out.println("Available symbols: " + String.join(", ", analyzer.listSymbols()));
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            }
            if (inMenu) {
                System.out.println();
            }
        }
    }

    private void addToWatchlist() throws UnknownSymbolException {
        String symbol = readLine("Symbol to add: ");
        Stock stock = analyzer.getStock(symbol);
        if (watchlist.add(stock.getSymbol())) {
            System.out.println(stock.getSymbol() + " added to the watchlist.");
        } else {
            System.out.println(stock.getSymbol() + " is already on the watchlist.");
        }
    }

    private void removeFromWatchlist() {
        String symbol = readLine("Symbol to remove: ");
        if (watchlist.remove(symbol)) {
            System.out.println(symbol.trim().toUpperCase(Locale.ROOT) + " removed from the watchlist.");
        } else {
            System.out.println("That symbol is not on the watchlist.");
        }
    }

    private void viewWatchlist() {
        List<String> symbols = watchlist.getSymbols();
        if (symbols.isEmpty()) {
            System.out.println("Watchlist is empty.");
            return;
        }
        System.out.println(String.format("%-12s %12s %12s", "SYMBOL", "LAST CLOSE", "% CHANGE"));
        System.out.println("------------------------------------------");
        for (String symbol : symbols) {
            try {
                Stock stock = analyzer.getStock(symbol);
                System.out.println(String.format(" %-11s %12.2f %11.2f%%",
                        stock.getSymbol(), stock.lastClose(), stock.percentChange()));
            } catch (UnknownSymbolException e) {
                System.out.println(" " + symbol + " (no longer in price file)");
            }
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        System.out.flush();
        if (!scanner.hasNextLine()) {
            return "";
        }
        return scanner.nextLine().trim();
    }
}
