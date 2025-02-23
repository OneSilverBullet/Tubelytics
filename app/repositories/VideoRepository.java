package repositories;

import models.ChannelModel;
import models.SearchResultModel;

import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

/**
 * Interface for a repository return video search results
 *
 * @author Wayan-Gwie Lapointe
 */
public interface VideoRepository {


    /**
     * Search for videos
     *
     * @param query Search query
     * @return Stream of search results
     * @author Wayan-Gwie Lapointe
     */
    CompletionStage<Stream<SearchResultModel>> search(String query);

    /**
     * Search for Channel Information
     *
     * @param channelID Channel ID
     * @return Channel Model
     * @author Yulin Zhang
     */
    CompletionStage<ChannelModel> getChannelDetails(String channelID);

    /**
     * Get tags for a specific Video ID
     *
     * @param Id Video Id
     * @return Stream of tag strings
     * @author Feng Zhao
     */
    CompletionStage<Stream<String>> getTagsById(String Id);
}