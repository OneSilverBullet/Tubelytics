package actors.protocols;

import models.SearchResultModel;
import org.apache.pekko.actor.Actor;
import org.apache.pekko.actor.ActorRef;

import java.util.List;

/**
 * Protocol for VideoSearchActor messages
 *
 * @author Wayan-Gwie Lapointe
 */
public class VideoSearchActorProtocol {
    /**
     * Unused constructor
     *
     * @author Wayan-Gwie Lapointe
     */
    private VideoSearchActorProtocol() {
    }

    /**
     * Factory interface for injection
     *
     * @author Wayan-Gwie Lapointe
     */
    public interface Factory {
        /**
         * Method to create a VideoSearchActor
         * @param query Search query of the actor
         * @return VideoSearchActor instance
         *
         * @author Wayan-Gwie Lapointe
         */
        Actor create(String query);
    }

    /**
     * SearchResult interface to unify handling of search result messages
     *
     * @author Wayan-Gwie Lapointe
     */
    public interface SearchResult {
    }

    /**
     * Tick message to re-search
     *
     * @author Wayan-Gwie Lapointe
     */
    public static final class Tick {
    }

    /**
     * Subscribe message to add a user to a search
     *
     * @author Wayan-Gwie Lapointe
     */
    public static final class Subscribe {
        private final ActorRef user;

        /**
         * Constructor
         * @param user User to subscribe
         *
         * @author Wayan-Gwie Lapointe
         */
        public Subscribe(ActorRef user) {
            this.user = user;
        }

        /**
         * Get user
         * @return User
         *
         * @author Wayan-Gwie Lapointe
         */
        public ActorRef getUser() {
            return user;
        }
    }

    /**
     * Unsubscribe message to remove a user to a search
     *
     * @author Wayan-Gwie Lapointe
     */
    public static final class Unsubscribe {
        private final ActorRef user;

        /**
         * Constructor
         * @param user User to unsubscribe
         *
         * @author Wayan-Gwie Lapointe
         */
        public Unsubscribe(ActorRef user) {
            this.user = user;
        }

        /**
         * Get user
         * @return User
         *
         * @author Wayan-Gwie Lapointe
         */
        public ActorRef getUser() {
            return user;
        }
    }

    /**
     * MultipleSearchResult message to indicate a batch of results for a query
     *
     * @author Wayan-Gwie Lapointe
     */
    public static final class MultipleSearchResult implements SearchResult {
        public final String code = "MultipleResult";
        public final String query;
        public final int totalCount;
        public final double totalSentimentScore;
        public final double totalReadingScore;
        public final double totalReadingGrade;
        public final List<SearchResultModel> results;

        /**
         * Constructor
         * @param query Query of the results
         * @param totalCount Total number of the results considered
         * @param totalSentimentScore Total sentiment score of results considered
         * @param totalReadingScore Total reading score of results considered
         * @param totalReadingGrade Total reading grade of results considered
         * @param results Results
         *
         * @author Wayan-Gwie Lapointe
         */
        public MultipleSearchResult(String query, int totalCount, double totalSentimentScore, double totalReadingScore, double totalReadingGrade, List<SearchResultModel> results) {
            this.query = query;
            this.totalCount = totalCount;
            this.totalSentimentScore = totalSentimentScore;
            this.totalReadingScore = totalReadingScore;
            this.totalReadingGrade = totalReadingGrade;
            this.results = results;
        }

        /**
         * ToString
         * @return String representation
         *
         * @author Wayan-Gwie Lapointe
         */
        @Override
        public String toString() {
            return "MultipleSearchResult{code=" + code + ", query=" + query +", results count=" + results.size() + '}';
        }
    }

    /**
     * SingleSearchResult message to indicate a single new result for a query
     *
     * @author Wayan-Gwie Lapointe
     */
    public static final class SingleSearchResult implements SearchResult {
        public final String code = "SingleResult";
        public final String query;
        public final SearchResultModel result;

        /**
         * Constructor
         * @param query Query of the result
         * @param result Result
         *
         * @author Wayan-Gwie Lapointe
         */
        public SingleSearchResult(String query, SearchResultModel result) {
            this.query = query;
            this.result = result;
        }

        /**
         * ToString
         * @return String representation
         *
         * @author Wayan-Gwie Lapointe
         */
        @Override
        public String toString() {
            return "SingleSearchResult{code=" + code + ", query=" + query + '}';
        }
    }
}
