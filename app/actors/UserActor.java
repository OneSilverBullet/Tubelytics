package actors;

import actors.protocols.UserActorProtocol;
import actors.protocols.VideoSearchActorProtocol;
import actors.protocols.VideoSupervisorActorProtocol;
import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;

import java.util.HashSet;

/**
 * Actor to represent a user
 *
 * @author Wayan-Gwie Lapointe
 */
public class UserActor extends AbstractActor {
    private static final Logger log = LoggerFactory.getLogger(UserActor.class);

    private final ActorRef wsOut;
    private final ActorRef videoSupervisor;
    private final HashSet<String> searches;

    /**
     * Constructor
     * @param wsOut ActorRef of the websocket output
     * @param videoSupervisor ActorRef of the video supervisor
     *
     * @author Wayan-Gwie Lapointe
     */
    public UserActor(ActorRef wsOut, ActorRef videoSupervisor) {
        this.wsOut = wsOut;
        this.videoSupervisor = videoSupervisor;
        this.searches = new HashSet<>();
    }

    /**
     * Create the actor
     * @param wsOut ActorRef of the websocket output
     * @param videoSupervisor ActorRef of the video supervisor
     * @return Props that represent the actor
     *
     * @author Wayan-Gwie Lapointe
     */
    public static Props props(ActorRef wsOut, ActorRef videoSupervisor) {
        return Props.create(UserActor.class, wsOut, videoSupervisor);
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
            .match(UserActorProtocol.ClientRequest.class, this::clientRequest)
            .match(VideoSearchActorProtocol.SearchResult.class, this::searchResult)
            .build();
    }

    /**
     * Handler for relaying search results to the user
     * @param sr Message to handle
     *
     * @author Wayan-Gwie Lapointe
     */
    private void searchResult(VideoSearchActorProtocol.SearchResult sr) {
        wsOut.tell(Json.toJson(sr), self());
        log.info("Relaying results '{}'", sr);
    }

    /**
     * Handler for commands coming from the user
     * @param msg Message to handle
     *
     * @author Wayan-Gwie Lapointe
     */
    private void clientRequest(UserActorProtocol.ClientRequest msg) {
        switch (msg.code) {
            case "start":
                videoSupervisor.tell(new VideoSupervisorActorProtocol.StartSearch(msg.query), self());
                searches.add(msg.query);
                log.info("Received request to start search for query '{}'", msg.query);
                break;
            case "stop":
                videoSupervisor.tell(new VideoSupervisorActorProtocol.EndSearch(msg.query), self());
                searches.remove(msg.query);
                log.info("Received request to stop search for query '{}'", msg.query);
                break;
            default:
                throw new IllegalArgumentException("Unrecognized message code: " + msg.code);
        }
    }

    /**
     * Called when the actor is stopped because the WebSocket connection is closed.
     * Unsubscribes from all searches.
     *
     * @author Wayan-Gwie Lapointe
     */
    @Override
    public void postStop() {
        log.info("User closed, stopping all searches");

        for (String query : searches) {
            videoSupervisor.tell(new VideoSupervisorActorProtocol.EndSearch(query), self());
            log.info("Stopping search for query '{}'", query);
        }
    }
}
