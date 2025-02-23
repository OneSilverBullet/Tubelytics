package actors;

import actors.protocols.VideoSearchActorProtocol;
import junit.framework.TestCase;
import models.SearchResultModel;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.Props;
import org.apache.pekko.testkit.javadsl.TestKit;
import repositories.VideoRepository;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * testing VideoSearchActor
 *
 * @author Jananee Aruboribaran
 */
public class VideoSearchActorTest extends TestCase {
    private ActorSystem actorSystem;
    private VideoRepository videoRepositoryMock;
    private ActorRef videoSearchActor;
    private SearchResultModel mockResult;
    private SearchResultModel mockResult2;
    private SearchResultModel mockResult3;

    /**
     * initializing all the necessary variables and mocking the VideoRepository
     *
     * @author Jananee Aruboribaran
     */
    @Override
    public void setUp() {
        actorSystem = ActorSystem.create();
        videoRepositoryMock = mock(VideoRepository.class);
        mockResult = new SearchResultModel(
            "id_1",
            "title_1",
            "channel_1",
            "description_1",
            "videoHyperLink_1",
            "channelId_1",
            "thumbnailHyperLink_1");
        mockResult2 = new SearchResultModel(
            "id_2",
            "title_2",
            "channel_2",
            "description_2",
            "videoHyperLink_2",
            "channelId_2",
            "thumbnailHyperLink_2");


        mockResult3 = new SearchResultModel(
            "id_3",
            "title_3",
            "channel_3",
            "description_3",
            "videoHyperLink_3",
            "channelId_3",
            "thumbnailHyperLink_3");

        ActorRef sentimentCalculatorActor = actorSystem.actorOf(Props.create(SentimentCalculatorActor.class));
        ActorRef readingCalculatorActor = actorSystem.actorOf(Props.create(ReadingCalculatorActor.class));

        when(videoRepositoryMock.search(anyString()))
            .thenReturn(
                CompletableFuture.completedFuture(Stream.of(mockResult, mockResult2)),
                CompletableFuture.completedFuture(Stream.of(mockResult, mockResult2,mockResult3))
            );

        videoSearchActor = actorSystem.actorOf(Props.create(VideoSearchActor.class,
            () -> new VideoSearchActor("testQuery", videoRepositoryMock,readingCalculatorActor ,sentimentCalculatorActor )));
    }

    /**
     * check if we receive a message when we send VideoSearchActorProtocol.Subscribe object
     *
     * @author Jananee Aruboribaran
     */
    public void testCreateReceiveSubscribe() {

        new TestKit(actorSystem) {{
            videoSearchActor.tell(new VideoSearchActorProtocol.Subscribe(getRef()),getRef());
            VideoSearchActorProtocol.MultipleSearchResult search = expectMsgClass(VideoSearchActorProtocol.MultipleSearchResult.class);
            assertEquals(mockResult, search.results.get(0));
            assertEquals(mockResult2, search.results.get(1));
        }};

    }

    /**
     * check if we receive a message when we send VideoSearchActorProtocol.Subscribe object. Then send the same
     * object, when the lastResult filed is full, to see we receive a message
     *
     * @author Jananee Aruboribaran
     */
    public void testCreateReceiveSubscribeLastResultsNotEmpty() {
        new TestKit(actorSystem) {{
            videoSearchActor.tell(new VideoSearchActorProtocol.Subscribe(getRef()), getRef());
            VideoSearchActorProtocol.MultipleSearchResult search = expectMsgClass(VideoSearchActorProtocol.MultipleSearchResult.class);
            assertEquals(mockResult, search.results.get(0));
            assertEquals(mockResult2, search.results.get(1));
            videoSearchActor.tell(new VideoSearchActorProtocol.Subscribe(getRef()), getRef());
            VideoSearchActorProtocol.MultipleSearchResult search2 = expectMsgClass(VideoSearchActorProtocol.MultipleSearchResult.class);
            assertEquals(mockResult, search2.results.get(0));
            assertEquals(mockResult2, search2.results.get(1));
        }};

    }

    /**
     * check if we don't receive a message when we send VideoSearchActorProtocol.Unsubscribe object
     *
     * @author Jananee Aruboribaran
     */
    public void testCreateReceiveUnsubscribe() {
        new TestKit(actorSystem) {{
            videoSearchActor.tell(new VideoSearchActorProtocol.Subscribe(getRef()), getRef());
            VideoSearchActorProtocol.MultipleSearchResult search = expectMsgClass(VideoSearchActorProtocol.MultipleSearchResult.class);
            assertEquals(search.results.get(0), mockResult);
            assertEquals(search.results.get(1), mockResult2);
            videoSearchActor.tell(new VideoSearchActorProtocol.Unsubscribe(getRef()), getRef());
            expectNoMessage();
        }};
    }

    /**
     * check if we don't receive a message when we send VideoSearchActorProtocol.Tick object
     *
     * @author Jananee Aruboribaran
     */
    public void testCreateReceiveTickNoUser() {
        new TestKit(actorSystem) {{
            videoSearchActor.tell(new VideoSearchActorProtocol.Tick(), getRef());
            expectNoMsg();
        }};

    }

    /**
     * check if we receive a message when we send VideoSearchActorProtocol.Subscribe object
     *
     * @author Jananee Aruboribaran
     */
    public void testCreateReceiveTick() {
        new TestKit(actorSystem) {{
            videoSearchActor.tell(new VideoSearchActorProtocol.Subscribe(getRef()), getRef());
            VideoSearchActorProtocol.MultipleSearchResult search = expectMsgClass(VideoSearchActorProtocol.MultipleSearchResult.class);
            assertEquals(search.results.get(0), mockResult);
            assertEquals(search.results.get(1), mockResult2);
            videoSearchActor.tell(new VideoSearchActorProtocol.Tick(), getRef());
            VideoSearchActorProtocol.SingleSearchResult search2 = expectMsgClass(VideoSearchActorProtocol.SingleSearchResult.class);
            assertEquals(mockResult3, search2.result);
        }};
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