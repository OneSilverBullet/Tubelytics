package models;

import java.util.Objects;

/**
 * YouTube search result
 *
 * @author Wayan-Gwie Lapointe
 */
public class SearchResultModel {
    private final String id;
    private final String title;
    private final String channel;
    private final String description;
    private final String videoHyperlink;
    private final String channelID;
    private final String thumbnailHyperlink;
    private double sentimentScore;
    private double readingScore;
    private double gradeLevel;


    /**
     * Create a SearchResultModel
     *
     * @param id                 Id of the video
     * @param title              Title of the video
     * @param channel            Channel of the video
     * @param description        Description of the video
     * @param videoHyperlink     Hyperlink of the video
     * @param channelID          ID of the channel
     * @param thumbnailHyperlink Hyperlink of the thumbnail
     * @author Wayan-Gwie Lapointe
     */

    public SearchResultModel(String id, String title, String channel, String description, String videoHyperlink, String channelID, String thumbnailHyperlink) {
        this.id = id;
        this.channel = channel;
        this.description = description;
        this.title = title;
        this.videoHyperlink = videoHyperlink;
        this.channelID = channelID;
        this.thumbnailHyperlink = thumbnailHyperlink;
        this.sentimentScore = 0.0;
        this.readingScore = 0;
        this.gradeLevel = 0;
    }

    /**
     * Get the id
     *
     * @return Id of the video
     * @author Feng Zhao
     */
    public String getId() {
        return id;
    }


    /**
     * Get the title
     *
     * @return Title of the video
     * @author Wayan-Gwie Lapointe
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the channel
     *
     * @return Channel of the video
     * @author Wayan-Gwie Lapointe
     */
    public String getChannel() {
        return channel;
    }

    /**
     * Get the description
     *
     * @return Description of the video
     * @author Wayan-Gwie Lapointe
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the hyperlink of the video
     *
     * @return Hyperlink of the video
     * @author Wayan-Gwie Lapointe
     */
    public String getVideoHyperlink() {
        return videoHyperlink;
    }

    /**
     * Get the hyperlink of the channel
     *
     * @return Hyperlink of the channel
     * @author Wayan-Gwie Lapointe
     */
    public String getChannelID() {
        return channelID;
    }

    /**
     * Get the hyperlink of the thumbnail
     *
     * @return Hyperlink of the thumbnail
     * @author Wayan-Gwie Lapointe
     */
    public String getThumbnailHyperlink() {
        return thumbnailHyperlink;
    }

    /**
     * Get the sentiment score
     *
     * @return Sentiment score
     * @author Jananee Aruboribaraqn
     */
    public double getSentimentScore() {
        return sentimentScore;
    }

    /**
     * Set the sentiment score
     *
     * @param score Sentiment score
     * @author Jananee Aruboribaraqn
     */
    public void setSentimentScore(double score) {
        sentimentScore = score;
    }

    /**
     * Get the reading score of the description
     *
     * @return Reading score of the description
     * @author Wayan-Gwie Lapointe
     */
    public double getReadingScore() {
        return readingScore;
    }

    /**
     * Set the reading score of the description
     *
     * @param score Reading score of the description
     * @author Wayan-Gwie Lapointe
     */
    public void setReadingScore(double score) {
        readingScore = score;
    }

    /**
     * Get the grade level of the description
     *
     * @return Grade level of the description
     * @author Wayan-Gwie Lapointe
     */
    public double getGradeLevel() {
        return gradeLevel;
    }

    /**
     * Set the grade level of the description
     *
     * @param level Grade level of the description
     * @author Wayan-Gwie Lapointe
     */
    public void setGradeLevel(double level) {
        gradeLevel = level;
    }

    /**
     * Check equality
     *
     * @return Equality result
     * @author Wayan-Gwie Lapointe
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SearchResultModel that = (SearchResultModel) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(channel, that.channel) && Objects.equals(description, that.description) && Objects.equals(videoHyperlink, that.videoHyperlink) && Objects.equals(channelID, that.channelID) && Objects.equals(thumbnailHyperlink, that.thumbnailHyperlink) && Objects.equals(sentimentScore, that.sentimentScore);
    }

    /**
     * Get the hashcode
     *
     * @return Hashcode of the object
     * @author Wayan-Gwie Lapointe
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, title, channel, description, videoHyperlink, channelID, thumbnailHyperlink, sentimentScore);
    }
}
