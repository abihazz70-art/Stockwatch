package stockwatch;

/**
 * Thrown when the user asks for a stock symbol that is not in the loaded data.
 */
public class UnknownSymbolException extends Exception {

    public UnknownSymbolException(String symbol) {
        super("Unknown stock symbol: " + symbol);
    }
}
