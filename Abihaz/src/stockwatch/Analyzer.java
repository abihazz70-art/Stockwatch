package stockwatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Groups price records by symbol and answers analysis questions.
 */
public class Analyzer {

    private final Map<String, Stock> stocksBySymbol;

    public Analyzer(List<PriceRecord> records) {
        Map<String, List<PriceRecord>> grouped = new LinkedHashMap<>();
        for (PriceRecord record : records) {
            grouped.computeIfAbsent(record.getSymbol(), key -> new ArrayList<>()).add(record);
        }

        Map<String, Stock> map = new LinkedHashMap<>();
        for (Map.Entry<String, List<PriceRecord>> entry : grouped.entrySet()) {
            map.put(entry.getKey(), new Stock(entry.getKey(), entry.getValue()));
        }
        this.stocksBySymbol = Collections.unmodifiableMap(map);
    }

    public List<Stock> listStocks() {
        return new ArrayList<>(stocksBySymbol.values());
    }

    public List<String> listSymbols() {
        return new ArrayList<>(stocksBySymbol.keySet());
    }

    public Stock getStock(String symbol) throws UnknownSymbolException {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new UnknownSymbolException("(empty)");
        }
        Stock stock = stocksBySymbol.get(symbol.trim().toUpperCase(Locale.ROOT));
        if (stock == null) {
            throw new UnknownSymbolException(symbol.trim().toUpperCase(Locale.ROOT));
        }
        return stock;
    }

    public List<Stock> percentChangeRanking() {
        List<Stock> ranked = listStocks();
        ranked.sort(Comparator.comparingDouble(Stock::percentChange).reversed());
        return ranked;
    }

    public List<Stock> topGainers(int count) {
        return percentChangeRanking().stream()
                .filter(stock -> stock.percentChange() > 0)
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Stock> topLosers(int count) {
        List<Stock> ranked = listStocks();
        ranked.sort(Comparator.comparingDouble(Stock::percentChange));
        return ranked.stream()
                .filter(stock -> stock.percentChange() < 0)
                .limit(count)
                .collect(Collectors.toList());
    }
}
