package actors;

import actors.protocols.ReadingCalculatorProtocol;
import actors.protocols.SentimentCalculatorProtocol;
import actors.protocols.VideoSearchActorProtocol;
import com.google.inject.assistedinject.Assisted;
import models.SearchResultModel;
import org.apache.pekko.actor.AbstractActorWithTimers;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.util.FutureConverters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repositories.VideoRepository;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.Duration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.pekko.pattern.Patterns.ask;

/**
 * Actor to manage a video search
 *
 * @author Wayan-Gwie Lapointe
 */
public class VideoSearchActor extends AbstractActorWithTimers {
    private static final Logger log = LoggerFactory.getLogger(VideoSearchActor.class);
    private final String query;
    private final VideoRepository videos;
    private final HashSet<ActorRef> users;
    private final LinkedHashSet<SearchResultModel> lastResults;
    private final ActorRef readingCalculator;
    private final ActorRef sentimentCalculator;

    /**
     * Constructor
     * @param query Query to search for
     * @param videos Repository to search videos
     * @param readingCalculator ActorRef to a ReadingCalculatorActor
     * @param sentimentCalculator ActorRef to a SentimentCalculatorActor
     *
     * @author Wayan-Gwie Lapointe
     */
    @Inject
    public VideoSearchActor(@Assisted String query, VideoRepository videos, @Named("reading-calculator-actor") ActorRef readingCalculator, @Named("sentiment-calculator-actor") ActorRef sentimentCalculator) {
        this.query = query;
        this.videos = videos;
        this.readingCalculator = readingCalculator;
        this.sentimentCalculator = sentimentCalculator;
        users = new HashSet<>();
        lastResults = new LinkedHashSet<>();
    }

    /**
     * Called when actor is created.
     * Initializes the search timer.
     *
     * @author Wayan-Gwie Lapointe
     */
    @Override
    public void preStart() {
        getTimers().startTimerWithFixedDelay("Timer", new VideoSearchActorProtocol.Tick(), Duration.ofMinutes(2));
    }

    /**
     * Define behaviour of the actor
     * @return behaviour
     *
     * @author Wayan-Gwie Lapointe
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(VideoSearchActorProtocol.Tick.class, msg -> doSearch())
            .match(VideoSearchActorProtocol.Subscribe.class, this::subscribe)
            .match(VideoSearchActorProtocol.Unsubscribe.class, this::unsubscribe)
            .build();
    }

    /**
     * Handler for users subscribing to searches
     * @param msg Message to handle
     *
     * @author Wayan-Gwie Lapointe
     */
    private void subscribe(VideoSearchActorProtocol.Subscribe msg) {
        ActorRef user = msg.getUser();
        users.add(user);
        if (lastResults.isEmpty()) {
            getLatestSearchResults()
                .thenAcceptAsync(results -> results.forEach(lastResults::add))
                .thenRunAsync(() -> sendSubscribeVideos(user));
        }
        else {
            sendSubscribeVideos(user);
        }
        log.info("User subscribed for query: '{}'.", query);
    }

    /**
     * Send batch of videos after subscription
     * @param user ActorRef of the user to send results to
     *
     * @author Wayan-Gwie Lapointe
     */
    private void sendSubscribeVideos(ActorRef user) {
        user.tell(
            new VideoSearchActorProtocol.MultipleSearchResult(
                query,
                Math.min(lastResults.size(), 50),
                lastResults.stream().skip(Math.max(0, lastResults.size() - 50)).mapToDouble(SearchResultModel::getSentimentScore).sum(),
                lastResults.stream().skip(Math.max(0, lastResults.size() - 50)).mapToDouble(SearchResultModel::getReadingScore).sum(),
                lastResults.stream().skip(Math.max(0, lastResults.size() - 50)).mapToDouble(SearchResultModel::getGradeLevel).sum(),
                lastResults.stream().skip(Math.max(0, lastResults.size() - 10)).collect(Collectors.toList())
            ),
            self()
        );
    }

    /**
     * Handler for users unsubscribing to searches
     * @param msg Message to handle
     *
     * @author Wayan-Gwie Lapointe
     */
    private void unsubscribe(VideoSearchActorProtocol.Unsubscribe msg) {
        users.remove(msg.getUser());
        log.info("User unsubscribed for query: '{}'.", query);
    }

    /**
     * Do video search to get new videos
     *
     * @author Wayan-Gwie Lapointe
     */
    private void doSearch() {
        if (!users.isEmpty()) {
            getLatestSearchResults()
                .thenAcceptAsync(results -> {
                    results
                        .filter(result -> !lastResults.contains(result))
                        .forEach(result -> {
                            lastResults.add(result);
                            users.forEach(user -> user.tell(new VideoSearchActorProtocol.SingleSearchResult(query, result), self()));
                        });
                });

            log.info("Search ticked for query: '{}'.", query);
        } else {
            log.info("Search ticked, but no users for query: '{}'.", query);
        }
    }

    /**
     * Do search and processing of videos
     * @return Stream of search results
     *
     * @author Wayan-Gwie Lapointe
     */
    private CompletionStage<Stream<SearchResultModel>> getLatestSearchResults() {
        return videos
            .search(query)
            .thenComposeAsync(results -> FutureConverters.asJava(ask(readingCalculator, new ReadingCalculatorProtocol.AddReadingStats(results), 1000)))
            .thenComposeAsync(results -> FutureConverters.asJava(ask(sentimentCalculator, new SentimentCalculatorProtocol.AddSentimentScore((Stream<SearchResultModel>) results), 1000)))
            .thenApply(results -> (Stream<SearchResultModel>) results);
    }
}
