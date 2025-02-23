package actors;

import actors.protocols.VideoSearchActorProtocol;
import actors.protocols.VideoSupervisorActorProtocol;
import junit.framework.TestCase;
import org.apache.pekko.actor.*;
import org.apache.pekko.testkit.javadsl.TestKit;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * testing VideoSupervisorActor
 *
 * @author Jananee Aruboribaran
 */
public class VideoSupervisorActorTest extends TestCase {
    private ActorSystem actorSystem;
    private ActorRef videoSupervisorActor;
    private TestKit testKit;

    /**
     * initializing all the necessary variables and mocking the VideoSearchActorProtocol.Factory
     *
     * @author Jananee Aruboribaran
     */
    @Override
    public void setUp() {

        actorSystem = ActorSystem.create();
        VideoSearchActorProtocol.Factory searchFactoryMock = mock(VideoSearchActorProtocol.Factory.class);
        testKit = new TestKit(actorSystem);
        when(searchFactoryMock.create(anyString()))
            .thenAnswer(invocation -> new ForwardingActor(testKit.getRef()));
        videoSupervisorActor = actorSystem.actorOf(
            Props.create(VideoSupervisorActor.class, () -> new VideoSupervisorActor(searchFactoryMock))
        );

    }

    /**
     * verifying when the videoSupervisorActor send an object of VideoSupervisorActorProtocol.StartSearch, we
     * receive back an object of VideoSearchActorProtocol.Subscribe
     *
     * @author Jananee Aruboribaran
     */
    public void testCreateReceiveStartSearchNewQuery() {
        new TestKit(actorSystem) {{
            String query = "12345";
            videoSupervisorActor.tell(new VideoSupervisorActorProtocol.StartSearch(query), getRef());
            VideoSearchActorProtocol.Subscribe result = testKit.expectMsgClass(VideoSearchActorProtocol.Subscribe.class);
            assertEquals(getRef(), result.getUser());
        }};
    }

    /**
     * verifying that when an actor is subscribed and videoSupervisorActor send an object of
     * VideoSupervisorActorProtocol.EndSearch, we receive back an object of VideoSearchActorProtocol.Unsubscribe
     *
     * @author Jananee Aruboribaran
     */
    public void testCreateReceiveEndSearchNewQuery() {
        new TestKit(actorSystem) {{
            String query = "12345";
            videoSupervisorActor.tell(new VideoSupervisorActorProtocol.StartSearch(query), getRef());
            VideoSearchActorProtocol.Subscribe result = testKit.expectMsgClass(VideoSearchActorProtocol.Subscribe.class);
            assertEquals(getRef(), result.getUser());
            videoSupervisorActor.tell(new VideoSupervisorActorProtocol.EndSearch(query), getRef());
            VideoSearchActorProtocol.Unsubscribe result2 = testKit.expectMsgClass(VideoSearchActorProtocol.Unsubscribe.class);
            assertEquals(getRef(), result2.getUser());
        }};
    }

    /**
     * checking when a query already exist, a new actor and still subscribe
     *
     * @author Jananee Aruboribaran
     */
    public void testCreateReceiveStartSearchOldQuery() {
        new TestKit(actorSystem) {{
            String query = "12345";
            videoSupervisorActor.tell(new VideoSupervisorActorProtocol.StartSearch(query), getRef());
            VideoSearchActorProtocol.Subscribe result = testKit.expectMsgClass(VideoSearchActorProtocol.Subscribe.class);
            assertEquals(getRef(), result.getUser());
            videoSupervisorActor.tell(new VideoSupervisorActorProtocol.StartSearch(query), getRef());
            VideoSearchActorProtocol.Subscribe result2 = testKit.expectMsgClass(VideoSearchActorProtocol.Subscribe.class);
            assertEquals(getRef(), result2.getUser());
        }};
    }

    /**
     * checking if a query doesn't already exist , we don't receive anything back
     *
     * @author Jananee Aruboribaran
     */
    public void testCreateReceiveEndSearchOldQuery() {
        new TestKit(actorSystem) {{
            String query = "12345";
            videoSupervisorActor.tell(new VideoSupervisorActorProtocol.EndSearch(query), getRef());
            testKit.expectNoMsg();
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

    /**
     * created an actor wrapper to use in the creation of videoSupervisorActor,
     * because the factory returns an actor and pekko uses actorRef
     *
     * @author Jananee Aruboribaran
     */
    static class ForwardingActor extends AbstractActor {

        private final ActorRef target;

        public ForwardingActor(ActorRef target) {
            this.target = target;
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder()
                .matchAny(msg -> target.tell(msg, sender()))
                .build();
        }
    }
}
