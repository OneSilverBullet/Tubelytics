package actors;

import models.ChannelModel;
import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repositories.VideoRepository;

import java.util.concurrent.CompletionStage;

/**
 * This actor handles channel-related requests, interacting with the VideoRepository
 * to fetch channel details based on the search query.
 *
 * @author Yulin Zhang
 */
public class ChannelActor extends AbstractActor {
    // Logger instance to log information and errors for this actor.
    private static final Logger log = LoggerFactory.getLogger(ChannelActor.class);

    /**
     * Static factory method for creating a Props instance.
     *
     * @return Props instance configured for ChannelActor.
     * @author Yulin Zhang
     */
    public static Props props() {
        return Props.create(ChannelActor.class);
    }

    /**
     * Represents a request to fetch channel information.
     *
     * @author Yulin Zhang
     */
    public static class ChannelRequest {
        public final String channelIDQuery; // Channel Id
        public final VideoRepository videos; // The repository used to fetch video data.

        /**
         * Constructor for ChannelRequest.
         *
         * @param channelID The search term for the channel.
         * @param videos The VideoRepository instance.
         * @author Yulin Zhang
         */
        public ChannelRequest(String channelID, VideoRepository videos) {
            this.channelIDQuery = channelID;
            this.videos = videos;
        }
    }

    /**
     * Represents a response containing channel information.
     *
     * @author Yulin Zhang
     */
    public static class ChannelResponse {
        public final CompletionStage<ChannelModel> channelModel;

        /**
         * Constructor for ChannelResponse.
         *
         * @param channelModel A CompletionStage that provides the channel details.
         * @author Yulin Zhang
         */
        public ChannelResponse(CompletionStage<ChannelModel> channelModel) {
            this.channelModel = channelModel;
        }
    }

    /**
     * The main message handler for the actor.
     *
     * @return Receive object defining the behavior of the actor.
     * @author Yulin Zhang
     */
    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(ChannelRequest.class, msg -> {
                    // Process the ChannelRequest message and respond with a ChannelResponse.
                    getSender().tell(new ChannelResponse(searchChannel(msg.channelIDQuery, msg.videos)), getSelf());
                    log.info("Fetched channel info for search term: {}", msg.channelIDQuery);
                })
                .build();
    }

    /**
     * Searches for channel details asynchronously.
     *
     * @param query The search term for the channel.
     * @param videos The VideoRepository instance.
     * @return A CompletionStage containing the ChannelModel result.
     * @author Yulin Zhang
     */
    private CompletionStage<ChannelModel> searchChannel(String query, VideoRepository videos) {
        return videos.getChannelDetails(query);
    }
}
