package stockwatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * User-selected symbols to follow. Symbols are stored in uppercase and without duplicates.
 */
public class Watchlist {

    private final Set<String> symbols = new LinkedHashSet<>();

    public boolean add(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Symbol cannot be empty");
        }
        return symbols.add(symbol.trim().toUpperCase(Locale.ROOT));
    }

    public boolean remove(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Symbol cannot be empty");
        }
        return symbols.remove(symbol.trim().toUpperCase(Locale.ROOT));
    }

    public boolean contains(String symbol) {
        return symbol != null && symbols.contains(symbol.trim().toUpperCase(Locale.ROOT));
    }

    public boolean isEmpty() {
        return symbols.isEmpty();
    }

    public List<String> getSymbols() {
        return Collections.unmodifiableList(new ArrayList<>(symbols));
    }
}
