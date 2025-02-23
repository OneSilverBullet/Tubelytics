package actors.protocols;

import junit.framework.TestCase;
import models.SearchResultModel;
import static org.mockito.Mockito.mock;
import java.util.stream.Stream;

/**
 * testing ReadingCalculatorProtocol
 *
 * @author Jananee Aruboribaran
 */
public class ReadingCalculatorProtocolTest extends TestCase {

    private ReadingCalculatorProtocol.AddReadingStats addReadingStats;

    /**
     * initializing all the necessary variables and mocking the SearchResultModel
     *
     * @author Jananee Aruboribaran
     */
    @Override
    public void setUp() {
        SearchResultModel result1 = mock(SearchResultModel.class);
        SearchResultModel result2 = mock(SearchResultModel.class);
        Stream<SearchResultModel> resultsStream = Stream.of(result1, result2);
        addReadingStats = new ReadingCalculatorProtocol.AddReadingStats(resultsStream);
    }

    /**
     * testing is the getResults returns the same number if elements
     *
     * @author Jananee Aruboribaran
     */
    public void testReadingCalculateExactCount() {
        assertNotNull(addReadingStats.getResults());
        assertEquals(2, addReadingStats.getResults().count());
    }

    /**
     * testing is the getResults with empty stream
     *
     * @author Jananee Aruboribaran
     */
    public void testReadingCalculateEmptyStream() {
        Stream<SearchResultModel> emptyStream = Stream.empty();
        ReadingCalculatorProtocol.AddReadingStats addReadingStatsEmpty = new ReadingCalculatorProtocol.AddReadingStats(emptyStream);

        assertNotNull(addReadingStatsEmpty.getResults());
        assertEquals(0, addReadingStatsEmpty.getResults().count());
    }

    /**
     * testing is the getResults with specific elements in stream
     *
     * @author Jananee Aruboribaran
     */
    public void testReadingCalculateSpecific() {
        SearchResultModel test1 = new SearchResultModel(
            "id_1",
            "title_1",
            "channel_1",
            "description_1",
            "videoHyperLink_1",
            "channelId_1",
            "thumbnailHyperLink_1");

        SearchResultModel test2 = new SearchResultModel(
            "id_2",
            "title_2",
            "channel_2",
            "description_2",
            "videoHyperLink_2",
            "channelId_2",
            "thumbnailHyperLink_2");

        Stream<SearchResultModel> testStream = Stream.of(test1, test2);
        ReadingCalculatorProtocol.AddReadingStats testAddReadingStats = new ReadingCalculatorProtocol.AddReadingStats(testStream);

        assertTrue(testAddReadingStats.getResults().anyMatch(x -> x.equals(test1)));
    }

}