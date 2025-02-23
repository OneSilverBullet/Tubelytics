package services;

import models.SearchResultModel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Service class responsible for calculating word frequency statistics from YouTube search results.
 * <p>
 * This service processes the titles and descriptions of search results to produce a sorted
 * list of word frequencies. It removes standalone punctuation marks and converts text to lowercase
 * for a consistent word count.
 * </p>
 * <p>
 * Example usage:
 * <pre>
 *     WordStatisticsService wordStatisticsService = new WordStatisticsService();
 *     List<WordCount> wordCount = wordStatisticsService.computeWordStatistics(searchResults);
 * </pre>
 * </p>
 *
 * @author Nicolas Alberto Agudelo Herrera
 */
public class WordStatisticsService {

    /**
     * Computes the frequency of each word in the titles and descriptions of YouTube search results.
     * <p>
     * This method concatenates titles and descriptions, removes standalone punctuation,
     * and produces a sorted list where words are sorted in descending order of frequency.
     * </p>
     *
     * @param results Stream of search results containing video titles and descriptions
     * @return A sorted list of {@link WordCount} objects representing words and their frequencies
     * @author Nicolas Alberto Agudelo Herrera
     */
    public List<WordCount> computeWordStatistics(Stream<SearchResultModel> results) {
        String words = results
            .map(item -> item.getTitle() + " " + item.getDescription())
            .collect(Collectors.joining(" "));

        String cleanedWords = words.replaceAll("(?<!\\w)'|'(?!\\w)", "");

        return Arrays.stream(cleanedWords.split("[^\\w']+"))
            .map(String::toLowerCase)
            .filter(word -> !word.isEmpty())
            .collect(Collectors.groupingBy(word -> word, Collectors.counting()))
            .entrySet().stream()
            .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
            .map(entry -> new WordCount(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    /**
     * Represents a word and its corresponding frequency count.
     * <p>
     * This class is used to encapsulate the word along with its count, providing a structured way
     * to manage word frequency data.
     * </p>
     *
     * @author Nicolas Alberto Agudelo Herrera
     */
    public static class WordCount {
        private final String word;
        private final long count;

        public WordCount(String word, long count) {
            this.word = word;
            this.count = count;
        }

        public String getWord() {
            return word;
        }

        public long getCount() {
            return count;
        }
    }
}
