package models;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the ReadingCalculator
 *
 * @author Wayan-Gwie Lapointe
 */
public class ReadingCalculatorTest {
    /**
     * Tests ReadingCalculator for an empty description
     *
     * @author Wayan-Gwie Lapointe
     */
    @Test
    public void testEmpty() {
        ReadingCalculator calculator = new ReadingCalculator("");
        assertEquals(0, calculator.getGradeLevel(), 0.00001);
        assertEquals(100, calculator.getReadingScore(), 0.00001);
    }

    /**
     * Tests ReadingCalculator for a blank description
     *
     * @author Wayan-Gwie Lapointe
     */
    @Test
    public void testBlank() {
        ReadingCalculator calculator = new ReadingCalculator("      ");
        assertEquals(0, calculator.getGradeLevel(), 0.00001);
        assertEquals(100, calculator.getReadingScore(), 0.00001);
    }

    /**
     * Tests ReadingCalculator for an easy description
     *
     * @author Wayan-Gwie Lapointe
     */
    @Test
    public void testEasy() {
        ReadingCalculator calculator = new ReadingCalculator("Test that a rabbit is flying");
        assertEquals(2.48, calculator.getGradeLevel(), 0.00001);
        assertEquals(87.95, calculator.getReadingScore(), 0.00001);
    }

    /**
     * Tests ReadingCalculator for a medium description
     *
     * @author Wayan-Gwie Lapointe
     */
    @Test
    public void testMedium() {
        ReadingCalculator calculator = new ReadingCalculator("Test that a rabbit is flying using a rocket powered by a liquid fuelled engine.");
        assertEquals(7.57, calculator.getGradeLevel(), 0.00001);
        assertEquals(67.53, calculator.getReadingScore(), 0.00001);
    }

    /**
     * Tests ReadingCalculator for a hard description
     *
     * @author Wayan-Gwie Lapointe
     */
    @Test
    public void testHard() {
        ReadingCalculator calculator = new ReadingCalculator("Substructural type systems are a family of type systems analogous to substructural logics where one or more of the structural rules are absent or only allowed under controlled circumstances.");
        assertEquals(14.44, calculator.getGradeLevel(), 0.00001);
        assertEquals(43.21, calculator.getReadingScore(), 0.00001);
    }

    /**
     * Tests ReadingCalculator for a description with multiple sentences
     *
     * @author Wayan-Gwie Lapointe
     */
    @Test
    public void testMultipleSentences() {
        ReadingCalculator calculator = new ReadingCalculator("Some elements (like doors) can be added with different tools, so it made more sense to me, even though it may be a bit redundant! Do you like it?");
        assertEquals(4.31, calculator.getGradeLevel(), 0.00001);
        assertEquals(90.01, calculator.getReadingScore(), 0.00001);
    }
}
