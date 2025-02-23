package actors;

import actors.protocols.SentimentCalculatorProtocol;
import junit.framework.TestCase;
import models.SearchResultModel;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.Props;
import org.apache.pekko.testkit.javadsl.TestKit;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * testing SentimentCalculatorActor
 *
 * @author Jananee Aruboribaran
 */
public class SentimentCalculatorActorTest extends TestCase {

    private ActorSystem actorSystem;
    private Stream<SearchResultModel> testStream;

    /**
     * initializing all the necessary variables and mocking the SearchResultModel
     *
     * @author Jananee Aruboribaran
     */
    @Override
    public void setUp() {
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

        testStream = Stream.of(test1, test2);
        actorSystem = ActorSystem.create();
    }

    /**
     * check if we receive a Stream of SearchResultModel when we send SentimentCalculatorProtocol.AddSentimentScore object
     *
     * @author Jananee Aruboribaran
     */
    public void testCreateReceive() {
        final Props props = Props.create(SentimentCalculatorActor.class);
        final ActorRef actorRef = actorSystem.actorOf(props);
        final TestKit testKit = new TestKit(actorSystem);

        actorRef.tell(new SentimentCalculatorProtocol.AddSentimentScore(testStream), testKit.getRef());
        List<SearchResultModel> message = (List<SearchResultModel>) testKit.expectMsgClass(Stream.class).collect(Collectors.toList());

        assertEquals(0.0, message.get(0).getGradeLevel());
        assertEquals(0.0, message.get(1).getGradeLevel());
        assertEquals(0.0, message.get(0).getReadingScore());
        assertEquals(0.0, message.get(1).getReadingScore());
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