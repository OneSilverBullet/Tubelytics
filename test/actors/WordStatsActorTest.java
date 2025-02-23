package actors;

import models.SearchResultModel;
import org.apache.pekko.actor.Props;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.testkit.javadsl.TestKit;
import repositories.VideoRepository;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static actors.WordStatsActor.*;
import static org.mockito.Mockito.*;
/**
 * Test suite for the {@link WordStatsActor} class. This test suite uses the Apache Pekko
 * framework to test the behavior of the WordStatsActor under various scenarios. It includes
 * setup and teardown of the actor system, as well as tests for basic functionality and
 * interactions with mocked dependencies.
 * @author Nicolas Alberto Agudelo Herrera
 */
public class WordStatsActorTest{
    static ActorSystem system;
    /**
     * Sets up the actor system before any tests are executed.
     *
     * @throws Exception if the actor system setup fails.
     * @author Nicolas Alberto Agudelo Herrera
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        system = ActorSystem.create("WordStatsActorTestSystem");
    }
    /**
     * Shuts down the actor system after all tests have been executed.
     *
     * @throws Exception if the actor system shutdown fails.
     * @author Nicolas Alberto Agudelo Herrera
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        TestKit.shutdownActorSystem(system);
        system = null;
    }
    /**
     * Tests the basic functionality of the {@link WordStatsActor}. Ensures that:
     * <ul>
     *     <li>The actor processes the {@link SearchVideos} message correctly.</li>
     *     <li>The actor sends a {@link VideosReturned} message with the correct response.</li>
     *     <li>The {@link VideoRepository} mock is interacted with as expected.</li>
     * </ul>
     * @author Nicolas Alberto Agudelo Herrera
     */
    @Test
    public void testWordStatsActor() {
        // Test for basic WordStatsActor functionality with mocked repository
        new TestKit(system) {
            {
                System.out.println("Starting test");

                // Mock VideoRepository
                VideoRepository mockVideoRepository = mock(VideoRepository.class);

                // Mock future result
                CompletableFuture<Stream<SearchResultModel>> mockFuture = new CompletableFuture<>();
                when(mockVideoRepository.search("test-query")).thenReturn(mockFuture);

                // Create the WordStatsActor
                ActorRef wordStatsActor = system.actorOf(props());

                // Send the SearchVideos message
                SearchVideos searchMsg = new SearchVideos("test-query", mockVideoRepository);
                wordStatsActor.tell(searchMsg, getRef());

                // Mock the search result
                Stream<SearchResultModel> mockStream = Stream.empty();
                mockFuture.complete(mockStream);

                // Expect the actor to send a VideosReturned message
                VideosReturned response = expectMsgClass(VideosReturned.class);

                // Assertions
                Assert.assertNotNull(response);
                Assert.assertEquals(mockFuture, response.Videos);

                // Verify interaction with the mock repository
                verify(mockVideoRepository).search("test-query");
        }
        };
    }
    /**
     * Tests the {@link WordStatsActor} with a sample data set. Ensures that:
     * <ul>
     *     <li>The actor correctly processes a {@link SearchVideos} message with a query.</li>
     *     <li>The response includes the correct data from the mocked {@link VideoRepository}.</li>
     *     <li>The response data is validated for correctness (e.g., IDs, titles, descriptions).</li>
     *     <li>The {@link VideoRepository} mock is interacted with as expected.</li>
     *     @author Nicolas Alberto Agudelo Herrera
     * </ul>
     */
    @Test
    public void testWordStatsActorWithSampleData() {
        new TestKit(system) {{
            System.out.println("Starting test with sample data");

            // Mock VideoRepository
            VideoRepository mockVideoRepository = mock(VideoRepository.class);

            // Sample Data
            String query = "mock query";
            var mockResults = Stream.of(
                    new SearchResultModel(
                            "ID one",
                            "Title One",
                            "Channel A",
                            "Sample description one, unique",
                            "link1",
                            "channellink1",
                            "thumbnail1"
                    ),
                    new SearchResultModel(
                            "ID two",
                            "Title Two,",
                            "Channel B",
                            "Sample description two, I'm testing",
                            "link2",
                            "channellink2",
                            "thumbnail2"
                    )
            );

            // Mock the returned CompletionStage
            CompletableFuture<Stream<SearchResultModel>> mockFuture = CompletableFuture.completedFuture(mockResults);
            when(mockVideoRepository.search(query)).thenReturn(mockFuture);

            // Create the WordStatsActor
            ActorRef wordStatsActor = system.actorOf(props());

            // Send the SearchVideos message
            SearchVideos searchMsg = new SearchVideos(query, mockVideoRepository);
            wordStatsActor.tell(searchMsg, getRef());

            // Expect the actor to send a VideosReturned message
            VideosReturned response = expectMsgClass(VideosReturned.class);

            // Assertions
            Assert.assertNotNull(response);
            Assert.assertEquals(mockFuture, response.Videos);

            // Verify the response contains the sample data
            response.Videos.thenAccept(results -> {
                var resultList = results.toList();
                Assert.assertEquals(2, resultList.size());

                // Validate first result
                SearchResultModel firstResult = resultList.get(0);
                Assert.assertEquals("ID one", firstResult.getId());
                Assert.assertEquals("Title One", firstResult.getTitle());
                Assert.assertEquals("Sample description one, unique", firstResult.getDescription());

                // Validate second result
                SearchResultModel secondResult = resultList.get(1);
                Assert.assertEquals("ID two", secondResult.getId());
                Assert.assertEquals("Title Two,", secondResult.getTitle());
                Assert.assertEquals("Sample description two, I'm testing", secondResult.getDescription());
            });

            // Verify interaction with the mock repository
            verify(mockVideoRepository).search(query);
        }};
    }
}