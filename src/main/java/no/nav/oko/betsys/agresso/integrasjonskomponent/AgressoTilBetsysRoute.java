package no.nav.oko.betsys.agresso.integrasjonskomponent;

import no.nav.generer.sbdh.SbdhService;
import no.nav.generer.sbdh.generer.SbdhType;
import no.nav.oko.betsys.agresso.integrasjonskomponent.config.CamelSftpConfig;
import org.apache.camel.Exchange;
import org.apache.camel.spring.SpringRouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AgressoTilBetsysRoute extends SpringRouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgressoTilBetsysRoute.class);
    private static final String MICROMETER_TIMER = "micrometer:timer:agresso.to.betsys.timer";

    @Value("${bankSender}")
    private String sender;

    @Value("${bankReceiver}")
    private String receiver;

    private CamelSftpConfig sftpConfig;

    public AgressoTilBetsysRoute(CamelSftpConfig sftpConfig){
        this.sftpConfig = sftpConfig;
    }

    @Override
    public void configure() {
        LOGGER.info("Setter opp AgressoTilBetys Camel-route");
        from(sftpConfig.agressoTilBetsysAgressoSftp())
                .routeId("KopierFilFraAgresso")
                .log("Lest fil med navn: ${header.CamelFileNameOnly} fra Agresso")
                .to(MICROMETER_TIMER + "?action=start")
                .to("validator:file:pain.001.001.03.xsd")
                .setHeader(Exchange.FILE_NAME, header(Exchange.FILE_NAME).regexReplaceAll("(.*)\\.lis$", "$1.xml").getExpression())
                .to(sftpConfig.agressoTilBetsysBetsysSftp()).id("toBetsysServer")
                .process(exchange -> {
                  String filename = exchange.getIn().getHeader("CamelFileNameOnly", String.class).replace(".lis", "");
                  exchange.getOut().setBody(
                            SbdhService.opprettStringSBDH(SbdhType.PAIN001,filename,sender, receiver));
                  exchange.getOut().setHeader("CamelFileNameOnly", filename);
                    }
                )
                .to("ref:betsysUt").id("betsysJMS")
                .log("Fil med navn: ${header.CamelFileNameOnly} ferdig kopiert fra Agresso til Betsys")
                .to(MICROMETER_TIMER + "?action=stop")
                .to("micrometer:counter:agresso.to.betsys.total.counter")
                .end();
    }

}
