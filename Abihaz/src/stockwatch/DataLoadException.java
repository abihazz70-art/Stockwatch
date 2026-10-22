package stockwatch;

/**
 * Thrown when price data cannot be loaded (missing file, empty file, or a bad row).
 */
public class DataLoadException extends Exception {

    public DataLoadException(String message) {
        super(message);
    }

    public DataLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
