package models;

import org.daisy.dotify.api.hyphenator.HyphenatorFactory;
import org.daisy.dotify.hyphenator.impl.LatexHyphenatorFactoryService;

import java.util.Arrays;

/**
 * Calculator for Flesch Reading Ease Score and Flesch-Kincaid Grade Level
 *
 * @author Wayan-Gwie Lapointe
 */
public class ReadingCalculator {
    private static final HyphenatorFactory hyphenatorFactory = (new LatexHyphenatorFactoryService()).newFactory();

    private final String text;
    private long numSentences;
    private double numWords;
    private int numSyllables;

    /**
     * Create a calculator for a piece of text
     *
     * @param text Text to calculate scores on
     * @author Wayan-Gwie Lapointe
     */
    public ReadingCalculator(String text) {
        this.text = text;

        if (!text.isBlank()) {
            String[] words = text.toLowerCase().split(" ");
            this.numSentences = Math.max(1, Arrays.stream(words).filter(l -> l.endsWith(".") || l.endsWith("!") || l.endsWith("?")).count());
            this.numWords = words.length;

            try {
                //Hyphenate the word to get the number of syllables
                int hyphens = countHyphens(text);
                String hyphenated = hyphenatorFactory.newHyphenator("en-US").hyphenate(text);
                this.numSyllables = words.length + countHyphens(hyphenated) - hyphens;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Get number of hyphens in text
     *
     * @param text Text to count hyphens in
     * @return Number of hyphens
     * @author Wayan-Gwie Lapointe
     */
    private static int countHyphens(String text) {
        int hyphens = 0;
        for (char letter : text.toCharArray()) {
            // Handle both kinds of hyphens
            if (letter == '-' || letter == '\u00AD') {
                hyphens++;
            }
        }

        return hyphens;
    }

    /**
     * Get Flesch-Kincaid Grade Level
     *
     * @return Grade level
     * @author Wayan-Gwie Lapointe
     */
    public double getGradeLevel() {
        if (text.isBlank()) {
            return 0;
        }

        return Math.round(100.0 * (0.39 * (numWords / numSentences) + 11.8 * (numSyllables / numWords) - 15.59)) / 100.0;
    }

    /**
     * Get Flesch Reading Ease Score
     *
     * @return Reading ease score
     * @author Wayan-Gwie Lapointe
     */
    public double getReadingScore() {
        if (text.isBlank()) {
            return 100;
        }

        return Math.round(100.0 * (206.835 - 1.015 * (numWords / numSentences) - 84.6 * (numSyllables / numWords))) / 100.0;
    }
}
