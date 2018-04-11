package no.nav.oko.betsys.agresso.integrasjonskomponent;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Processor;
import org.apache.camel.ValidationException;
import org.apache.camel.component.jms.JmsEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TilBetsysProcessor{

    private static final Logger LOGGER = LoggerFactory.getLogger(TilBetsysProcessor.class);


//    @Override
//    public void process(Exchange exchange) throws Exception {
//
//        LOGGER.info("Er i tilBetsysProcessor og skal kaste exception");
//        //LOGGER.info(exchange.getIn().getBody().toString());
//        LOGGER.info("${header.CamelFileNameOnly}");
//        LOGGER.info((String) exchange.getIn().getHeader("CamelFileNameOnly"));
//
//        throw new ValidationException(exchange, "Test");
//
//
//
//
//        // Legg fil på NFS share til betsys
//        // Generer SBDH
//        // Legg på kø til betsys
//    }

    @Handler
    public String sendSbdh(Exchange exchange) {
        String filnavn = (String) exchange.getIn().getHeader("CamelFileNameOnly");

        filnavn.replace(".ils", "");
    return filnavn;
    }
}
