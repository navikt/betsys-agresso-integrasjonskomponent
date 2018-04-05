package no.nav.oko.betsys.agresso.integrasjonskomponent.config;

import no.nav.oko.betsys.agresso.integrasjonskomponent.LesFilFraAgressoRoute;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamelConfig {

    private static final Logger log = LoggerFactory.getLogger(CamelConfig.class);

    public void setUpCamelContext() {
        CamelContext camelContext = new DefaultCamelContext();

        try {
            camelContext.addRoutes(new LesFilFraAgressoRoute());
            camelContext.start();
        } catch (Exception e) {
            log.error("Unable to start camelcontext", e);
        }
    }

}
