package actors.protocols;

import models.SearchResultModel;

import java.util.stream.Stream;

/**
 * Protocol for SentimentCalculatorActor
 *
 * @author Jananee Aruboribaran
 */
public class SentimentCalculatorProtocol {
    /**
     * Unused constructor
     *
     * @author Jananee Aruboribaran
     */
    private SentimentCalculatorProtocol() {
    }

    /**
     * Message for requesting adding of sentiment score
     *
     * @author Jananee Aruboribaran
     */
    public static final class AddSentimentScore {
        private final Stream<SearchResultModel> results;

        /**
         * Constructor
         * @param results Results to add sentiment to
         *
         * @author Jananee Aruboribaran
         */
        public AddSentimentScore(Stream<SearchResultModel> results) {
            this.results = results;
        }

        /**
         * Get the results
         * @return Results
         *
         * @author Jananee Aruboribaran
         */
        public Stream<SearchResultModel> getResults() {
            return results;
        }
    }
}
