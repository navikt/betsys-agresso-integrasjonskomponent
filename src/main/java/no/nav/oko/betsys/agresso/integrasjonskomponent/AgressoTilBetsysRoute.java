package no.nav.oko.betsys.agresso.integrasjonskomponent;


import io.micrometer.core.instrument.MeterRegistry;
import no.nav.generer.sbdh.SbdhService;
import no.nav.generer.sbdh.generer.SbdhType;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class AgressoTilBetsysRoute extends RouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgressoTilBetsysRoute.class);

    private static final String SFTP_OPTIONS = "&useUserKnownHostsFile=false&initialDelay=15000&maxMessagesPerPoll=1&delay=15000&move=Arkiv&readLock=changed";

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

    @Value("${PORT}")
    private String port;

    private MeterRegistry registry;

    public AgressoTilBetsysRoute(MeterRegistry registry){
        this.registry = registry;
    }

    @Override
    public void configure() {
        String agressoOutbound = getOutboundAgressoSftpPath(agressoSftpUrl, agressoSftpUsername, agressoSftpPassword);
        String betsysSftpPath = getBetsysSftpPath(betsysSftpUrl, betsysSftpUsername, betsysSftpPassword);

        LOGGER.info("Setter opp AgressoTilBetys Camel-route");

        errorHandler(defaultErrorHandler()
                .onExceptionOccurred(exchange -> {
                    Throwable exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
                    registry.counter("agresso_to_betsys_exception_counter", "Exception" , exception.getClass().getSimpleName() ).increment();
                        })
        );
        from(agressoOutbound + SFTP_OPTIONS)
                .routeId("KopierFilFraAgresso")
                .log("Lest fil med navn: ${header.CamelFileNameOnly} fra Agresso")
                .to("micrometer:counter:agresso.to.betsys.total.counter")
                .to("micrometer:timer:agresso.to.betsys.timer?action=start")
                .to("validator:file:pain.001.001.03.xsd")
                .to(betsysSftpPath)
                .process(exchange -> {
                  String filename = exchange.getIn().getHeader("CamelFileNameOnly", String.class).replace(".xml", "");
                  exchange.getOut().setBody(
                            SbdhService.opprettStringSBDH(SbdhType.PAIN001,filename,"974232952", "984851006"));
                  exchange.getOut().setHeader("CamelFileNameOnly", filename);
                    }
                )
                .to("ref:betsysUt")
                .log("Fil med navn: ${header.CamelFileNameOnly} ferdig kopiert fra Agresso til Betsys")
                .to("micrometer:timer:agresso.to.betsys.timer?action=stop")
                .end();
    }


    private String getOutboundAgressoSftpPath(String url, String username, String password) {
        return "sftp://" +
                username +
                "@" +
                url +
                ":" + port + "/outbound" +
                "?password=" +
                password;
    }

    private String getBetsysSftpPath(String url, String username, String password) {
        return "sftp://" +
                username +
                "@" +
                url +
                ":" + port + "/outbound" +
                "?password=" +
                password +
                "&useUserKnownHostsFile=false";
    }
}
