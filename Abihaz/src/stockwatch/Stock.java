package stockwatch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * A listed stock and its chronological price history.
 */
public class Stock {

    private final String symbol;
    private final List<PriceRecord> history;

    public Stock(String symbol, List<PriceRecord> history) {
        this.symbol = Objects.requireNonNull(symbol, "symbol").toUpperCase();
        List<PriceRecord> copy = new ArrayList<>(Objects.requireNonNull(history, "history"));
        copy.sort(Comparator.comparing(PriceRecord::getDate));
        this.history = Collections.unmodifiableList(copy);
    }

    public String getSymbol() {
        return symbol;
    }

    public List<PriceRecord> getHistory() {
        return history;
    }

    public PriceRecord firstRecord() {
        return history.isEmpty() ? null : history.get(0);
    }

    public PriceRecord lastRecord() {
        return history.isEmpty() ? null : history.get(history.size() - 1);
    }

    public LocalDate firstDate() {
        PriceRecord first = firstRecord();
        return first == null ? null : first.getDate();
    }

    public LocalDate lastDate() {
        PriceRecord last = lastRecord();
        return last == null ? null : last.getDate();
    }

    public double firstClose() {
        PriceRecord first = firstRecord();
        return first == null ? Double.NaN : first.getClose();
    }

    public double lastClose() {
        PriceRecord last = lastRecord();
        return last == null ? Double.NaN : last.getClose();
    }

    /**
     * Percent change from the first close to the last close.
     */
    public double percentChange() {
        double start = firstClose();
        if (history.isEmpty() || start == 0.0 || Double.isNaN(start)) {
            return Double.NaN;
        }
        return ((lastClose() - start) / start) * 100.0;
    }

    public double highestClose() {
        return history.stream()
                .mapToDouble(PriceRecord::getClose)
                .max()
                .orElse(Double.NaN);
    }

    public double lowestClose() {
        return history.stream()
                .mapToDouble(PriceRecord::getClose)
                .min()
                .orElse(Double.NaN);
    }

    /**
     * Simple moving average of closing prices. Days before a full window have no average.
     */
    public List<MovingAveragePoint> simpleMovingAverage(int window) {
        if (window < 1) {
            throw new IllegalArgumentException("Moving-average window must be at least 1");
        }
        List<MovingAveragePoint> points = new ArrayList<>();
        for (int i = 0; i < history.size(); i++) {
            PriceRecord record = history.get(i);
            Double average = null;
            if (i >= window - 1) {
                double sum = 0.0;
                for (int j = i - window + 1; j <= i; j++) {
                    sum += history.get(j).getClose();
                }
                average = sum / window;
            }
            points.add(new MovingAveragePoint(record.getDate(), record.getClose(), average));
        }
        return points;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
