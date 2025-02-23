import actors.*;
import actors.protocols.VideoSearchActorProtocol;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.youtube.YouTube;
import com.google.inject.AbstractModule;
import play.libs.pekko.PekkoGuiceSupport;
import repositories.VideoRepository;
import repositories.YoutubeRepository;

/**
 * Guice module configuring DI
 *
 * @author Wayan-Gwie Lapointe
 */
public class Module extends AbstractModule implements PekkoGuiceSupport {
    /**
     * Configure Guice Injector
     * bindActor doesn't work in testing, so this method can't be tested
     *
     * @author Wayan-Gwie Lapointe
     */
    protected void configure() {
        try {
            bind(NetHttpTransport.class).toInstance(GoogleNetHttpTransport.newTrustedTransport());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        bind(YouTube.class).toProvider(YoutubeRepository.YoutubeProvider.class);
        bind(VideoRepository.class).to(YoutubeRepository.class);

        bindActor(VideoSupervisorActor.class, "video-supervisor-actor");
        bindActor(ReadingCalculatorActor.class, "reading-calculator-actor");
        bindActor(SentimentCalculatorActor.class, "sentiment-calculator-actor");
        bindActor(TagActor.class, "tag-actor");
        bindActor(ChannelActor.class, "channel-actor");
        bindActor(WordStatsActor.class, "word-stats-actor");
        bindActorFactory(VideoSearchActor.class, VideoSearchActorProtocol.Factory.class);
    }
}
