package no.nav.oko.betsys.agresso.integrasjonskomponent;

import org.apache.camel.Exchange;
import org.apache.camel.spring.SpringRouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static no.nav.oko.betsys.agresso.integrasjonskomponent.config.PrometheusLabels.LABEL_TECHNICAL_EXCEPTION;
import static no.nav.oko.betsys.agresso.integrasjonskomponent.config.PrometheusLabels.PROCESS_BETSYS;
import static no.nav.oko.betsys.agresso.integrasjonskomponent.config.PrometheusMetrics.betsysCounter;
import static no.nav.oko.betsys.agresso.integrasjonskomponent.config.PrometheusMetrics.exceptionCounter;

@Service
public class BetsysTilAgressoRoute extends SpringRouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(BetsysTilAgressoRoute.class);

    private static final String XML_SUFFIX = ".xml";
    private static final long POLL_TIMEOUT = 10000;

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
        String agressoInbound = getInboundAgressoSftpPath(agressoSftpUrl, agressoSftpUsername, agressoSftpPassword);
        String betsysSftpPath = getBetsysSftpPath(betsysSftpUrl, betsysSftpUsername, betsysSftpPassword);

        LOGGER.info("Setter opp BetsysTilAgresso Camel-route");

        errorHandler(defaultErrorHandler()
                .onExceptionOccurred(exchange -> {
                    Throwable exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
                    exceptionCounter.labels(PROCESS_BETSYS, LABEL_TECHNICAL_EXCEPTION, exception.getClass().getSimpleName()).inc();
                })
        );

        from("ref:betsysInn")
                .routeId("KopierFilFraBetsys")
                .split(xpath("//n:DocumentIdentification/n:InstanceIdentifier/text()")
                        .namespace("n", "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader"))
                .log("Henter fil fra Betsys med navn: ${body}")
                .pollEnrich().simple(betsysSftpPath + "&fileName=${body}" + XML_SUFFIX).timeout(POLL_TIMEOUT)
                .to(agressoInbound)
                .process(exchange -> betsysCounter.labels(PROCESS_BETSYS, "Fil kopiert til Agresso"))
                .end();
    }

    private String getBetsysSftpPath(String url, String username, String password) {
        return "sftp://" +
                username +
                "@" +
                url +
                ":2222/srv/nais_apps/q0/naisnfs/out" +
                "?password=" +
                password +
                "&useUserKnownHostsFile=false";
    }

    private String getInboundAgressoSftpPath(String url, String username, String password) {
        return "sftp://" +
                username +
                "@" +
                url +
                ":2222/inbound" +
                "?password=" +
                password +
                "&useUserKnownHostsFile=false";
    }

}
