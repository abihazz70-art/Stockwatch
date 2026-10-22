package stockwatch;

import java.time.LocalDate;

/**
 * One day's close together with a simple moving average, if enough days exist.
 */
public class MovingAveragePoint {

    private final LocalDate date;
    private final double close;
    private final Double movingAverage;

    public MovingAveragePoint(LocalDate date, double close, Double movingAverage) {
        this.date = date;
        this.close = close;
        this.movingAverage = movingAverage;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getClose() {
        return close;
    }

    public boolean hasMovingAverage() {
        return movingAverage != null;
    }

    public double getMovingAverage() {
        if (movingAverage == null) {
            throw new IllegalStateException("Not enough days for a moving average");
        }
        return movingAverage;
    }
}
