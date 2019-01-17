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

    @Value("${PORT}")
    private String port;

    @Value("${vaultPath}")
    private String vaultPath;

    private MeterRegistry registry;

    public AgressoTilBetsysRoute(MeterRegistry registry){
        this.registry = registry;
    }

    @Override
    public void configure() {
        final String SFTP_OPTIONS =
                "&initialDelay=15000" +
                "&maxMessagesPerPoll=1&delay=15000" +
                "&move=Arkiv" +
                "&readLock=changed" +
                "&bridgeErrorHandler=true" +
                "&throwExceptionOnConnectFailed=true" +
                "&knownHostsFile=" + vaultPath + "/known_hosts" +
                //"&useUserKnownHostsFile=false" +
                "&privateKeyFile=" + vaultPath + "/betsysKey" +
                "&privateKeyPassphrase=betsysTest";

        String agressoOutbound = getOutboundAgressoSftpPath(agressoSftpUrl, agressoSftpUsername, agressoSftpPassword);
        String betsysSftpPath = getBetsysSftpPath(betsysSftpUrl, betsysSftpUsername);

        LOGGER.info("Setter opp AgressoTilBetys Camel-route");

        errorHandler(defaultErrorHandler()
                .onExceptionOccurred(exchange -> {
                    Throwable exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
                    registry.counter("agresso_to_betsys_exception_counter", "Exception" , exception.getClass().getSimpleName() ).increment();
                        })
        );
        from(agressoOutbound +  "&useUserKnownHostsFile=false" )
                .routeId("KopierFilFraAgresso")
                .log("Lest fil med navn: ${header.CamelFileNameOnly} fra Agresso")
                .to("micrometer:counter:agresso.to.betsys.total.counter")
                .to("micrometer:timer:agresso.to.betsys.timer?action=start")
                .to("validator:file:pain.001.001.03.xsd")
                .setHeader(Exchange.FILE_NAME, header(Exchange.FILE_NAME).regexReplaceAll("(.*)\\.lis$", "$1.xml").getExpression())
                .to(betsysSftpPath + SFTP_OPTIONS)
                .process(exchange -> {
                  String filename = exchange.getIn().getHeader("CamelFileNameOnly", String.class).replace(".lis", "");
                  exchange.getOut().setBody(
                            SbdhService.opprettStringSBDH(SbdhType.PAIN001,filename,"974232952", "984851006"));
                  exchange.getOut().setHeader("CamelFileNameOnly", filename);
                    }
                )
                .to("ref:betsysUt").id("betsysJMS")
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

    private String getBetsysSftpPath(String url, String username) {
        return "sftp://" +
                username +
                "@" +
                url +
                ":" + port + "/outbound";
    }
}
