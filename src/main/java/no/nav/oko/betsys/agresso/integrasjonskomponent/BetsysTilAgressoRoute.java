package no.nav.oko.betsys.agresso.integrasjonskomponent;

import no.nav.oko.betsys.agresso.integrasjonskomponent.config.CamelSftpConfig;
import org.apache.camel.spring.SpringRouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class BetsysTilAgressoRoute extends SpringRouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(BetsysTilAgressoRoute.class);
    private static final String MICROMETER_TIMER = "micrometer:timer:betsys.to.agresso.timer";

    private CamelSftpConfig sftpConfig;

    public BetsysTilAgressoRoute(CamelSftpConfig sftpConfig){
        this.sftpConfig = sftpConfig;
    }

    @Override
    public void configure() {
        LOGGER.info("Setter opp BetsysTilAgresso Camel-route");

        from("ref:betsysInn")
                .routeId("KopierFilFraBetsys")
                .to( MICROMETER_TIMER + "?action=start")
                .split(xpath("//n:DocumentIdentification/n:InstanceIdentifier/text()")
                        .namespace("n", "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader"))
                .log("Forsøker å lese fil med navn: ${body} fra Betsys til Agresso")
                .pollEnrich().simple(sftpConfig.betsysTilAgressoBetsysSftp() + "&fileName=${body}" + ".xml").timeout(10000).id("fromBetsysServer")
                .to(sftpConfig.betsysTilAgressoAgressoSftp()).id("toAgressoServer")
                .log("Fil med navn:  ${header.CamelFileNameOnly} ferdig kopiert fra Betsys til Agresso")
                .to(MICROMETER_TIMER +"?action=stop")
                .to("micrometer:counter:betsys.to.agresso.total.counter")
                .end();
    }

}
