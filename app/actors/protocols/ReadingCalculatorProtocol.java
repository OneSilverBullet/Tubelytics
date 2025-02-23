package actors.protocols;

import models.SearchResultModel;

import java.util.stream.Stream;

/**
 * Protocol for ReadingCalculatorActor
 *
 * @author Wayan-Gwie Lapointe
 */
public class ReadingCalculatorProtocol {
    /**
     * Unused constructor
     *
     * @author Wayan-Gwie Lapointe
     */
    private ReadingCalculatorProtocol() {
    }

    /**
     * Message for requesting adding of reading stats
     *
     * @author Wayan-Gwie Lapointe
     */
    public static final class AddReadingStats {
        private final Stream<SearchResultModel> results;

        /**
         * Constructor
         * @param results Results to add stats to
         *
         * @author Wayan-Gwie Lapointe
         */
        public AddReadingStats(Stream<SearchResultModel> results) {
            this.results = results;
        }

        /**
         * Get the results
         * @return Results
         *
         * @author Wayan-Gwie Lapointe
         */
        public Stream<SearchResultModel> getResults() {
            return results;
        }
    }
}
