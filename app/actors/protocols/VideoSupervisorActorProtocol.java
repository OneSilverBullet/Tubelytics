package actors.protocols;

/**
 * Protocol of VideoSupervisorActor messages
 *
 * @author Wayan-Gwie Lapointe
 */
public class VideoSupervisorActorProtocol {
    /**
     * Unused constructor
     *
     * @author Wayan-Gwie Lapointe
     */
    private VideoSupervisorActorProtocol() {
    }

    /**
     * Start search message to start a search for a query
     *
     * @author Wayan-Gwie Lapointe
     */
    public static class StartSearch {
        private final String query;

        /**
         * Constructor
         * @param query Query to search
         *
         * @author Wayan-Gwie Lapointe
         */
        public StartSearch(String query) {
            this.query = query;
        }

        /**
         * Get query
         * @return Query
         *
         * @author Wayan-Gwie Lapointe
         */
        public String getQuery() {
            return query;
        }
    }

    /**
     * End search message to end a search for a query
     *
     * @author Wayan-Gwie Lapointe
     */
    public static class EndSearch {
        private final String query;

        /**
         * Constructor
         * @param query Query to stop searching
         *
         * @author Wayan-Gwie Lapointe
         */
        public EndSearch(String query) {
            this.query = query;
        }

        /**
         * Get query
         * @return Query
         *
         * @author Wayan-Gwie Lapointe
         */
        public String getQuery() {
            return query;
        }
    }
}
