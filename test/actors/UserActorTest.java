package actors;

import actors.protocols.UserActorProtocol;
import actors.protocols.VideoSearchActorProtocol;
import actors.protocols.VideoSupervisorActorProtocol;
import com.fasterxml.jackson.databind.JsonNode;
import junit.framework.TestCase;
import models.SearchResultModel;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.testkit.javadsl.TestKit;

import java.util.List;

import static org.mockito.Mockito.mock;

/**
 * testing UserActor
 *
 * @author Jananee Aruboribaran
 */
public class UserActorTest extends TestCase {
    private ActorSystem actorSystem;
    private TestKit wsOutProbe;
    private TestKit videoSupervisorProbe;
    private ActorRef actorRef;
    private List<SearchResultModel> testStream;

    /**
     * initializing all the necessary variables and mocking the SearchResultModel
     *
     * @author Jananee Aruboribaran
     */
    @Override
    public void setUp() {
        actorSystem = ActorSystem.create();
        wsOutProbe = new TestKit(actorSystem);
        videoSupervisorProbe = new TestKit(actorSystem);
        actorRef = actorSystem.actorOf(
            UserActor.props(wsOutProbe.getRef(), videoSupervisorProbe.getRef())
        );
        SearchResultModel test1 = mock(SearchResultModel.class);
        SearchResultModel test2 = mock(SearchResultModel.class);
        testStream = List.of(test1, test2);

    }

    /**
     * check if we receive a Json when we send VideoSearchActorProtocol.MultipleSearchResult object
     *
     * @author Jananee Aruboribaran
     */
    public void testSearchResult() {
        actorRef.tell(new VideoSearchActorProtocol.MultipleSearchResult("query", 0, 0, 0, 0, testStream), videoSupervisorProbe.getRef());
        JsonNode result = wsOutProbe.expectMsgClass(JsonNode.class);

        assertEquals("MultipleResult", result.get("code").asText());
        assertEquals("query", result.get("query").asText());
        assertEquals(2, result.get("results").size());
    }

    /**
     * check if we receive a VideoSupervisorActorProtocol.StartSearch object when we send
     * UserActorProtocol.ClientRequest object with code "start"
     *
     * @author Jananee Aruboribaran
     */
    public void testClientRequestStart() {
        actorRef.tell(new UserActorProtocol.ClientRequest("start", "12345"), wsOutProbe.getRef());
        VideoSupervisorActorProtocol.StartSearch result = videoSupervisorProbe.expectMsgClass(VideoSupervisorActorProtocol.StartSearch.class);
        assertEquals("12345", result.getQuery());

    }

    /**
     * check if we receive VideoSupervisorActorProtocol.EndSearch object when we send
     * UserActorProtocol.ClientRequest object with code "end"
     *
     * @author Jananee Aruboribaran
     */
    public void testClientRequestStop() {
        actorRef.tell(new UserActorProtocol.ClientRequest("stop", "12345"), wsOutProbe.getRef());
        VideoSupervisorActorProtocol.EndSearch result = videoSupervisorProbe.expectMsgClass(VideoSupervisorActorProtocol.EndSearch.class);
        assertEquals("12345",result.getQuery());

    }

    /**
     * check if we receive an exception when we send UserActorProtocol.ClientRequest object with a cade
     * different from "start" or "end"
     *
     * @author Jananee Aruboribaran
     */
    public void testClientRequestException() {
        new TestKit(actorSystem) {
            {
                try {
                    actorRef.tell(new UserActorProtocol.ClientRequest("12345", "12345"), wsOutProbe.getRef());
                    expectNoMessage();
                } catch (IllegalArgumentException e) {
                    assert e.getMessage().contains("Unrecognized message code: 12345");
                }
            }
        };

    }

    /**
     * terminating the actorSystem
     *
     * @author Jananee Aruboribaran
     */
    @Override
    public void tearDown() {
        TestKit.shutdownActorSystem(actorSystem);
        actorSystem = null;
    }
}