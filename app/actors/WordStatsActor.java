package actors;

import models.SearchResultModel;
import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repositories.VideoRepository;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;
/**
 * The {@code WordStatsActor} class processes video search requests and returns word statistics
 * based on the search results. It uses the Apache Pekko framework to handle asynchronous
 * communication and interactions with a {@link VideoRepository}.
 * @author Nicolas Alberto Agudelo Herrera
 */
public class WordStatsActor extends AbstractActor {
    private static final Logger log = LoggerFactory.getLogger(WordStatsActor.class);
    /**
     * Creates a {@link Props} object for the {@code WordStatsActor}.
     *
     * @return a {@code Props} object for creating a new instance of {@code WordStatsActor}.
     * @author Nicolas Alberto Agudelo Herrera
     */
    public static Props props(){
        return Props.create(WordStatsActor.class);
    }
    /**
     * Message class representing a request to search for videos.
     * Contains the search query and a reference to the {@link VideoRepository}.
     * @author Nicolas Alberto Agudelo Herrera
     */
    public static class SearchVideos {
        public final String query;
        public final VideoRepository videos;
        /**
         * Constructs a {@code SearchVideos} message.
         *
         * @param query the search query string.
         * @param videos the {@link VideoRepository} to use for the search.
         * @author Nicolas Alberto Agudelo Herrera
         */
        public SearchVideos(String query, VideoRepository videos) {
            this.query = query;
            this.videos = videos;
        }
    }
    /**
     * Message class representing the response to a video search request.
     * Contains the asynchronous search results as a {@link CompletionStage}.
     * @author Nicolas Alberto Agudelo Herrera
     */
    public static class VideosReturned {
        public final CompletionStage<Stream<SearchResultModel>> Videos;
        /**
         * Constructs a {@code VideosReturned} message.
         *
         * @param Videos the asynchronous stream of search results.
         * @author Nicolas Alberto Agudelo Herrera
         */
        public VideosReturned(CompletionStage<Stream<SearchResultModel>> Videos) {
            this.Videos = Videos;
        }
    }
    /**
     * Creates the actor's behavior by defining message handlers.
     * Handles the {@link SearchVideos} message by performing a search
     * and sending back a {@link VideosReturned} message.
     *
     * @return the {@code Receive} object defining the actor's message handlers.
     * @author Nicolas Alberto Agudelo Herrera
     */
    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(SearchVideos.class, msg -> {
                    getSender().tell(new VideosReturned(searchVideos(msg.query, msg.videos)), getSelf());
                    log.info("Calculating Word Stats for search term {}", msg.query);
                })
                .build();
    }
    /**
     * Performs a video search using the provided query and {@link VideoRepository}.
     *
     * @param query the search query string.
     * @param videos the {@link VideoRepository} to use for the search.
     * @return a {@link CompletionStage} containing a stream of {@link SearchResultModel}.
     * @author Nicolas Alberto Agudelo Herrera
     */
    private CompletionStage<Stream<SearchResultModel>> searchVideos(String query, VideoRepository videos){
        return videos.search(query);
    }


}
