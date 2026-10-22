package stockwatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads daily prices from a CSV file with columns:
 * date, symbol, open, high, low, close, volume
 */
public class CsvPriceLoader implements PriceDataSource {

    private static final int EXPECTED_COLUMNS = 7;

    private final Path filePath;

    public CsvPriceLoader(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<PriceRecord> load() throws DataLoadException {
        if (!Files.exists(filePath)) {
            throw new DataLoadException("Price file not found: " + filePath.toAbsolutePath());
        }

        List<PriceRecord> records = new ArrayList<>();
        int dataRows = 0;

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            boolean headerSkipped = false;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }

                if (!headerSkipped) {
                    headerSkipped = true;
                    if (isHeader(trimmed)) {
                        continue;
                    }
                }

                dataRows++;
                records.add(parseRow(trimmed, lineNumber));
            }
        } catch (IOException e) {
            throw new DataLoadException("Could not read price file: " + filePath, e);
        }

        if (dataRows == 0 || records.isEmpty()) {
            throw new DataLoadException("Price file is empty: " + filePath.toAbsolutePath());
        }

        return records;
    }

    private boolean isHeader(String line) {
        String[] parts = line.split(",", -1);
        return parts.length > 0 && parts[0].trim().equalsIgnoreCase("date");
    }

    private PriceRecord parseRow(String line, int lineNumber) throws DataLoadException {
        String[] parts = line.split(",", -1);
        if (parts.length != EXPECTED_COLUMNS) {
            throw new DataLoadException(
                    "Malformed row at line " + lineNumber + ": expected " + EXPECTED_COLUMNS
                            + " columns but found " + parts.length + " (" + line + ")");
        }

        try {
            LocalDate date = LocalDate.parse(parts[0].trim());
            String symbol = parts[1].trim();
            if (symbol.isEmpty()) {
                throw new DataLoadException("Malformed row at line " + lineNumber + ": missing symbol");
            }
            double open = Double.parseDouble(parts[2].trim());
            double high = Double.parseDouble(parts[3].trim());
            double low = Double.parseDouble(parts[4].trim());
            double close = Double.parseDouble(parts[5].trim());
            long volume = Long.parseLong(parts[6].trim());

            if (high < Math.max(open, close) || low > Math.min(open, close)) {
                throw new DataLoadException(
                        "Malformed row at line " + lineNumber
                                + ": high/low do not match open/close values");
            }
            if (volume < 0) {
                throw new DataLoadException("Malformed row at line " + lineNumber + ": volume cannot be negative");
            }

            return new PriceRecord(date, symbol, open, high, low, close, volume);
        } catch (DateTimeParseException | NumberFormatException e) {
            throw new DataLoadException(
                    "Malformed row at line " + lineNumber + ": " + e.getMessage() + " (" + line + ")", e);
        }
    }
}
