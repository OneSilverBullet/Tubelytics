package actors.protocols;

import junit.framework.TestCase;
import models.SearchResultModel;
import org.apache.pekko.actor.ActorRef;
import java.util.List;
import static org.mockito.Mockito.mock;

/**
 * testing VideoSearchActorProtocol
 *
 * @author Jananee Aruboribaran
 */
public class VideoSearchActorProtocolTest extends TestCase {
    private ActorRef actor;
    private VideoSearchActorProtocol.Subscribe subscribe;
    private VideoSearchActorProtocol.Unsubscribe unsubscribe;
    private VideoSearchActorProtocol.MultipleSearchResult multipleSearchResult;
    private VideoSearchActorProtocol.SingleSearchResult singleSearchResult;
    private VideoSearchActorProtocol.Tick tick;

    /**
     * initializing all the necessary variables and mocking searchResultModel & ActorRef
     *
     * @author Jananee Aruboribaran
     */
    @Override
    public void setUp() {

        actor = mock(ActorRef.class);
        SearchResultModel searchResultModel = mock(SearchResultModel.class);
        SearchResultModel searchResultModel2 = mock(SearchResultModel.class);
        List<SearchResultModel> results = List.of(searchResultModel, searchResultModel2);

        subscribe = new VideoSearchActorProtocol.Subscribe(actor);
        unsubscribe = new VideoSearchActorProtocol.Unsubscribe(actor);
        multipleSearchResult = new VideoSearchActorProtocol.MultipleSearchResult(
            "query_multi",
            2,
            2.5,
            4.5,
            3.4,
            results);
        singleSearchResult = new VideoSearchActorProtocol.SingleSearchResult(
            "query_single",
            searchResultModel);

        tick = new VideoSearchActorProtocol.Tick();
    }

    /**
     * verify if the actor is correctly initialized for Subscribe
     *
     * @author Jananee Aruboribaran
     */
    public void testSubscribeGetUser() {
        assertEquals(actor, subscribe.getUser());
    }

    /**
     * verify if the actor is correctly initialized for Unsubscribe
     *
     * @author Jananee Aruboribaran
     */
    public void testUnsubscribeGetUser() {
        assertEquals(actor, unsubscribe.getUser());
    }

    /**
     * verify that MultipleSearchResult is initialized
     *
     * @author Jananee Aruboribaran
     */
    public void testMultipleSearchResult() {
        assertNotNull(multipleSearchResult);
    }

    /**
     * verify that SingleSearchResult is initialized
     *
     * @author Jananee Aruboribaran
     */
    public void testSingleSearchResult() {
        assertNotNull(singleSearchResult);
    }

    /**
     * verify that Tick is initialized
     *
     * @author Jananee Aruboribaran
     */
    public void testMultipleSearchToString() {
        assertEquals("MultipleSearchResult{code=MultipleResult, query=query_multi, results count=2}", multipleSearchResult.toString());
    }

    /**
     * verify that Tick is initialized
     *
     * @author Jananee Aruboribaran
     */
    public void testSingleSearchToString() {
        assertEquals("SingleSearchResult{code=SingleResult, query=query_single}", singleSearchResult.toString());
    }

    /**
     * verify that Tick is initialized
     *
     * @author Jananee Aruboribaran
     */
    public void testTick() {
        assertNotNull(tick);
    }




}