package stockwatch;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/**
 * Writes analysis reports to CSV using only the Java standard library.
 */
public class ReportExporter {

    public void exportGainersLosers(Path outputPath, List<Stock> gainers, List<Stock> losers)
            throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            writer.write("rank,category,symbol,first_close,last_close,percent_change");
            writer.newLine();
            writeCategory(writer, gainers, "GAINER");
            writeCategory(writer, losers, "LOSER");
        }
    }

    private void writeCategory(BufferedWriter writer, List<Stock> stocks, String category)
            throws IOException {
        int rank = 1;
        for (Stock stock : stocks) {
            writer.write(String.format(Locale.US, "%d,%s,%s,%.2f,%.2f,%.2f",
                    rank++,
                    category,
                    stock.getSymbol(),
                    stock.firstClose(),
                    stock.lastClose(),
                    stock.percentChange()));
            writer.newLine();
        }
    }
}
