package models;

import java.util.Arrays;

/**
 * calculating the score
 *
 * @author Jananee Aruboribaran
 */
public class SentimentCalculator {
    private final WordAnalyser analysis;

    /**
     * initializing the analysis private variable
     *
     * @author Jananee Aruboribaran
     */
    public SentimentCalculator(WordAnalyser analysis) {
        this.analysis = analysis;
    }

    /**
     * It splits the phrase into words
     * then uses the get() method from WordAnalyser to get its total score
     * afterward it sums all the score to return a score for the full phrase
     *
     * @param phrase gets a full phrase and return the total score of it
     * @return the sum of score for all the words in the phrase
     * @author Jananee Aruboribaran
     */
    public double sentimentScore(String phrase) {
        String[] words = phrase.split(" ");

        return Arrays.stream(words)
            .map(String::toLowerCase)
            .mapToDouble(analysis::get)
            .sum();
    }
}
