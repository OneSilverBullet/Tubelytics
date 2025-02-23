package models;

import org.junit.Test;

import static org.junit.Assert.*;

public class WordAnalyserTest {

    /**
     * Tests getInstance and see if we are getting the same instance
     *
     * @author Jananee Aruboribaraqn
     */
    @Test
    public void testGetInstance() {
        WordAnalyser wordAnalyser = WordAnalyser.getInstance();
        WordAnalyser wordAnalyser2 = WordAnalyser.getInstance();
        assertSame(wordAnalyser, wordAnalyser2);
    }

    /**
     * Tests get with valid word
     *
     * @author Jananee Aruboribaraqn
     */
    @Test
    public void testGetValidWord() {
        WordAnalyser wordAnalyser = WordAnalyser.getInstance();
        String validWord = "love";
        double score = wordAnalyser.get(validWord);
        assertTrue(score > 0);
    }

    /**
     * Tests get with invalid word
     *
     * @author Jananee Aruboribaraqn
     */
    @Test
    public void testGetInvalidWord() {
        WordAnalyser wordAnalyser = WordAnalyser.getInstance();
        String invalidWord = "helloWorld";
        double score = wordAnalyser.get(invalidWord);
        assertEquals(0.0, score, 0.0001);
    }

    /**
     * Tests get with empty word
     *
     * @author Jananee Aruboribaraqn
     */
    @Test
    public void testGetEmptyWord() {
        WordAnalyser wordAnalyser = WordAnalyser.getInstance();
        String invalidWord = "";
        double score = wordAnalyser.get(invalidWord);
        assertEquals(0.0, score, 0.0001);
    }
}