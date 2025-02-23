package repositories;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import models.SearchResultModel;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the YoutubeRepository
 *
 * @author Wayan-Gwie Lapointe and Feng Zhao
 */
public class YoutubeRepositoryTest {

    /**
     * Tests creation of the YoutubeRepository
     *
     * @author Wayan-Gwie Lapointe
     */
    @Test
    public void testCreation() throws GeneralSecurityException, IOException {
        YoutubeRepository repository = new YoutubeRepository();
        assertEquals("TubeLytics", repository.getAPIClient().getApplicationName());
    }

    /**
     * Tests a cache-miss search that successfully returns results
     *
     * @author Wayan-Gwie Lapointe
     */
    @Test
    public void testSearchCacheMissSuccess() throws IOException {
        SearchResultModel[] expected = new SearchResultModel[]{
            new SearchResultModel(
                "11111",
                "Title 1",
                "Channel 1",
                "Description 1",
                "https://www.youtube.com/watch?v=11111",
                "22222",
                "thumbnail_path"
            )
        };

        List<SearchResult> items = new ArrayList<>();
        items.add(getNewSearchResult("Title 1", "Description 1", "Channel 1", "11111", "22222", "thumbnail_path"));
        SearchListResponse youtubeResult = new SearchListResponse();
        youtubeResult.setItems(items);

        YouTube.Search.List result = Mockito.mock(YouTube.Search.List.class);
        when(result.setMaxResults(anyLong())).thenReturn(result);
        when(result.setOrder(anyString())).thenReturn(result);
        when(result.setQ(anyString())).thenReturn(result);
        when(result.setType(anyList())).thenReturn(result);
        when(result.setKey(anyString())).thenReturn(result);
        when(result.execute()).thenReturn(youtubeResult)
            .thenThrow(new RuntimeException("Searched twice instead of caching"));

        YouTube.Search search = Mockito.mock(YouTube.Search.class);
        when(search.list(Collections.singletonList("snippet"))).thenReturn(result);

        YouTube client = Mockito.mock(YouTube.class);
        when(client.search()).thenReturn(search);

        YoutubeRepository repository = new YoutubeRepository(
            client,
            new YoutubeRepository.SearchCache(),
            new YoutubeRepository.ChannelCache(),
            new YoutubeRepository.PlayListsCache()
        );
        assertArrayEquals(expected, repository.search("test").toCompletableFuture().join().toArray());
        assertArrayEquals(expected, repository.search("test").toCompletableFuture().join().toArray());
    }

    /**
     * Tests a cache-hit search that successfully returns results
     *
     * @author Feng Zhao
     */
    @Test
    public void testSearchCacheHitSuccess() {
        SearchResultModel[] expected = new SearchResultModel[]{
            new SearchResultModel("11111", "Title 1", "Channel 1", "Description 1", "https://www.youtube.com/watch?v=11111",
                "22222", "thumbnail_path"
            )
        };

        List<SearchResult> items = new ArrayList<>();
        items.add(getNewSearchResult("Title 1", "Description 1", "Channel 1", "11111", "22222", "thumbnail_path"));
        SearchListResponse youtubeResult = new SearchListResponse();
        youtubeResult.setItems(items);

        YouTube client = Mockito.mock(YouTube.class);
        when(client.search()).thenThrow(new RuntimeException("Searched instead of caching"));

        YoutubeRepository.SearchCache cache = Mockito.mock(YoutubeRepository.SearchCache.class);
        when(cache.get(anyString())).thenReturn(Optional.of(youtubeResult));

        YoutubeRepository.ChannelCache cCache = Mockito.mock(YoutubeRepository.ChannelCache.class);
        ChannelListResponse ChannelListResponse = new ChannelListResponse();
        when(cCache.get(anyString())).thenReturn(Optional.of(ChannelListResponse));

        YoutubeRepository.PlayListsCache pCache = Mockito.mock(YoutubeRepository.PlayListsCache.class);
        PlaylistItemListResponse playlistResponse = new PlaylistItemListResponse();
        when(pCache.get(anyString())).thenReturn(Optional.of(playlistResponse));

        YoutubeRepository repositoryTest = new YoutubeRepository(client, cache, cCache, pCache);
        assertArrayEquals(expected, repositoryTest.search("test").toCompletableFuture().join().toArray());
    }

    /**
     * Tests a search that throws an IOException
     *
     * @author Wayan-Gwie Lapointe
     */
    @Test
    public void testSearchFailed() throws IOException {
        YouTube.Search.List result = Mockito.mock(YouTube.Search.List.class);
        when(result.setMaxResults(10L)).thenReturn(result);
        when(result.setQ(anyString())).thenReturn(result);
        when(result.setKey(anyString())).thenReturn(result);
        when(result.execute()).thenThrow(new IOException());

        YouTube.Search search = Mockito.mock(YouTube.Search.class);
        when(search.list(Collections.singletonList("snippet"))).thenReturn(result);

        YouTube client = Mockito.mock(YouTube.class);
        when(client.search()).thenReturn(search);

        YoutubeRepository.SearchCache cache = Mockito.mock(YoutubeRepository.SearchCache.class);
        when(cache.get(anyString())).thenReturn(Optional.empty());

        YoutubeRepository.ChannelCache ccache = Mockito.mock(YoutubeRepository.ChannelCache.class);
        when(ccache.get(anyString())).thenReturn(Optional.empty());

        YoutubeRepository.PlayListsCache pcache = Mockito.mock(YoutubeRepository.PlayListsCache.class);
        when(pcache.get(anyString())).thenReturn(Optional.empty());

        YoutubeRepository repository = new YoutubeRepository(client, cache, ccache, pcache);
        assertThrows(CompletionException.class, () -> repository.search("test").toCompletableFuture().join());
    }

    /**
     * Create a dummy SearchResult
     *
     * @param title       Title of video
     * @param description Description of video
     * @param channel     Channel of video
     * @param videoId     Id of video
     * @param channelId   Id of channel
     * @param thumbnail   Url of thumbnail
     * @return SearchResult
     * @author Wayan-Gwie Lapointe
     */
    private static SearchResult getNewSearchResult(String title, String description, String channel, String videoId, String channelId, String thumbnail) {
        Thumbnail defaultThumb = new Thumbnail();
        defaultThumb.setUrl(thumbnail);

        ThumbnailDetails thumbnailDetails = new ThumbnailDetails();
        thumbnailDetails.setDefault(defaultThumb);

        SearchResultSnippet snippet = new SearchResultSnippet();
        snippet.setTitle(title);
        snippet.setDescription(description);
        snippet.setChannelTitle(channel);
        snippet.setChannelId(channelId);
        snippet.setThumbnails(thumbnailDetails);

        ResourceId resourceId = new ResourceId();
        resourceId.setVideoId(videoId);

        SearchResult searchResult = new SearchResult();
        searchResult.setId(resourceId);
        searchResult.setSnippet(snippet);

        return searchResult;
    }

    /**
     * Test getYouTubeVideoListResponse with success
     *
     * @throws IOException
     * @author Feng Zhao
     */
    @Test
    public void testGetYouTubeVideoListResponse() throws IOException {
        YouTube.Videos.List result = Mockito.mock(YouTube.Videos.List.class);
        when(result.setId(anyList())).thenReturn(result);
        when(result.setMaxResults(anyLong())).thenReturn(result);
        when(result.setKey(anyString())).thenReturn(result);
        VideoListResponse youtubeResult = new VideoListResponse();
        when(result.execute()).thenReturn(youtubeResult);

        YouTube.Videos videos = Mockito.mock(YouTube.Videos.class);
        when(videos.list(Collections.singletonList("snippet"))).thenReturn(result);

        YouTube client = Mockito.mock(YouTube.class);
        when(client.videos()).thenReturn(videos);

        YoutubeRepository.SearchCache youtubeCache = Mockito.mock(YoutubeRepository.SearchCache.class);
        YoutubeRepository.ChannelCache cCache = Mockito.mock(YoutubeRepository.ChannelCache.class);
        YoutubeRepository.PlayListsCache pCache = Mockito.mock(YoutubeRepository.PlayListsCache.class);

        YoutubeRepository repository = new YoutubeRepository(client, youtubeCache, cCache, pCache);
        assertEquals(youtubeResult, repository.getYouTubeVideoListResponse(List.of("test")));
    }


    /**
     * Test mapToSearchResultModel with success
     *
     * @author Feng Zhao
     */
    @Test
    public void testMapSearchResultToSearchResultModel() {
        SearchResult searchResult = Mockito.mock(SearchResult.class);
        SearchResultSnippet searchResultSnippet = Mockito.mock(SearchResultSnippet.class);

        ResourceId resourceId = Mockito.mock(ResourceId.class);
        when(searchResult.getId()).thenReturn(resourceId);
        when(resourceId.getVideoId()).thenReturn("1");

        when(searchResult.getSnippet()).thenReturn(searchResultSnippet);
        when(searchResultSnippet.getTitle()).thenReturn("Title 1");
        when(searchResultSnippet.getChannelTitle()).thenReturn("Channel 1");
        when(searchResultSnippet.getDescription()).thenReturn("Description 1");
        when(searchResultSnippet.getChannelId()).thenReturn("2");
        ThumbnailDetails thumbnailDetails = Mockito.mock(ThumbnailDetails.class);
        when(searchResultSnippet.getThumbnails()).thenReturn(thumbnailDetails);
        Thumbnail thumbnail = Mockito.mock(Thumbnail.class);
        when(thumbnailDetails.getDefault()).thenReturn(thumbnail);
        when(thumbnail.getUrl()).thenReturn("thumbnail_path");

        SearchResultModel expected = new SearchResultModel("1", "Title 1", "Channel 1", "Description 1", "https://www.youtube.com/watch?v=1", "2", "thumbnail_path");

        YouTube client = Mockito.mock(YouTube.class);
        YoutubeRepository.SearchCache youtubeCache = Mockito.mock(YoutubeRepository.SearchCache.class);
        YoutubeRepository.ChannelCache cCache = Mockito.mock(YoutubeRepository.ChannelCache.class);
        YoutubeRepository.PlayListsCache pCache = Mockito.mock(YoutubeRepository.PlayListsCache.class);
        YoutubeRepository repository = new YoutubeRepository(client, youtubeCache, cCache, pCache);

        assertEquals(expected, repository.mapSearchResultToSearchResultModel(searchResult));
    }

    /**
     * Test getTagsByIdWithSuccess with success
     *
     * @author Feng Zhao
     */
    @Test
    public void testGetTagsByIdWithSuccess() {
        YouTube client = Mockito.mock(YouTube.class);
        YoutubeRepository.SearchCache youtubeCache = Mockito.mock(YoutubeRepository.SearchCache.class);
        YoutubeRepository.ChannelCache cCache = Mockito.mock(YoutubeRepository.ChannelCache.class);
        YoutubeRepository.PlayListsCache pCache = Mockito.mock(YoutubeRepository.PlayListsCache.class);
        YoutubeRepository repository = spy(new YoutubeRepository(client, youtubeCache, cCache, pCache));
        VideoListResponse response = new VideoListResponse();
        Video video = new Video();
        VideoSnippet videoSnippet = new VideoSnippet();
        videoSnippet.setTags(List.of("testTag"));
        video.setSnippet(videoSnippet);
        response.setItems(List.of(video));

        doReturn(response).when(repository).getYouTubeVideoListResponse(anyList());
        List<String> expected = List.of("testTag");
        assertEquals(expected, repository.getTagsById("testTag").toCompletableFuture().join().collect(Collectors.toList()));
    }

    /**
     * Test GetTagsByIdWith with Failure
     *
     * @author Feng Zhao
     */
    @Test
    public void testGetTagsByIdWithFailure() {
        YouTube client = Mockito.mock(YouTube.class);
        YoutubeRepository.SearchCache youtubeCache = Mockito.mock(YoutubeRepository.SearchCache.class);
        YoutubeRepository.ChannelCache cCache = Mockito.mock(YoutubeRepository.ChannelCache.class);
        YoutubeRepository.PlayListsCache pCache = Mockito.mock(YoutubeRepository.PlayListsCache.class);
        YoutubeRepository repository = spy(new YoutubeRepository(client, youtubeCache, cCache, pCache));
        VideoListResponse response = new VideoListResponse();
        Video video = new Video();
        VideoSnippet videoSnippet = new VideoSnippet();
        videoSnippet.setTags(null);
        video.setSnippet(videoSnippet);
        response.setItems(List.of(video));

        doReturn(response).when(repository).getYouTubeVideoListResponse(anyList());
        List<String> expected = List.of("");
        assertEquals(expected, repository.getTagsById("testTag").toCompletableFuture().join().collect(Collectors.toList()));
    }

    /**
     * Test getYouTubeVideoListResponse with Failure
     *
     * @author Feng Zhao
     */
    @Test
    public void testGetYouTubeVideoListResponseFailure() throws IOException {
        YouTube.Videos.List result = Mockito.mock(YouTube.Videos.List.class);
        when(result.setId(anyList())).thenReturn(result);
        when(result.setMaxResults(anyLong())).thenReturn(result);
        when(result.setKey(anyString())).thenReturn(result);
        VideoListResponse youtubeResult = new VideoListResponse();
        when(result.execute()).thenReturn(youtubeResult);

        YouTube.Videos videos = Mockito.mock(YouTube.Videos.class);
        when(videos.list(Collections.singletonList("snippet"))).thenReturn(result);

        YouTube client = Mockito.mock(YouTube.class);
        when(client.videos()).thenReturn(videos);

        YoutubeRepository.SearchCache youtubeCache = Mockito.mock(YoutubeRepository.SearchCache.class);
        YoutubeRepository.ChannelCache cCache = Mockito.mock(YoutubeRepository.ChannelCache.class);
        YoutubeRepository.PlayListsCache pCache = Mockito.mock(YoutubeRepository.PlayListsCache.class);

        YoutubeRepository repository = new YoutubeRepository(client, youtubeCache, cCache, pCache);
        doThrow(IOException.class).when(result).execute();
        assertThrows(RuntimeException.class, () -> repository.getYouTubeVideoListResponse(List.of("test1", "test2")));
    }

    /**
     * Test Search Channel
     *
     * @author Yulin Zhang
     */
    @Test
    public void testSearchChannel() throws IOException {
        YouTube.Channels.List result = Mockito.mock(YouTube.Channels.List.class);
        when(result.setId(anyList())).thenReturn(result);
        when(result.setMaxResults(anyLong())).thenReturn(result);
        when(result.setKey(anyString())).thenReturn(result);

        ChannelListResponse youtubeResult = new ChannelListResponse();
        ChannelContentDetails.RelatedPlaylists related = new ChannelContentDetails.RelatedPlaylists();
        related.setUploads("1234");
        ChannelContentDetails details = new ChannelContentDetails();
        details.setRelatedPlaylists(related);
        Channel channel = new Channel();

        Thumbnail channelThumb = new Thumbnail();
        channelThumb.setUrl("Thumb");

        ThumbnailDetails channelThumbDetails = new ThumbnailDetails();
        channelThumbDetails.setDefault(channelThumb);

        ChannelSnippet channelSnippet = new ChannelSnippet();
        channelSnippet.setTitle("SpaceX Community");
        channelSnippet.setDescription("description");
        channelSnippet.setCountry("Canada");
        channelSnippet.setThumbnails(channelThumbDetails);

        ChannelStatistics stats = new ChannelStatistics();
        stats.setViewCount(BigInteger.valueOf(10012341235L));
        stats.setVideoCount(BigInteger.valueOf(100));
        stats.setSubscriberCount(BigInteger.valueOf(10023429));

        channel.setSnippet(channelSnippet);
        channel.setStatistics(stats);
        channel.setContentDetails(details);
        youtubeResult.setItems(List.of(channel));
        when(result.execute()).thenReturn(youtubeResult);

        YouTube.Channels channels = Mockito.mock(YouTube.Channels.class);
        when(channels.list(Collections.singletonList("snippet,contentDetails,statistics"))).thenReturn(result);

        YouTube.PlaylistItems.List itemResult = Mockito.mock(YouTube.PlaylistItems.List.class);
        when(itemResult.setPlaylistId(anyString())).thenReturn(itemResult);
        when(itemResult.setMaxResults(anyLong())).thenReturn(itemResult);
        when(itemResult.setKey(anyString())).thenReturn(itemResult);


        PlaylistItemListResponse itemResponse = new PlaylistItemListResponse();
        PlaylistItem playlistItem = new PlaylistItem();
        Thumbnail defaultThumb = new Thumbnail();
        defaultThumb.setUrl("Thumb");

        ThumbnailDetails thumbnailDetails = new ThumbnailDetails();
        thumbnailDetails.setDefault(defaultThumb);

        PlaylistItemSnippet snippet = new PlaylistItemSnippet();
        snippet.setTitle("SpaceX Community");
        snippet.setDescription("description");
        snippet.setChannelTitle("channel");
        snippet.setChannelId("channelId");
        snippet.setThumbnails(thumbnailDetails);

        ResourceId resourceId = new ResourceId();
        resourceId.setVideoId("videoId");

        snippet.setResourceId(resourceId);
        playlistItem.setSnippet(snippet);

        itemResponse.setItems(List.of(playlistItem));
        when(itemResult.execute()).thenReturn(itemResponse);

        YouTube.PlaylistItems items = Mockito.mock(YouTube.PlaylistItems.class);
        when(items.list(Collections.singletonList("snippet"))).thenReturn(itemResult);

        YouTube client = Mockito.mock(YouTube.class);
        when(client.channels()).thenReturn(channels);
        when(client.playlistItems()).thenReturn(items);

        YoutubeRepository.SearchCache youtubeCache = Mockito.mock(YoutubeRepository.SearchCache.class);
        YoutubeRepository.ChannelCache cCache = Mockito.mock(YoutubeRepository.ChannelCache.class);
        YoutubeRepository.PlayListsCache pCache = Mockito.mock(YoutubeRepository.PlayListsCache.class);

        YoutubeRepository repository = new YoutubeRepository(client, youtubeCache, cCache, pCache);

        String title = repository.getChannelDetails("UCJuT7Sk2y520XwDrK4FXZqg").join().getTitle();
        assertEquals(title, "SpaceX Community");
    }
}