package actors;

import models.ChannelModel;
import models.SearchResultModel;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.testkit.javadsl.TestKit;
import repositories.VideoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static actors.ChannelActor.*;
import static org.mockito.Mockito.*;

public class ChannelActorTest{
    static ActorSystem system;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        system = ActorSystem.create("ChannelActorTestSystem");
    }
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        TestKit.shutdownActorSystem(system);
        system = null;
    }
    @Test
    public void testChannelActor() {
        new TestKit(system) {
            {
                System.out.println("Starting test");

                // Mock VideoRepository
                VideoRepository mockVideoRepository = mock(VideoRepository.class);

                // Mock future result
                CompletableFuture<ChannelModel> mockFuture = new CompletableFuture<>();
                when(mockVideoRepository.getChannelDetails("test-query")).thenReturn(mockFuture);

                // Create the channelActor
                ActorRef channelActor = system.actorOf(props());

                // Send the ChannelRequest message
                ChannelActor.ChannelRequest searchMsg = new ChannelActor.ChannelRequest("test-query", mockVideoRepository);
                channelActor.tell(searchMsg, getRef());

                // Expect the actor to send a ChannelResponse message
                ChannelActor.ChannelResponse response = expectMsgClass(ChannelResponse.class);

                // Assertions
                Assert.assertNotNull(response);
                Assert.assertEquals(mockFuture, response.channelModel);

                // Verify interaction with the mock repository
                verify(mockVideoRepository).getChannelDetails("test-query");
            }
        };
    }

    @Test
    public void testChannelActorWithSampleData() {
        new TestKit(system) {{
            System.out.println("Starting test with sample data");

            // Mock VideoRepository
            VideoRepository mockVideoRepository = mock(VideoRepository.class);

            // Sample Data
            List<SearchResultModel> videosList = new ArrayList<>();
            String query = "mock query";
            var mockResult = new ChannelModel(
                    "channel title",
                    "channel description",
                    "channel country",
                    "channel view count",
                    "channel subscribe",
                    "channel video count",
                    "channel image link",
                    videosList
            );

            // Mock the returned CompletionStage
            CompletableFuture<ChannelModel> mockFuture = CompletableFuture.completedFuture(mockResult);
            when(mockVideoRepository.getChannelDetails(query)).thenReturn(mockFuture);

            // Create the ChannelActor
            ActorRef channelActor = system.actorOf(props());

            // Send the SearchVideos message
            ChannelActor.ChannelRequest searchMsg = new ChannelRequest(query, mockVideoRepository);
            channelActor.tell(searchMsg, getRef());

            // Expect the actor to send a VideosReturned message
            ChannelActor.ChannelResponse response = expectMsgClass(ChannelResponse.class);

            // Assertions
            Assert.assertNotNull(response);
            Assert.assertEquals(mockFuture, response.channelModel);

            // Verify the response contains the sample data
            response.channelModel.thenAccept(result -> {
                Assert.assertEquals("channel title", result.getTitle());
                Assert.assertEquals("channel description", result.getDescription());
                Assert.assertEquals("channel country", result.getCountry());
                Assert.assertEquals("channel view count", result.getTitle());
                Assert.assertEquals("channel subscribe", result.getSubscriberCount());
                Assert.assertEquals("channel video count", result.getVideoCount());
                Assert.assertEquals("channel image link", result.getThumbnailHyperlink());
            });

            // Verify interaction with the mock repository
            verify(mockVideoRepository).getChannelDetails(query);
        }};
    }
}