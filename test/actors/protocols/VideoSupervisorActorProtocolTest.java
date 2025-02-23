package actors.protocols;

import junit.framework.TestCase;

import static org.mockito.Mockito.mock;

/**
 * testing VideoSupervisorActorProtocol
 *
 * @author Jananee Aruboribaran
 */
public class VideoSupervisorActorProtocolTest extends TestCase {

    private VideoSupervisorActorProtocol.StartSearch startSearch;
    private VideoSupervisorActorProtocol.EndSearch endSearch;

    /**
     * initializing all the necessary variables and mocking the SearchResultModel
     *
     * @author Jananee Aruboribaran
     */
    @Override
    public void setUp() {
        startSearch = new VideoSupervisorActorProtocol.StartSearch("query_StartSearch");
        endSearch = new VideoSupervisorActorProtocol.EndSearch("query_EndSearch");
    }

    /**
     * testing is the startSearch returns the same string
     *
     * @author Jananee Aruboribaran
     */
    public void testStartSearchQetQuery() {
        assertEquals("query_StartSearch", startSearch.getQuery());
    }

    /**
     * testing is the startSearch returns the same string
     *
     * @author Jananee Aruboribaran
     */
    public void testEndSearchQetQuery() {
        assertEquals("query_EndSearch", endSearch.getQuery());
    }

    /**
     * testing is the startSearch returns the null
     *
     * @author Jananee Aruboribaran
     */
    public void testStartSearchQetQueryNull() {
        VideoSupervisorActorProtocol.StartSearch testStartSearch = mock(VideoSupervisorActorProtocol.StartSearch.class);
        assertNull(testStartSearch.getQuery());
    }

    /**
     * testing is the startSearch returns null
     *
     * @author Jananee Aruboribaran
     */
    public void testEndSearchQetQueryNull() {
        VideoSupervisorActorProtocol.EndSearch testEndSearch = mock(VideoSupervisorActorProtocol.EndSearch.class);
        assertNull(testEndSearch.getQuery());
    }


}