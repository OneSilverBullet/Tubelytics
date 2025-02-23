package controllers;

import actors.ChannelActor;
import actors.ChannelActor.*;
import actors.TagActor;
import actors.WordStatsActor;
import actors.WordStatsActor.*;

import models.ChannelModel;
import models.SearchResultModel;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.stream.Materializer;
import org.apache.pekko.testkit.javadsl.TestKit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.WebSocket;
import repositories.VideoRepository;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import static actors.WordStatsActor.props;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static play.mvc.Http.Status.INTERNAL_SERVER_ERROR;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.contentAsString;

/**
 * Tests for the HomeController
 *
 * @author Wayan-Gwie Lapointe and Jananee Aruboribaraqn
 */
public class HomeControllerTest {

    static ActorSystem system;
    private static final Logger log = LoggerFactory.getLogger(HomeControllerTest.class);

    @BeforeClass
    public static void setUpBeforeClass() {
        system = ActorSystem.create("HomeControllerTestSystem");
    }

    @AfterClass
    public static void tearDownAfterClass() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    /**
     * Tests creation of the HomeController
     *
     * @author Jananee Aruboribaraqn
     */
    @Test
    public void testHomeControllerCreation() {
        HomeController homeController = new HomeController(mock(VideoRepository.class), mock(ActorSystem.class), mock(Materializer.class), mock(ActorRef.class), mock(TagActor.class).self(), mock(WordStatsActor.class).self(), mock(ChannelActor.class).self());
        assertNotNull(homeController);
    }

    /**
     * Tests content of the index page
     *
     * @author Wayan-Gwie Lapointe
     */
    @Test
    public void testIndexContent() {
        HomeController homeController = new HomeController(mock(VideoRepository.class), mock(ActorSystem.class), mock(Materializer.class), mock(ActorRef.class), mock(TagActor.class).self(), mock(WordStatsActor.class).self(), mock(ChannelActor.class).self());

        Http.RequestBuilder request = new Http.RequestBuilder()
            .method(GET)
            .uri("/");

        Result result = homeController.index(request.build());
        assertEquals(OK, result.status());
        assertTrue(contentAsString(result).contains("Welcome to YT Lytics!"));
    }

    /**
     * Tests content of successful search results
     *
     * @author Wayan-Gwie Lapointe
     */
    @Test
    public void testSearchContentSuccess() {
        VideoRepository videos = Mockito.mock(VideoRepository.class);
        when(videos.search(anyString())).thenReturn(CompletableFuture.completedFuture(Stream.of(new SearchResultModel(
            "Id 1",
            "Title 1",
            "Channel 1",
            "Description 1",
            "video_link",
            "channel_link",
            "thumbnail_link"
        ))));

        HomeController homeController = new HomeController(videos, mock(ActorSystem.class), mock(Materializer.class), mock(ActorRef.class), mock(TagActor.class).self(), mock(WordStatsActor.class).self(), mock(ChannelActor.class).self());

        Result result = homeController.search("test").toCompletableFuture().join();
        assertEquals(OK, result.status());
        assertTrue(contentAsString(result).contains("Search terms: test"));
        assertTrue(contentAsString(result).contains("Title 1"));
        assertTrue(contentAsString(result).contains("Description 1"));
    }

    /**
     * Tests a failed search
     *
     * @author Wayan-Gwie Lapointe
     */
    @Test
    public void testSearchContentFailure() {
        VideoRepository videos = Mockito.mock(VideoRepository.class);
        when(videos.search(anyString())).thenThrow(RuntimeException.class);

        HomeController homeController = new HomeController(mock(VideoRepository.class), mock(ActorSystem.class), mock(Materializer.class), mock(ActorRef.class), mock(TagActor.class).self(), mock(WordStatsActor.class).self(), mock(ChannelActor.class).self());

        assertThrows(RuntimeException.class, () -> homeController.search("test").toCompletableFuture().join());
    }

    /**
     * Tests a SearchSkeleton
     *
     * @author Jananee Aruboribaran
     */
    @Test
    public void testSearchSkeleton() {
        HomeController homeController = new HomeController(mock(VideoRepository.class), mock(ActorSystem.class), mock(Materializer.class), mock(ActorRef.class), mock(TagActor.class).self(), mock(WordStatsActor.class).self(), mock(ChannelActor.class).self());

        String testQuery = "testQuery";
        Result result = homeController.searchSkeleton(testQuery);

        assertEquals(OK, result.status());
        String content = contentAsString(result);
        assertNotNull(content);
        assertTrue(content.contains("testQuery"));
    }

    /**
     * Tests a ws
     *
     * @author Jananee Aruboribaran
     */
    @Test
    public void testWs() {
        HomeController homeController = new HomeController(mock(VideoRepository.class), mock(ActorSystem.class), mock(Materializer.class), mock(ActorRef.class), mock(TagActor.class).self(), mock(WordStatsActor.class).self(), mock(ChannelActor.class).self());
        WebSocket webSocket = homeController.ws();
        assertNotNull(webSocket);
    }

    /**
     * test a success GetNewPageWithTag invocation
     *
     * @author Feng Zhao
     */
    @Test
    public void testGetNewPageWithTagSuccess(){
        new TestKit(system) {{
            VideoRepository mockVideoRepository = mock(VideoRepository.class);
            ActorRef tagActor = system.actorOf(TagActor.props(mockVideoRepository));

            String testId = "testId";
            Stream<String> mockResult = Stream.of("tag1", "tag2");
            CompletionStage<Stream<String>> mockFuture = CompletableFuture.completedFuture(mockResult);
            when(mockVideoRepository.getTagsById(testId)).thenReturn(mockFuture);
            TagActor.IDsReturned mockResponse = new TagActor.IDsReturned(mockFuture);

            new Thread(() -> {
                try {
                    TagActor.SearchTag message = expectMsgClass(TagActor.SearchTag.class);
                    assertEquals(testId, message.tag);
                    getRef().tell(mockResponse, ActorRef.noSender());
                } catch (AssertionError e) {
                    log.error(e.getMessage());
                }
            }).start();

            HomeController homeController = new HomeController(mockVideoRepository, system, null, null,  tagActor, null, null);
            Http.Request mockHttpRequest = mock(Http.Request.class);
            homeController.setRequest(mockHttpRequest);

            Result result = homeController
                    .getNewPageWithTag(
                            "testId",
                            "testTitle",
                            "testChannelTitle",
                            "testDescription",
                            "testVideoHyperlink",
                            "testChannelHyperlink")
                    .toCompletableFuture()
                    .join();

            assertEquals(OK, result.status());
            String content = contentAsString(result);
            assertTrue(content.contains("tag1"));
            assertTrue(content.contains("tag2"));
        }};
    }

    /**
     * test a fail GetNewPageWithTag invocation
     *
     * @author Feng Zhao
     */
    @Test
    public void testGetNewPageWithTagFail(){
        new TestKit(system) {{
            VideoRepository mockVideoRepository = mock(VideoRepository.class);
            ActorRef tagActor = system.actorOf(TagActor.props(mockVideoRepository));
            new Thread(() -> {
                try {
                    TagActor.SearchTag message = expectMsgClass(TagActor.SearchTag.class);
                    getRef().tell(null, ActorRef.noSender());
                } catch (AssertionError e) {
                    log.error(e.getMessage());
                }
            }).start();

            HomeController homeController = new HomeController(mockVideoRepository, system, null, null,  tagActor, null, null);
            Http.Request mockHttpRequest = mock(Http.Request.class);
            homeController.setRequest(mockHttpRequest);


            Result result = homeController
                    .getNewPageWithTag(
                            "testId",
                            "testTitle",
                            "testChannelTitle",
                            "testDescription",
                            "testVideoHyperlink",
                            "testChannelHyperlink")
                    .toCompletableFuture()
                    .join();

            assertEquals(INTERNAL_SERVER_ERROR, result.status());
            assertTrue(contentAsString(result).contains("Unexpected response"));
        }};
    }


    /**
     * Tests the {@link HomeController#wordStatistics(String)} method for a successful case.
     * <p>
     * This test simulates a search operation with mock data using a {@link VideoRepository}.
     * It verifies that the HTML response includes the expected content and checks the word
     * count statistics in the generated HTML table.
     * </p>
     *
     * @author Nicolas Alberto Agudelo Herrera
     */
    @Test
    public void testWordStatistics() {
        new TestKit(system) {{
            // Mock VideoRepository
            VideoRepository mockVideoRepository = mock(VideoRepository.class);

            // Create the WordStatsActor
            ActorRef wordStatsActor = system.actorOf(props());

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
                    "Title Two",
                    "Channel B",
                    "Sample description two, I'm testing",
                    "link2",
                    "channellink2",
                    "thumbnail2"
                )
            );

            // Mock the actor response
            CompletionStage<Stream<SearchResultModel>> mockFuture = CompletableFuture.completedFuture(mockResults);
            when(mockVideoRepository.search(query)).thenReturn(mockFuture);
            VideosReturned mockResponse = new VideosReturned(mockFuture);

            // Expect the actor to receive a SearchVideos message and reply with the mock response
            new Thread(() -> {
                try {
                    SearchVideos message = expectMsgClass(SearchVideos.class);
                    assertEquals(query, message.query);
                    getRef().tell(mockResponse, ActorRef.noSender());
                } catch (AssertionError e) {
                    log.error(e.getMessage());
                }
            }).start();

            // Create HomeController with mocks
            HomeController homeController = new HomeController(mockVideoRepository, system, null, null, null, wordStatsActor, null);

            // Call the wordStatistics method
            Result result = homeController.wordStatistics(query).toCompletableFuture().join();

            // Validate the response
            assertEquals(OK, result.status());
            String content = contentAsString(result);

            // Check for expected content
            assertTrue(content.contains("Word Statistics for \"" + query + "\""));
            assertTrue(content.contains("<table"));

            // Parse and validate HTML
            Document doc = Jsoup.parse(content);
            Elements rows = doc.select("table tbody tr");

            // Check specific word counts
            for (Element row : rows) {
                String word = row.select("td").get(0).text(); // First column is the word
                String countStr = row.select("td").get(1).text(); // Second column is the count
                int count = Integer.parseInt(countStr);

                // Expected results
                switch (word) {
                    case "title":
                    case "one":
                    case "sample":
                    case "description":
                    case "two":
                        assertEquals(2, count);
                        break;
                    case "unique":
                    case "I'm":
                    case "testing":
                        assertEquals(1, count);
                        break;
                    default:
                        // Handle unexpected words if necessary
                        break;
                }
            }
        }};
    }

    @Test
    public void testWordStatsNullResponse() {
        new TestKit(system) {{
            // Mock VideoRepository
            VideoRepository mockVideoRepository = mock(VideoRepository.class);

            // Create the WordStatsActor
            ActorRef wordStatsActor = system.actorOf(props());

            // Mock the actor to respond with an unexpected type
            new Thread(() -> {
                try {
                    // Expect a SearchVideos message and respond with a plain String
                    SearchVideos message = expectMsgClass(SearchVideos.class);
                    assertEquals("something", message.query); // Confirm the correct query is sent
                    getRef().tell("Unexpected Response", ActorRef.noSender()); // Send an unexpected response
                } catch (AssertionError e) {
                    log.error(e.getMessage());
                }
            }).start();

            // Create HomeController with mocks
            HomeController homeController = new HomeController(mockVideoRepository, system, null, null, null, wordStatsActor, null);

            // Call the wordStatistics method
            Result result = homeController.wordStatistics("something").toCompletableFuture().join();

            // Validate the response status
            assertEquals(INTERNAL_SERVER_ERROR, result.status());
            String content = contentAsString(result);
            System.out.println(content);
            assertTrue(content.contains("Unexpected response"));
        }};
    }

    @Test
    public void testWordStatsServerErrorResponse() {
        new TestKit(system) {{
            // Mock VideoRepository
            VideoRepository mockVideoRepository = mock(VideoRepository.class);

            // Mock a search that results in a server error (failed CompletionStage)
            CompletableFuture<Stream<SearchResultModel>> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new RuntimeException("Server error during video search"));
            when(mockVideoRepository.search("error-query")).thenReturn(failedFuture);

            // Create the WordStatsActor
            ActorRef wordStatsActor = system.actorOf(props());

            // Mock the actor response to return VideosReturned with the failed future
            new Thread(() -> {
                try {
                    SearchVideos message = expectMsgClass(SearchVideos.class);
                    assertEquals("error-query", message.query);

                    VideosReturned mockResponse = new VideosReturned(failedFuture);
                    getRef().tell(mockResponse, ActorRef.noSender());
                } catch (AssertionError e) {
                    log.error(e.getMessage());
                }
            }).start();

            // Create HomeController with mocks
            HomeController homeController = new HomeController(mockVideoRepository, system, null, null, null, wordStatsActor, null);

            // Call the wordStatistics method
            Result result = homeController.wordStatistics("error-query").toCompletableFuture().join();

            // Validate the response
            assertEquals(INTERNAL_SERVER_ERROR, result.status());
            String content = contentAsString(result);

            // Validate the error message
            assertTrue(content.contains("Error processing video results"));
            assertTrue(content.contains("Server error during video search"));
        }};
    }



    /**
     * Tests a channel search operation
     *
     * @author Yulin Zhang
     */
    @Test
    public void testSearchChannel() {
        new TestKit(system) {{
            // Mock VideoRepository
            VideoRepository mockVideoRepository = mock(VideoRepository.class);

            // Create the channel actor
            ActorRef channelActor = system.actorOf(ChannelActor.props());

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

            // Mock the actor response
            CompletionStage<ChannelModel> mockFuture = CompletableFuture.completedFuture(mockResult);
            when(mockVideoRepository.getChannelDetails(query)).thenReturn(mockFuture);
            ChannelResponse mockResponse = new ChannelResponse(mockFuture);

            // Expect the actor to receive a ChannelRequest message and reply with the mock response
            new Thread(() -> {
                try {
                    ChannelRequest message = expectMsgClass(ChannelRequest.class);
                    assertEquals(query, message.channelIDQuery);
                    getRef().tell(mockResponse, ActorRef.noSender());
                } catch (AssertionError e) {
                    log.error(e.getMessage());
                }
            }).start();

            // Create HomeController with mocks
            HomeController homeController = new HomeController(mockVideoRepository, system, null, null,  null, null, channelActor);

            // Call the channel search method
            Result result = homeController.searchChannel(query).toCompletableFuture().join();

            // Validate the response
            assertEquals(OK, result.status());
            String content = contentAsString(result);

            // Check for expected content
            assertTrue(content.contains("Subscribers"));
        }};
    }
}
