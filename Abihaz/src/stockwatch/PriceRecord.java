package stockwatch;

import java.time.LocalDate;
import java.util.Objects;

/**
 * One trading day's OHLC data for a single stock.
 */
public class PriceRecord {

    private final LocalDate date;
    private final String symbol;
    private final double open;
    private final double high;
    private final double low;
    private final double close;
    private final long volume;

    public PriceRecord(LocalDate date, String symbol, double open, double high, double low,
                       double close, long volume) {
        this.date = Objects.requireNonNull(date, "date");
        this.symbol = Objects.requireNonNull(symbol, "symbol").toUpperCase();
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getOpen() {
        return open;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getClose() {
        return close;
    }

    public long getVolume() {
        return volume;
    }

    @Override
    public String toString() {
        return String.format("%s  %-10s  O:%8.2f  H:%8.2f  L:%8.2f  C:%8.2f  V:%,d",
                date, symbol, open, high, low, close, volume);
    }
}
