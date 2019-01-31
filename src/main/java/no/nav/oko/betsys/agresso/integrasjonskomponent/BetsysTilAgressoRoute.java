package no.nav.oko.betsys.agresso.integrasjonskomponent;

import io.micrometer.core.instrument.MeterRegistry;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class BetsysTilAgressoRoute extends RouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(BetsysTilAgressoRoute.class);

    private static final String XML_SUFFIX = ".xml";
    private static final long POLL_TIMEOUT = 10000;

    @Value("${SFTPUSERNAME}")
    private String agressoSftpUsername;

    @Value("${SFTPPASSWORD}")
    private String agressoSftpPassword;

    @Value("${SFTPSERVERURL}")
    private String agressoSftpUrl;

    @Value("${BETSYSSERVERURL}")
    private String betsysSftpUrl;

    @Value("${BETSYSUSERNAME}")
    private String betsysSftpUsername;

    @Value("${BETSYSPASSWORD}")
    private String betsysSftpPassword;

    @Value("${PORT}")
    private String port;

    @Value("${vaultPath}")
    private String vaultPath;

    private MeterRegistry registry;

    public  BetsysTilAgressoRoute(MeterRegistry registry){
        this.registry = registry;
    }


    @Override
    public void configure() {
        final String betsysSftpOptions =
                "?throwExceptionOnConnectFailed=true" +
                "&knownHostsFile=" + vaultPath + "/known_hosts" +
                //"&useUserKnownHostsFile=false" +
                "&privateKeyFile=" + vaultPath + "/betsysKey" +
                "&privateKeyPassphrase=betsysTest";

        String agressoInbound = getInboundAgressoSftpPath(agressoSftpUrl, agressoSftpUsername, agressoSftpPassword);
        String betsysSftpPath = getBetsysSftpPath(betsysSftpUrl, betsysSftpUsername, betsysSftpPassword);

        LOGGER.info("Setter opp BetsysTilAgresso Camel-route");

        errorHandler(defaultErrorHandler()
                .onExceptionOccurred(exchange -> {
                    Throwable exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
                    registry.counter("betsys_to_agresso_exception_counter", "Exception" , exception.getClass().getSimpleName() ).increment();
                })
        );

        from("ref:betsysInn")
                .routeId("KopierFilFraBetsys")
                .to("micrometer:counter:betsys.to.agresso.total.counter")
                .to("micrometer:timer:betsys.to.agresso.timer?action=start")
                .split(xpath("//n:DocumentIdentification/n:InstanceIdentifier/text()")
                        .namespace("n", "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader"))
                .log("Forsøker å lese fil med navn: ${body} fra Betsys til Agresso")
                .pollEnrich().simple(betsysSftpPath + betsysSftpOptions + "&fileName=${body}" + XML_SUFFIX).timeout(POLL_TIMEOUT)
                .to(agressoInbound + "&useUserKnownHostsFile=false")
                .log("Fil med navn:  ${header.CamelFileNameOnly} ferdig kopiert fra Betsys til Agresso")
                .to("micrometer:timer:betsys.to.agresso.timer?action=stop")
                .process(exchange ->  registry.gauge("betsys_to_agresso_last_file_transfer", new AtomicLong()).set(System.currentTimeMillis()))
                .end();
    }

    private String getBetsysSftpPath(String url, String username, String password) {
        return "sftp://" +
                username +
                "@" +
                url +
                "/inbound";
    }

    private String getInboundAgressoSftpPath(String url, String username, String password) {
        return "sftp://" +
                username +
                "@" +
                url +
                "/inbound" +
                "?password=" +
                password;
    }

}
