package actors;

import static org.mockito.Mockito.*;
import actors.TagActor.IDsReturned;
import actors.TagActor.SearchTag;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.testkit.javadsl.TestKit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repositories.VideoRepository;


import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagActorTest {

    private ActorSystem actorSystem;
    private TestKit testKit;
    private VideoRepository mockedVideoRepository;

    @BeforeEach
    public void setUp() {
        actorSystem = ActorSystem.create("TagActorSystem");
        testKit = new TestKit(actorSystem);
        mockedVideoRepository = mock(VideoRepository.class);
    }

    @AfterEach
    public void tearDown() {
        TestKit.shutdownActorSystem(actorSystem);
        actorSystem.terminate();
    }

    /**
     * Test searchTag Actor
     *
     * @author Feng Zhao
     */
    @Test
    public void testSearchTagProcessing() {
        Stream<String> mockIds = Stream.of("v1", "v2");
        CompletionStage<Stream<String>> mockCompletionStage = mock(CompletionStage.class);
        when(mockCompletionStage.toCompletableFuture().join()).thenReturn(mockIds);
        when(mockedVideoRepository.getTagsById(anyString())).thenReturn(mockCompletionStage);
        ActorRef tagActor = actorSystem.actorOf(TagActor.props(mockedVideoRepository));

        SearchTag searchTagMsg = new SearchTag("tag1");
        tagActor.tell(searchTagMsg, testKit.getRef());
        IDsReturned response = testKit.expectMsgClass(IDsReturned.class);
        Assertions.assertEquals(2, response.Ids.toCompletableFuture().join().count());
        Assertions.assertEquals(List.of("v1", "v2"), response.Ids.toCompletableFuture().join().collect(Collectors.toList()));
    }
}