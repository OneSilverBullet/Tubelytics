package models;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotEquals;

/**
 * Test the Channel Model
 *
 * @author Yulin Zhang
 */
public class ChannelModelTest extends TestCase {
    private ChannelModel channelModelInstance;
    private ChannelModel channelModelInstance2;

    /**
     * Set up common test data
     *
     * @author Yulin Zhang
     */
    @Override
    public void setUp() {
        List<SearchResultModel> videosList = new ArrayList<>();
        channelModelInstance = new ChannelModel(
            "channel title",
            "channel description",
            "channel country",
            "channel view count",
            "channel subscribe",
            "channel video count",
            "channel image link",
            videosList
        );

        channelModelInstance2 = new ChannelModel(
                "channel title",
                "channel description",
                "channel country",
                "channel view count",
                "channel subscribe",
                "channel video count",
                "channel image link",
                videosList
        );
    }

    /**
     * Tests getTitle
     *
     * @author Yulin Zhang
     */
    public void testGetTitle() {
        assertEquals("channel title", channelModelInstance.getTitle());
    }

    /**
     * Tests getDescription
     *
     * @author Yulin Zhang
     */
    public void testGetDescription() {
        assertEquals("channel description", channelModelInstance.getDescription());
    }

    /**
     * Tests getCountry
     *
     * @author Yulin Zhang
     */
    public void testGetCountry() {
        assertEquals("channel country", channelModelInstance.getCountry());
    }

    /**
     * Tests getViewCount
     *
     * @author Yulin Zhang
     */
    public void testGetViewCount() {
        assertEquals("channel view count", channelModelInstance.getViewCount());
    }

    /**
     * Tests getSubscriberCount
     *
     * @author Yulin Zhang
     */
    public void testGetSubscriberCount() {
        assertEquals("channel subscribe", channelModelInstance.getSubscriberCount());
    }

    /**
     * Tests getVideoCount
     *
     * @author Yulin Zhang
     */
    public void testGetVideoCount() {
        assertEquals("channel video count", channelModelInstance.getVideoCount());
    }

    /**
     * Tests getThumbnailHyperlink
     *
     * @author Yulin Zhang
     */
    public void testGetThumbnailHyperlink() {
        assertEquals("channel image link", channelModelInstance.getThumbnailHyperlink());
    }


    /**
     * Tests equals
     *
     * @author Yulin Zhang
     */
    public void testEquals() {
        assertEquals(channelModelInstance, channelModelInstance);
    }

    /**
     * Tests equals with new object
     *
     * @author Yulin Zhang
     */
    public void testEqualswithNewObj() {
        assertNotEquals(channelModelInstance, new Object());
    }

    /**
     * Tests equals with different object
     *
     * @author Yulin Zhang
     */
    public void testEqualswithDiffObj() {
        assertNotEquals(channelModelInstance2, channelModelInstance);
    }

    /**
     * Tests hashCode
     *
     * @author Yulin Zhang
     */
    public void testHashCode() {
        assertNotEquals(channelModelInstance.hashCode(), channelModelInstance2.hashCode());
    }
}