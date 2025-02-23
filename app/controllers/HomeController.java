package controllers;

import actors.ChannelActor;
import actors.UserActor;
import actors.WordStatsActor;
import actors.protocols.UserActorProtocol;
import models.ReadingCalculator;
import models.SentimentCalculator;
import models.WordAnalyser;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.ActorSystem;
import org.apache.pekko.pattern.Patterns;
import org.apache.pekko.stream.Materializer;
import org.apache.pekko.util.FutureConverters;
import play.libs.streams.ActorFlow;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.WebSocket;
import repositories.VideoRepository;
import org.apache.pekko.util.FutureConverters;
import services.WordStatisticsService;
import services.WordStatisticsService.WordCount;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import static play.libs.Scala.asScala;
import actors.TagActor;
import org.apache.pekko.util.FutureConverters;
import org.apache.pekko.pattern.Patterns;
import java.util.concurrent.CompletableFuture;


/**
 * Main controller
 *
 * @author Wayan-Gwie Lapointe
 */
public class HomeController extends Controller {
    private final VideoRepository videos;
    private final WordStatisticsService wordStatisticsService;
    private final ActorSystem actorSystem;
    private final Materializer materializer;
    private final ActorRef videoSupervisor;
    private final ActorRef channelActor;
    private final ActorRef tagActor;
    private final ActorRef wordStatsActor;
    private Http.Request request;

    public void setRequest(Http.Request request) {
        this.request = request;
    }

    /**
     * Create a HomeController
     *
     * @param videos Repository to search videos
     * @author Wayan-Gwie Lapointe
     */
    @Inject
    public HomeController(VideoRepository videos, ActorSystem actorSystem, Materializer materializer, @Named("video-supervisor-actor") ActorRef videoSupervisor, @Named("tag-actor") ActorRef tagActor, @Named("word-stats-actor") ActorRef wordStatsActor, @Named("channel-actor")ActorRef channelActor) {
        this.videos = videos;
        this.wordStatisticsService = new WordStatisticsService();
        this.actorSystem = actorSystem;
        this.materializer = materializer;
        this.videoSupervisor = videoSupervisor;
        this.channelActor = channelActor;
        this.tagActor = tagActor;
        this.wordStatsActor = wordStatsActor;
    }

    /**
     * Action that renders the main page, a search box.
     *
     * @author Wayan-Gwie Lapointe
     */
    public Result index(Http.Request request) {
        this.request = request;
        return ok(views.html.index.render(request));
    }

    /**
     * Action that starts the WebSocket connection.
     *
     * @return WebSocket
     * @author Wayan-Gwie Lapointe
     */
    public WebSocket ws() {
        return WebSocket.json(UserActorProtocol.ClientRequest.class)
                .accept(request -> ActorFlow.actorRef(ref -> UserActor.props(ref, videoSupervisor), actorSystem, materializer));
    }

    /**
     * Action that renders the search results skeleton.
     *
     * @param query Search query terms
     * @return Result of rendering the skeleton
     * @author Wayan-Gwie Lapointe
     */
    public Result searchSkeleton(String query) {
        return ok(views.html.searchResultsSkeleton.render(query));
    }

    /**
     * Action that renders the search results.
     *
     * @param query Search query terms
     * @return Async result of rendering the results
     * @author Wayan-Gwie Lapointe
     */
    public CompletionStage<Result> search(String query) {
        return videos
                .search(query)
                .thenApplyAsync(results ->
                        results.peek(x -> {
                            ReadingCalculator calculator = new ReadingCalculator(x.getDescription());
                            x.setReadingScore(calculator.getReadingScore());
                            x.setGradeLevel(calculator.getGradeLevel());
                        })
                )
                .thenApplyAsync(search ->
                        search.peek(y -> {
                            SentimentCalculator cal = new SentimentCalculator(WordAnalyser.getInstance());
                            y.setSentimentScore(cal.sentimentScore(y.getDescription()));
                        })
                )
                .thenApplyAsync(results -> ok(
                        views.html.searchresults.render(
                                query,
                                asScala(results.collect(Collectors.toList()))
                        )
                ));
    }

    /**
     * Action that renders the  results of tag search.
     *
     * @param Id               Video ID string
     * @param Title            Video Title String
     * @param ChannelTitle     Video ChanelTitle
     * @param Description      Video Description
     * @param VideoHyperlink   Video Hyperlink
     * @param ChannelHyperlink Video channel Hyperlink
     * @return Async result of rendering the results
     * @author Feng Zhao
     */
    public CompletionStage<Result> getNewPageWithTag(
            String Id,
            String Title,
            String ChannelTitle,
            String Description,
            String VideoHyperlink,
            String ChannelHyperlink
    ) {
        scala.concurrent.Future<Object> scalaFuture = Patterns.ask(tagActor, new TagActor.SearchTag(Id), 1000);
        CompletionStage<Object> javaFuture = FutureConverters.asJava(scalaFuture);

        return javaFuture.thenComposeAsync(response -> {
            if (response instanceof TagActor.IDsReturned iDsReturned && ((TagActor.IDsReturned) response).Ids != null) {
                return iDsReturned.Ids.thenApplyAsync(tags ->
                        ok(views.html.resultwithtags.render(
                                Title,
                                ChannelTitle,
                                Description,
                                VideoHyperlink,
                                ChannelHyperlink,
                                asScala(tags.collect(Collectors.toList())),
                                this.request
                        ))
                );
            } else {
                return CompletableFuture.completedFuture(internalServerError("Unexpected response"));
            }
        });
    }


    /**
     * Action that gathers word frequency statistics for the titles and descriptions
     * of search results returned from a query to the YouTube API.
     * <p>
     * This method asynchronously communicates with the `WordStatsActor` to retrieve
     * search results for the given query. It uses the `WordStatisticsService` to compute
     * the frequency of each word in the titles and descriptions from the response.
     * If successful, it renders a view to display these statistics in a table format.
     * <p>
     * If the actor's response is unexpected or invalid, or if an error occurs during processing,
     * it returns an internal server error with an appropriate error message.
     *
     * @param query Search query terms
     * @return A {@link CompletionStage} of {@link Result}, which represents either the rendered
     *         word statistics page.
     * @throws RuntimeException if an unexpected error occurs during result processing
     * @author Nicolas Alberto Agudelo Herrera
     */

    public CompletionStage<Result> wordStatistics(String query) {
        scala.concurrent.Future<Object> scalaFuture = Patterns.ask(wordStatsActor, new WordStatsActor.SearchVideos(query, videos), 1000);
        CompletionStage<Object> javaFuture = FutureConverters.asJava(scalaFuture);

        return javaFuture.thenComposeAsync(response ->{
            if (response instanceof WordStatsActor.VideosReturned videosReturned && videosReturned.Videos != null) {
                return videosReturned.Videos.thenApplyAsync(results -> {
                            List<WordCount> sortedWordCount = wordStatisticsService.computeWordStatistics(results);
                            return ok(views.html.wordstats.render(query, sortedWordCount));
                        }
                ).exceptionally(e -> internalServerError("Error processing video results: " + e.getMessage()));
            } else {
                return CompletableFuture.completedFuture(internalServerError("Unexpected response"));
            }
        });
    }

    /**
     * Action that renders the channel page.
     *
     * @param query Channel Search ID
     * @return Async result of rendering the results
     * @author Yulin Zhang
     */
    public CompletionStage<Result> searchChannel(String query) {
        scala.concurrent.Future<Object> scalaFuture = Patterns.ask(channelActor, new ChannelActor.ChannelRequest(query, videos), 1000);
        CompletionStage<Object> javaFuture = FutureConverters.asJava(scalaFuture);

        return javaFuture.thenComposeAsync(response ->{
            if (response instanceof ChannelActor.ChannelResponse channelResponse) {
                return channelResponse.channelModel.thenApplyAsync(result -> {
                            return ok(views.html.channelresults.render(result));
                        }
                );
            } else {
                return CompletableFuture.completedFuture(internalServerError("Unexpected response"));
            }
        });
    }
}
