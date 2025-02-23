package actors.protocols;

import junit.framework.TestCase;
import static org.mockito.Mockito.mock;

/**
 * testing UserActorProtocol
 *
 * @author Jananee Aruboribaran
 */
public class UserActorProtocolTest extends TestCase {

    private UserActorProtocol.ClientRequest request;

    /**
     * initializing all the necessary variables and mocking the UserActorProtocol.ClientRequest
     *
     * @author Jananee Aruboribaran
     */
    @Override
    public void setUp() {
        request = mock(UserActorProtocol.ClientRequest.class);

        request.code = "12345";
        request.query = "SELECT * FROM users";
    }

    /**
     * verifying if the variables of UserActorProtocol.ClientRequest is correct
     *
     * @author Jananee Aruboribaran
     */
    public void testVariablesCorrect() {
        assertEquals("12345", request.code);
        assertEquals("SELECT * FROM users", request.query);
    }

    /**
     * check if the instance is initiated
     *
     * @author Jananee Aruboribaran
     */
    public void testInstance() {
        UserActorProtocol.ClientRequest test = new UserActorProtocol.ClientRequest("12345", "start");
        assertNotNull(test);
    }

    /**
     * verifying if the variables of UserActorProtocol.ClientRequest is null if is not initialized
     *
     * @author Jananee Aruboribaran
     */
    public void testVariablesNull() {
        UserActorProtocol.ClientRequest nullResult = mock(UserActorProtocol.ClientRequest.class);

        assertNull(nullResult.code);
        assertNull(nullResult.query);
    }

    /**
     * verify that Tick is initialized
     *
     * @author Jananee Aruboribaran
     */
    public void testClientRequest() {
        assertNotNull(request);
    }


}