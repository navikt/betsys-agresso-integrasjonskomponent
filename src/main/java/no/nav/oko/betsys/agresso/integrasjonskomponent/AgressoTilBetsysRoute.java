package no.nav.oko.betsys.agresso.integrasjonskomponent;


import no.nav.generer.sbdh.SbdhService;
import no.nav.generer.sbdh.generer.SbdhType;
import org.apache.camel.Exchange;
import org.apache.camel.spring.SpringRouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static no.nav.oko.betsys.agresso.integrasjonskomponent.config.PrometheusLabels.LABEL_TECHNICAL_EXCEPTION;
import static no.nav.oko.betsys.agresso.integrasjonskomponent.config.PrometheusLabels.PROCESS_AGRESSO;
import static no.nav.oko.betsys.agresso.integrasjonskomponent.config.PrometheusMetrics.agressoCounter;
import static no.nav.oko.betsys.agresso.integrasjonskomponent.config.PrometheusMetrics.exceptionCounter;

@Service
public class AgressoTilBetsysRoute extends SpringRouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgressoTilBetsysRoute.class);

    private static final String SFTP_OPTIONS = "&useUserKnownHostsFile=false&initialDelay=15000&maxMessagesPerPoll=1&delay=15000&move=Arkiv";

    @Value("${SFTPUSERNAME}")
    private String agressoSftpUsername;

    @Value("${SFTPPASSWORD}")
    private String agressoSftpPassword;

    @Value("${SFTPSERVERURL}")
    private String agressoSftpUrl;

    @Value("${BETSYS_SFTP_SERVER_URL}")
    private String betsysSftpUrl;

    @Value("${BETSYSUSERNAME}")
    private String betsysSftpUsername;

    @Value("${BETSYSPASSWORD}")
    private String betsysSftpPassword;

    @Override
    public void configure() {
        String agressoOutbound = getOutboundAgressoSftpPath(agressoSftpUrl, agressoSftpUsername, agressoSftpPassword);
        String betsysSftpPath = getBetsysSftpPath(betsysSftpUrl, betsysSftpUsername, betsysSftpPassword);

        LOGGER.info("Setter opp AgressoTilBetys Camel-route");

        errorHandler(defaultErrorHandler()
                .onExceptionOccurred(exchange -> {
                    Throwable exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
                    exceptionCounter.labels(PROCESS_AGRESSO, LABEL_TECHNICAL_EXCEPTION, exception.getClass().getSimpleName()).inc();
                        })
        );

        from(agressoOutbound + SFTP_OPTIONS)
                .routeId("KopierFilFraAgresso")
                .log("Lest fil med navn: ${header.CamelFileNameOnly}")
                .to("validator:file:pain.001.001.03.xsd")
                .to(betsysSftpPath)
                .process(exchange -> {
                  String filename = exchange.getIn().getHeader("CamelFileNameOnly", String.class).replace(".lis", "");
                  exchange.getOut().setBody(
                            SbdhService.opprettStringSBDH(SbdhType.PAIN001,filename,"test", "test"));
                    }
                )
                .to("ref:betsysUt")
                .process(exchange -> agressoCounter.labels(PROCESS_AGRESSO, "Fil kopiert til Betsys").inc())
                .end();
    }


    private String getOutboundAgressoSftpPath(String url, String username, String password) {
        return "sftp://" +
                username +
                "@" +
                url +
                "/outbound" +
                "?password=" +
                password;
    }

    private String getBetsysSftpPath(String url, String username, String password) {
        return "sftp://" +
                username +
                "@" +
                url +
                "/srv/nais_apps/q0/naisnfs/out" +
                "?password=" +
                password +
                "&useUserKnownHostsFile=false";
    }
}
