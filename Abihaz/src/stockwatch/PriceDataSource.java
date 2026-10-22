package stockwatch;

import java.util.List;

/**
 * Source of daily price records. Implementations may read a CSV file, a database, etc.
 */
public interface PriceDataSource {

    List<PriceRecord> load() throws DataLoadException;
}
