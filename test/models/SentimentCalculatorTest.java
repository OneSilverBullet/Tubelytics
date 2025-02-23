package models;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SentimentCalculatorTest {

    private SentimentCalculator sentimentCalculator;

    /**
     * setting up all the needed variables
     *
     * @author Jananee Aruboribaraqn
     */
    @Before
    public void setUpBeforeClass() {
        WordAnalyser wordAnalyser = WordAnalyser.getInstance();
        sentimentCalculator = new SentimentCalculator(wordAnalyser);
    }

    /**
     * Tests SentimentScore with positive phrase
     *
     * @author Jananee Aruboribaraqn
     */
    @Test
    public void testSentimentScore_PositivePhrase() {
        String phrase = "happy happy";
        if (sentimentCalculator != null) {
            double score = sentimentCalculator.sentimentScore(phrase);
            assertTrue(score > 0);
        }
    }

    /**
     * Tests SentimentScore with negative phrase
     *
     * @author Jananee Aruboribaraqn
     */
    @Test
    public void testSentimentScore_NegativePhrase() {
        String phrase = "sad sad";
        if (sentimentCalculator != null) {
            double score = sentimentCalculator.sentimentScore(phrase);
            assertTrue(score < 0);
        }
    }

    /**
     * Tests SentimentScore with mixed phrase
     *
     * @author Jananee Aruboribaraqn
     */
    @Test
    public void testSentimentScore_MixedPhrase() {
        String phrase = "What Goes Up Must Come Down";
        if (sentimentCalculator != null) {
            double score = sentimentCalculator.sentimentScore(phrase);
            assertNotEquals(0.0, score, 0.0001);
        }
    }

    /**
     * Tests SentimentScore with empty phrase
     *
     * @author Jananee Aruboribaraqn
     */
    @Test
    public void testSentimentScore_EmptyPhrase() {
        String phrase = "";
        if (sentimentCalculator != null) {
            double score = sentimentCalculator.sentimentScore(phrase);
            assertEquals(0.0, score, 0.0001);
        }
    }

    /**
     * Tests SentimentScore with nonexistent word in a phrase
     *
     * @author Jananee Aruboribaraqn
     */
    @Test
    public void testSentimentScore_UnrecognizedWords() {
        String phrase = "happy helloWorld sad";
        if (sentimentCalculator != null) {
            double score = sentimentCalculator.sentimentScore(phrase);
            assertTrue(score != 0);
        }
    }
}