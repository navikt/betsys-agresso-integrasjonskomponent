package no.nav.oko.betsys.agresso.integrasjonskomponent;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TilBetsysProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TilBetsysProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        LOGGER.info("Flytter fil til NFS og sender SBDH påkø");

    }
}
