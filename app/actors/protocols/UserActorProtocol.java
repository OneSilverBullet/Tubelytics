package actors.protocols;

/**
 * Protocol for UserActor messages
 *
 * @author Wayan-Gwie Lapointe
 */
public class UserActorProtocol {
    /**
     * Unused constructor
     *
     * @author Wayan-Gwie Lapointe
     */
    private UserActorProtocol() {
    }

    /**
     * Client request message, only obtained from the WebSocket in JSON
     *
     * @author Wayan-Gwie Lapointe
     */
    public static class ClientRequest {
        public String code;
        public String query;

        public ClientRequest(String code, String query) {
            this.code = code;
            this.query = query;
        }
    }
}
