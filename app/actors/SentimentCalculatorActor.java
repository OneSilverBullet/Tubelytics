package actors;

import actors.protocols.SentimentCalculatorProtocol;
import models.SentimentCalculator;
import models.WordAnalyser;
import org.apache.pekko.actor.AbstractActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Actor to handle sentiment calculations
 *
 * @author Jananee Aruboribaran
 */
public class SentimentCalculatorActor extends AbstractActor {
    private static final Logger log = LoggerFactory.getLogger(SentimentCalculatorActor.class);

    /**
     * Define behaviour of the actor
     * @return behaviour
     *
     * @author Jananee Aruboribaran
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(SentimentCalculatorProtocol.AddSentimentScore.class, this::addSentimentScore)
            .build();
    }

    /**
     * Handler for adding sentiment score
     * @param stats Message to handle
     *
     * @author Jananee Aruboribaran
     */
    private void addSentimentScore(SentimentCalculatorProtocol.AddSentimentScore stats) {
        sender().tell(
            stats.getResults().peek(y -> {
                SentimentCalculator cal = new SentimentCalculator(WordAnalyser.getInstance());
                y.setSentimentScore(cal.sentimentScore(y.getDescription()));
            }),
            self()
        );
        log.info("Adding sentiment score to results");
    }
}
