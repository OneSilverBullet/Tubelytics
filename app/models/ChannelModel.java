package models;


import java.util.ArrayList;
import java.util.List;

/**
 * YouTube Channel Information
 *
 * @author Yulin Zhang
 */
public class ChannelModel {
    private final String title;
    private final String description;
    private final String country;
    private final String viewCount;
    private final String subscriberCount;
    private final String videoCount;
    private final String thumbnailHyperlink;

    private List<SearchResultModel> videosList = new ArrayList<>();

    /**
     * Constructor
     *
     * @author Yulin Zhang
     */
    public ChannelModel(String title, String description, String country, String viewCount, String subscriberCount, String videoCount, String thumbnailHyperlink, List<SearchResultModel> videos) {
        this.title = title;
        this.description = description;
        this.country = country;
        this.viewCount = viewCount;
        this.subscriberCount = subscriberCount;
        this.videoCount = videoCount;
        this.thumbnailHyperlink = thumbnailHyperlink;
        this.videosList = videos;
    }

    /**
     * Get the Title
     *
     * @return String of the Title
     * @author Yulin Zhang
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the Description
     *
     * @return String of the Description
     * @author Yulin Zhang
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the Country
     *
     * @return String of the Country
     * @author Yulin Zhang
     */
    public String getCountry() {
        return country;
    }

    /**
     * Get the View Count
     *
     * @return String of the View Count
     * @author Yulin Zhang
     */
    public String getViewCount() {
        return viewCount;
    }

    /**
     * Get the Subscriber Count
     *
     * @return String of the View Count
     * @author Yulin Zhang
     */
    public String getSubscriberCount() {
        return subscriberCount;
    }

    /**
     * Get the Video Count
     *
     * @return String of the Video Count
     * @author Yulin Zhang
     */
    public String getVideoCount() {
        return videoCount;
    }

    /**
     * Get the Thumbnail Hyperlink
     *
     * @return String of the Thumbnail Hyperlink
     * @author Yulin Zhang
     */
    public String getThumbnailHyperlink() {
        return thumbnailHyperlink;
    }

    /**
     * Get the Information of Videos
     *
     * @return List of the SearchResultModel
     * @author Yulin Zhang
     */
    public List<SearchResultModel> getVideosList() {
        return videosList;
    }
}
