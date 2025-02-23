package actors;

import actors.protocols.ReadingCalculatorProtocol;
import models.ReadingCalculator;
import org.apache.pekko.actor.AbstractActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Actor to handle reading calculation
 *
 * @author Wayan-Gwie Lapointe
 */
public class ReadingCalculatorActor extends AbstractActor {
    private static final Logger log = LoggerFactory.getLogger(ReadingCalculatorActor.class);

    /**
     * Define behaviour of the actor
     * @return behaviour
     *
     * @author Wayan-Gwie Lapointe
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(ReadingCalculatorProtocol.AddReadingStats.class, this::addReadingStats)
            .build();
    }

    /**
     * Handler for adding reading stats
     * @param stats Message to handle
     *
     * @author Wayan-Gwie Lapointe
     */
    private void addReadingStats(ReadingCalculatorProtocol.AddReadingStats stats) {
        sender().tell(
            stats.getResults().peek(x -> {
                ReadingCalculator calculator = new ReadingCalculator(x.getDescription());
                x.setReadingScore(calculator.getReadingScore());
                x.setGradeLevel(calculator.getGradeLevel());
            }),
            self()
        );
        log.info("Adding reading stats to results");
    }
}
