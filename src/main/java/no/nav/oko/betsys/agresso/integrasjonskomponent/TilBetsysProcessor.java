package no.nav.oko.betsys.agresso.integrasjonskomponent;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TilBetsysProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TilBetsysProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        LOGGER.info("Flytter fil til NFS og sender SBDH påkø");
        LOGGER.info(exchange.getIn().getBody().toString());

        // Legg fil på NFS share til betsys
        // Generer SBDH
        // Legg på kø til betsys
    }
}
