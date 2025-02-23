package models;

import junit.framework.TestCase;

import static org.junit.Assert.assertNotEquals;

/**
 * Test the SearchResultModel
 *
 * @author Jananee Aruboribaraqn
 */
public class SearchResultModelTest extends TestCase {
    private SearchResultModel searchResultModel;

    /**
     * Set up common test data
     *
     * @author Jananee Aruboribaraqn
     */
    @Override
    public void setUp() {
        searchResultModel = new SearchResultModel(
            "Id",
            "Title",
            "Channel",
            "Description",
            "videoLink",
            "channelLink",
            "thumbnailLink"
        );
        searchResultModel.setSentimentScore(0.5);
    }

    /**
     * Tests getTitle
     *
     * @author Feng Zhao
     */
    public void getId() {
        assertEquals("video id", searchResultModel.getId());
    }


    /**
     * Tests getTitle
     *
     * @author Jananee Aruboribaraqn
     */
    public void testGetTitle() {
        assertEquals("Title", searchResultModel.getTitle());
    }

    /**
     * Tests getChannel
     *
     * @author Jananee Aruboribaraqn
     */
    public void testGetChannel() {
        assertEquals("Channel", searchResultModel.getChannel());
    }

    /**
     * Tests getDescription
     *
     * @author Jananee Aruboribaraqn
     */
    public void testGetDescription() {
        assertEquals("Description", searchResultModel.getDescription());
    }

    /**
     * Tests GetVideoHyperlink
     *
     * @author Jananee Aruboribaraqn
     */
    public void testGetVideoHyperlink() {
        assertEquals("videoLink", searchResultModel.getVideoHyperlink());
    }

    /**
     * Tests GetChannelId
     *
     * @author Jananee Aruboribaraqn
     */
    public void testGetChannelID() {
        assertEquals("channelLink", searchResultModel.getChannelID());
    }

    /**
     * Tests GetThumbnailHyperlink
     *
     * @author Jananee Aruboribaraqn
     */
    public void testGetThumbnailHyperlink() {
        assertEquals("thumbnailLink", searchResultModel.getThumbnailHyperlink());
    }

    /**
     * Tests GetSentimentScore
     *
     * @author Jananee Aruboribaraqn
     */
    public void testGetSentimentScore() {
        assertEquals(0.5, searchResultModel.getSentimentScore());
    }

    /**
     * Tests SetSentimentScore
     *
     * @author Jananee Aruboribaraqn
     */
    public void testSetSentimentScore() {
        SearchResultModel model2 = new SearchResultModel(
            "Id",
            "Title",
            "Channel",
            "Description",
            "videoLink",
            "channelLink",
            "thumbnailLink"
        );
        model2.setSentimentScore(1.5);
        assertEquals(1.5, model2.getSentimentScore());
    }

    /**
     * Tests Equals with two object with same variables
     *
     * @author Jananee Aruboribaraqn
     */
    public void testEquals_SameValues() {
        SearchResultModel model2 = new SearchResultModel(
            "Id",
            "Title",
            "Channel",
            "Description",
            "videoLink",
            "channelLink",
            "thumbnailLink"
        );
        model2.setSentimentScore(0.5);
        assertEquals(searchResultModel, model2);
    }

    /**
     * Tests Equals with different ID
     *
     * @author Feng Zhao
     */
    public void testEquals_DifferentID() {
        SearchResultModel model2 = new SearchResultModel(
            "Id2",
            "Title",
            "Channel",
            "Description",
            "videoLink",
            "channelLink",
            "thumbnailLink"
        );
        assertNotEquals(searchResultModel, model2);
    }

    /**
     * Tests Equals with different title
     *
     * @author Jananee Aruboribaraqn
     */
    public void testEquals_DifferentTitle() {
        SearchResultModel model2 = new SearchResultModel(
            "Id",
            "Title2",
            "Channel",
            "Description",
            "videoLink",
            "channelLink",
            "thumbnailLink"
        );
        assertNotEquals(searchResultModel, model2);
    }

    /**
     * Tests Equals with different  channel
     *
     * @author Jananee Aruboribaraqn
     */
    public void testEquals_DifferentChannel() {
        SearchResultModel model2 = new SearchResultModel(
            "Id",
            "Title",
            "Channel2",
            "Description",
            "videoLink",
            "channelLink",
            "thumbnailLink"
        );
        assertNotEquals(searchResultModel, model2);
    }

    /**
     * Tests Equals with different  description
     *
     * @author Jananee Aruboribaraqn
     */
    public void testEquals_DifferentDescription() {
        SearchResultModel model2 = new SearchResultModel(
            "Id",
            "Title",
            "Channel",
            "Description2",
            "videoLink",
            "channelLink",
            "thumbnailLink"
        );
        assertNotEquals(searchResultModel, model2);
    }

    /**
     * Tests Equals with different hyperlink
     *
     * @author Jananee Aruboribaraqn
     */
    public void testEquals_DifferentHyperlinks() {
        SearchResultModel model2 = new SearchResultModel(
            "Id",
            "Title",
            "Channel",
            "Description",
            "videoLink2",
            "channelLink",
            "thumbnailLink"
        );
        assertNotEquals(searchResultModel, model2);
    }

    /**
     * Tests Equals wit different  sentiment score
     *
     * @author Jananee Aruboribaraqn
     */
    public void testEquals_DifferentSentimentScore() {
        SearchResultModel model2 = new SearchResultModel(
            "Id",
            "Title",
            "Channel",
            "Description",
            "videoLink",
            "channelLink",
            "thumbnailLink"
        );
        model2.setSentimentScore(0.7);
        assertNotEquals(searchResultModel, model2);
    }

    /**
     * Tests Equals with different class
     *
     * @author Jananee Aruboribaraqn
     */
    public void testEquals_DifferentClass() {
        assertNotEquals(searchResultModel, new Object());
    }

    /**
     * Tests Equals with null
     *
     * @author Jananee Aruboribaraqn
     */
    public void testEquals_Null() {
        assertNotEquals(searchResultModel, null);
    }

    /**
     * Tests Equals with same object
     *
     * @author Jananee Aruboribaraqn
     */
    public void testEquals_SameObject() {
        assertEquals(searchResultModel, searchResultModel);
    }


    /**
     * Tests hashCode
     *
     * @author Wayan-Gwie Lapointe
     */
    public void testHashCode() {
        SearchResultModel searchResultModel2 = new SearchResultModel(
            "Id",
            "Title",
            "Channel",
            "Description",
            "videoLink",
            "channelLink",
            "thumbnailLink"
        );
        searchResultModel2.setSentimentScore(0.5);

        assertEquals(searchResultModel.hashCode(), searchResultModel2.hashCode());
    }
}