package actors;
import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.Props;
import repositories.VideoRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public class TagActor extends AbstractActor{

    private final VideoRepository videos;
    @Inject
    public TagActor(VideoRepository videos){
        this.videos = videos;
    }

    /**
     * Creates a Props instance for this actor
     *
     * @return a Props instance configured for TagActor
     *
     * @author Feng Zhao
     */
    public static Props props(VideoRepository videos){
        return Props.create(TagActor.class, videos);
    }

    /**
     * a message protocol for searching video tags
     *
     * @author Feng Zhao
     */
    public static class SearchTag {
        public final String tag;

        /**
         * Constructs a SearchTag message
         *
         * @param tag    tag to search for
         *
         * @author Feng Zhao
         */
        public SearchTag(String tag) {
            this.tag = tag;
        }
    }


    /**
     * a response message containing video IDs
     *
     * @author Feng Zhao
     */
    public static class IDsReturned {

        /**
         * a CompletionStage holding a stream of video IDs matching the search
         *
         * @author Feng Zhao
         */
        public final CompletionStage<Stream<String>> Ids;

        /**
         * Constructs an IDsReturned message
         *
         * @param Ids CompletionStage containing the stream of video IDs
         *
         * @author Feng Zhao
         */
        public IDsReturned(CompletionStage<Stream<String>> Ids) {
            this.Ids = Ids;
        }
    }

    /**
     * Defines the actor's behavior by specifying message handlers
     *
     * @return  Receive builder for this actor
     *
     * @author Feng Zhao
     */
    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(SearchTag.class, msg -> {
                    getSender().tell(new IDsReturned(getTagsById(msg.tag)), getSelf());
                })
                .build();
    }

    /**
     * Retrieves video IDs with a given tag and a VideoRepository
     *
     * @param Id     The ID of the tag to search for
     * @return a CompletionStage containing a stream of video IDs
     *
     * @author Feng Zhao
     */
    private CompletionStage<Stream<String>> getTagsById(String Id){
        return videos.getTagsById(Id);
    }


}
