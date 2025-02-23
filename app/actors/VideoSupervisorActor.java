package actors;

import actors.protocols.VideoSearchActorProtocol;
import actors.protocols.VideoSupervisorActorProtocol;
import io.github.cdimascio.dotenv.DotenvException;
import org.apache.pekko.actor.AbstractActor;
import org.apache.pekko.actor.ActorRef;
import org.apache.pekko.actor.OneForOneStrategy;
import org.apache.pekko.actor.SupervisorStrategy;
import org.apache.pekko.japi.pf.DeciderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.pekko.InjectedActorSupport;

import javax.inject.Inject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;

/**
 * Actor to supervise VideoSearch actors
 *
 * @author Wayan-Gwie Lapointe
 */
public class VideoSupervisorActor extends AbstractActor implements InjectedActorSupport {
    private static final Logger log = LoggerFactory.getLogger(VideoSupervisorActor.class);
    private final VideoSearchActorProtocol.Factory searchFactory;
    private final HashMap<String, ActorRef> searchManagers;

    // Supervision strategy
    // IOException are presumed to be network errors from YouTube and to be non-fatal so we continue
    // DotenvException is an error when loading the API key, it is fatal so we escalate
    // Any other error we stop the actor
    private final static SupervisorStrategy strategy = new OneForOneStrategy(
        10,
        Duration.ofMinutes(5),
        DeciderBuilder.match(IOException.class, e -> SupervisorStrategy.resume())
            .match(DotenvException.class, e -> SupervisorStrategy.escalate())
            .matchAny(e -> SupervisorStrategy.stop())
            .build()
    );

    /**
     * Constructor
     * @param searchFactory Factory to construct a VideoSearchActor
     *
     * @author Wayan-Gwie Lapointe
     */
    @Inject
    public VideoSupervisorActor(VideoSearchActorProtocol.Factory searchFactory) {
        this.searchFactory = searchFactory;
        this.searchManagers = new HashMap<>();
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
            .match(VideoSupervisorActorProtocol.StartSearch.class, this::startSearch)
            .match(VideoSupervisorActorProtocol.EndSearch.class, this::endSearch)
            .build();
    }

    /**
     * Handler for users starting a search. Creates only one VideoSearchActor for each unique search.
     * @param msg Message to handle
     *
     * @author Wayan-Gwie Lapointe
     */
    private void startSearch(VideoSupervisorActorProtocol.StartSearch msg) {
        String query = msg.getQuery();

        ActorRef search;
        if (!searchManagers.containsKey(query)) {
            search = getSearchActor(query);
            searchManagers.put(query, search);
            log.info("VideoSearch for query: '{}' created.", query);
        } else {
            search = searchManagers.get(query);
            log.info("VideoSearch for query: '{}' already exists.", query);
        }

        search.tell(new VideoSearchActorProtocol.Subscribe(sender()), self());
    }

    /**
     * Handler for users ending a search.
     * @param msg Message to handle
     *
     * @author Wayan-Gwie Lapointe
     */
    private void endSearch(VideoSupervisorActorProtocol.EndSearch msg) {
        String query = msg.getQuery();

        if (searchManagers.containsKey(query)) {
            ActorRef search = searchManagers.get(query);
            search.tell(new VideoSearchActorProtocol.Unsubscribe(sender()), self());
            log.info("User ended search for query {}.", query);
        } else {
            log.info("Impossible to end search for query {}.", query);
        }

    }

    /**
     * Creates a VideoSearchActor as a child actor
     * @param query Query to search in the actor
     * @return ActorRef of the VideoSearchActor
     */
    private ActorRef getSearchActor(String query) {
        String sanitizedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        return injectedChild(() -> searchFactory.create(query), "VideoSearchActor_'" + sanitizedQuery.replace(" ", "$") + "'");
    }

    /**
     * Get supervisor strategy
     * @return Supervisor strategy to use
     */
    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }
}
