package services;

import models.SearchResultModel;
import org.junit.Test;
import services.WordStatisticsService.WordCount;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * Unit tests for the WordStatisticsService class, which is responsible for computing
 * word frequency statistics from YouTube search results.
 * <p>
 * These tests verify that the word frequency calculations in {@link WordStatisticsService}
 * produce the expected results based on sample search result data and handle cases where the
 * input is empty.
 * </p>
 *
 * @see WordStatisticsService
 * @author Nicolas Alberto Agudelo Herrera
 */
public class WordStatisticsServiceTest {

    /**
     * Tests that {@link WordStatisticsService#computeWordStatistics(Stream)} correctly
     * computes word frequencies from a sample set of search results.
     * <p>
     * Verifies the expected size of the result list, checks the presence and count of
     * specific words, and confirms that counts differ for selected words.
     * </p>
     * @author Nicolas Alberto Agudelo Herrera
     */
    @Test
    public void testComputeWordStatistics() {
        WordStatisticsService wordStatisticsService = new WordStatisticsService();
        var mockResults = Stream.of(
            new SearchResultModel(
                "Id one",
                "Title One",
                "Channel A",
                "Sample description one, unique",
                "link1",
                "channellink1",
                "thumbnail1"
            ),
            new SearchResultModel(
                "Id one",
                "Title Two,",
                "Channel B",
                "Sample description two, I'm testing",
                "link2",
                "channellink2",
                "thumbnail2"
            )
        );

        List<WordCount> wordCountMockList = wordStatisticsService.computeWordStatistics(mockResults);


        assertEquals(8, wordCountMockList.size()); // Expected list size for the search models provided.
        // Check if the list contains the word "I'm" and verify its count
        assertTrue(wordCountMockList.stream().anyMatch(wc -> wc.getWord().equals("i'm")));
        long imCount = wordCountMockList.stream().filter(wc -> wc.getWord().equals("i'm")).findFirst().get().getCount();
        assertEquals(1, imCount);
        // Check if the list contains the word "sample" and verify its count
        assertTrue(wordCountMockList.stream().anyMatch(wc -> wc.getWord().equals("sample")));
        long sampleCount = wordCountMockList.stream().filter(wc -> wc.getWord().equals("sample")).findFirst().get().getCount();
        assertEquals(2, sampleCount);
        // Check that the counts for "i'm" and "sample" are not the same
        assertNotEquals(imCount, sampleCount);
    }

    /**
     * Tests that {@link WordStatisticsService#computeWordStatistics(Stream)} correctly handles
     * an input with empty titles and descriptions, producing an empty list.
     * <p>
     * This tests the edge case where the input search results contain no meaningful data
     * to ensure that no entries are mistakenly created.
     * </p>
     * @author Nicolas Alberto Agudelo Herrera
     */
    @Test
    public void testComputeWordStatisticsWithEmptyInput() {
        WordStatisticsService wordStatisticsService = new WordStatisticsService();
        var emptyResults = Stream.of(
            new SearchResultModel(
                "Id one",
                "",
                "Channel A",
                "",
                "link1",
                "channellink1",
                "thumbnail1"
            )); // Empty stream

        List<WordCount> wordCoundMockList = wordStatisticsService.computeWordStatistics(emptyResults);

        // Check that the list is empty
        assertTrue(wordCoundMockList.isEmpty()); // Since title and description are empty the list is empty
    }
}