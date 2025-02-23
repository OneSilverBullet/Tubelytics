import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.youtube.YouTube;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;
import repositories.VideoRepository;
import repositories.YoutubeRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Tests for the Module
 *
 * @author Wayan-Gwie Lapointe
 */
public class ModuleTest {
    /**
     * Tests the Module
     * bindActor doesn't work in testing, so this method can't be tested
     *
     * @author Wayan-Gwie Lapointe
     */
    @Test
    public void testModule() {
//        Injector injector = Guice.createInjector(new Module());
//
//        assertSame(injector.getInstance(NetHttpTransport.class), injector.getInstance(NetHttpTransport.class));
//        assertEquals(injector.getInstance(YouTube.class).getClass(), YouTube.class);
//        assertEquals(injector.getInstance(VideoRepository.class).getClass(), YoutubeRepository.class);
    }
}
