package repositories;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import io.github.cdimascio.dotenv.Dotenv;
import models.ChannelModel;
import models.SearchResultModel;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Repository to interact with the YouTube API
 *
 * @author Wayan-Gwie Lapointe
 */
public class YoutubeRepository implements VideoRepository {
    private static final String API_KEY = Dotenv.load().get("YOUTUBE_API_KEY");
    private final YouTube api;
    private final SearchCache cache;
    private final ChannelCache channelCache;
    private final PlayListsCache channelPlaylistCache;

    /**
     * Build an authorized API client repository.
     *
     * @author Wayan-Gwie Lapointe
     */
    @Inject
    public YoutubeRepository(YouTube client, SearchCache cache, ChannelCache ccache, PlayListsCache pcache) {
        this.cache = cache;
        this.api = client;
        this.channelCache = ccache;
        this.channelPlaylistCache = pcache;
    }

    /**
     * Build an authorized API client repository.
     *
     * @author Wayan-Gwie Lapointe
     */
    public YoutubeRepository() throws IOException, GeneralSecurityException {
        this.api = (new YoutubeProvider(GoogleNetHttpTransport.newTrustedTransport())).get();
        this.cache = new SearchCache();
        this.channelPlaylistCache = new PlayListsCache();
        this.channelCache = new ChannelCache();
    }

    /**
     * Get the YouTube API client
     *
     * @return Current YouTube API client
     * @author Wayan-Gwie Lapointe
     */
    YouTube getAPIClient() {
        return api;
    }

    /**
     * Search the YouTube API
     *
     * @param query Search query
     * @return Stream of search results
     * @author Wayan-Gwie Lapointe
     */
    public CompletionStage<Stream<SearchResultModel>> search(String query) {
        return CompletableFuture.supplyAsync(
            () -> {
                SearchListResponse response = cache.get(query).orElseGet(() -> {
                    try {
                        // Define and execute the API request
                        SearchListResponse newResponse = api
                            .search()
                            .list(Collections.singletonList("snippet"))
                            .setMaxResults(50L)
                            .setOrder("date")
                            .setQ(query)
                            .setType(List.of("video"))
                            .setKey(API_KEY)
                            .execute();
                        cache.put(query, newResponse);
                        return newResponse;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                return response
                    .getItems()
                    .stream()
                    .map(this::mapSearchResultToSearchResultModel);
            }
        );
    }

    /**
     * map searchResult to SearchResultModel
     *
     * @param searchResult searchResult to be mapped
     * @return SearchResultModel
     * @author Feng Zhao
     */
    public SearchResultModel mapSearchResultToSearchResultModel(SearchResult searchResult) {
        return new SearchResultModel(
            searchResult.getId().getVideoId(),
            searchResult.getSnippet().getTitle(),
            searchResult.getSnippet().getChannelTitle(),
            searchResult.getSnippet().getDescription(),
            "https://www.youtube.com/watch?v=" + searchResult.getId().getVideoId(),
            searchResult.getSnippet().getChannelId(),
            searchResult.getSnippet().getThumbnails().getDefault().getUrl()
        );
    }

    /**
     * search YouTube by video ID to get a stream of String
     *
     * @param Id Video ID of YouTube
     * @return CompletionStage of stream of String
     * @author Feng Zhao
     */
    public CompletionStage<Stream<String>> getTagsById(String Id) {
        return CompletableFuture.supplyAsync(
            () -> {
                VideoListResponse response = getYouTubeVideoListResponse(Collections.singletonList(Id));
                if (response.getItems().get(0).getSnippet().getTags() == null) {
                    return Arrays.stream(new String[]{""});
                }
                return response
                    .getItems()
                    .stream()
                    .map(video -> video.getSnippet().getTags())
                    .flatMap(List::stream)
                    .map(str -> str.replaceAll("[`~!@#$%^&*()_+\\[\\]\\\\;',./{}|:\"<>?]", ""));
            }
        );
    }

    /**
     * Channel Search the YouTube API
     *
     * @param channelID Search Channel Prefix
     * @return Stream of search results
     * @author Yulin Zhang
     */
    public CompletableFuture<ChannelModel> getChannelDetails(String channelID) {
        return CompletableFuture.supplyAsync(
            () -> {
                ChannelListResponse response = channelCache.get(channelID)
                    .orElseGet(() -> {
                        try {
                            YouTube.Channels.List channelsListByIdRequest = api.channels()
                                .list(Collections.singletonList("snippet,contentDetails,statistics"))
                                .setKey(API_KEY)
                                .setId(Collections.singletonList(channelID)); // Use the provided channel ID
                            ChannelListResponse responseChannel = channelsListByIdRequest.execute();
                            channelCache.put(channelID, responseChannel);
                            return responseChannel;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

                Channel channel = response.getItems().get(0);

                String uploadsPlaylistId = channel
                    .getContentDetails()
                    .getRelatedPlaylists()
                    .getUploads();

                PlaylistItemListResponse playlistResponse = channelPlaylistCache.get(channelID)
                    .orElseGet(() -> {
                        try {
                            // Get the videos in the uploads playlist
                            YouTube.PlaylistItems.List playlistItemsRequest = api.playlistItems()
                                .list(Collections.singletonList("snippet"))
                                .setPlaylistId(uploadsPlaylistId)
                                .setMaxResults(10L) // Limit the number of results
                                .setKey(API_KEY);

                            PlaylistItemListResponse playlistItemResult = playlistItemsRequest.execute();
                            channelPlaylistCache.put(channelID, playlistItemResult);
                            return playlistItemResult;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });


                List<SearchResultModel> playList = playlistResponse
                    .getItems()
                    .stream()
                    .map(x -> new SearchResultModel(
                        x.getSnippet().getResourceId().getVideoId(),
                        x.getSnippet().getTitle(),
                        x.getSnippet().getChannelTitle(),
                        x.getSnippet().getDescription(),
                        "https://www.youtube.com/watch?v=" + x.getSnippet().getResourceId().getVideoId(),
                        x.getSnippet().getChannelId(),
                        x.getSnippet().getThumbnails().getDefault().getUrl()
                    )).collect(Collectors.toList());

                return new ChannelModel(
                    channel.getSnippet().getTitle(),
                    channel.getSnippet().getDescription(),
                    channel.getSnippet().getCountry(),
                    channel.getStatistics().getViewCount().toString(),
                    channel.getStatistics().getSubscriberCount().toString(),
                    channel.getStatistics().getVideoCount().toString(),
                    channel.getSnippet().getThumbnails().getDefault().getUrl(),
                    playList
                );
            }
        );
    }


    /**
     * get YouTube Video List Response
     *
     * @param Ids list of Video IDs
     * @return VideoListResponse
     * @author Feng Zhao
     */
    public VideoListResponse getYouTubeVideoListResponse(List<String> Ids) {
        try {
            return api
                .videos()
                .list(Collections.singletonList("snippet"))
                .setId(Ids)
                .setMaxResults(50L)
                .setKey(API_KEY)
                .execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Cache specialized for holding YouTube search responses
     *
     * @author Wayan-Gwie Lapointe
     */
    @Singleton
    public static class SearchCache extends Cache<SearchListResponse> {
    }

    /**
     * Cache specialized for holding YouTube Channel Response
     *
     * @author Yulin Zhang
     */
    @Singleton
    public static class ChannelCache extends Cache<ChannelListResponse> {
    }

    /**
     * Cache specialized for holding play list related to the channel
     *
     * @author Yulin Zhang
     */
    @Singleton
    public static class PlayListsCache extends Cache<PlaylistItemListResponse> {
    }

    /**
     * Guice Provider for YouTube API clients
     *
     * @author Wayan-Gwie Lapointe
     */
    public static class YoutubeProvider implements Provider<YouTube> {
        private static final String APPLICATION_NAME = "TubeLytics";
        private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

        private final NetHttpTransport httpTransport;

        /**
         * Build a YouTube API client provider
         *
         * @param httpTransport NetHttpTransport to use
         * @author Wayan-Gwie Lapointe
         */
        @Inject
        public YoutubeProvider(NetHttpTransport httpTransport) {
            this.httpTransport = httpTransport;
        }

        /**
         * Get a new YouTube API client
         *
         * @return YouTube API client
         * @author Wayan-Gwie Lapointe
         */
        @Override
        public YouTube get() {
            return new YouTube.Builder(httpTransport, JSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .build();
        }
    }
}
