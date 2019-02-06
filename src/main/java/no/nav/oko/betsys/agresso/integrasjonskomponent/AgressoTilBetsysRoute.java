package no.nav.oko.betsys.agresso.integrasjonskomponent;

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

    @Value("${BETSYSSERVERURL}")
    private String betsysSftpUrl;

    @Value("${BETSYSUSERNAME}")
    private String betsysSftpUsername;

    @Value("${vaultPath}")
    private String vaultPath;

    public AgressoTilBetsysRoute(){
    }

    @Override
    public void configure() {
        final String agressoSftpOptions =
                "?initialDelay=15000" +
                "&maxMessagesPerPoll=1&delay=15000" +
                "&move=Arkiv" +
                "&readLock=changed" +
                "&bridgeErrorHandler=true" +
                "&knownHostsFile=" + vaultPath + "/known_hosts" +
                "&privateKeyFile=" + vaultPath + "/betsysKey" +
                "&privateKeyPassphrase=betsysTest";


        final String betsysSftpOptions =
                "?throwExceptionOnConnectFailed=true" +
                "&knownHostsFile=" + vaultPath + "/known_hosts" +
                "&privateKeyFile=" + vaultPath + "/betsysKey" +
                "&privateKeyPassphrase=betsysTest";

        String agressoOutbound = getOutboundAgressoSftpPath(agressoSftpUrl, agressoSftpUsername, agressoSftpPassword);
        String betsysSftpPath = getBetsysSftpPath(betsysSftpUrl, betsysSftpUsername);

        LOGGER.info("Setter opp AgressoTilBetys Camel-route");

        from(agressoOutbound +  agressoSftpOptions )
                .routeId("KopierFilFraAgresso")
                .log("Lest fil med navn: ${header.CamelFileNameOnly} fra Agresso")
                .to("micrometer:timer:agresso.to.betsys.timer?action=start")
                .to("validator:file:pain.001.001.03.xsd")
                .setHeader(Exchange.FILE_NAME, header(Exchange.FILE_NAME).regexReplaceAll("(.*)\\.lis$", "$1.xml").getExpression())
                .to(betsysSftpPath + betsysSftpOptions).id("toBetsysServer")
                .process(exchange -> {
                  String filename = exchange.getIn().getHeader("CamelFileNameOnly", String.class).replace(".lis", "");
                  exchange.getOut().setBody(
                            SbdhService.opprettStringSBDH(SbdhType.PAIN001,filename,"10263448004", "920058817"));
                  exchange.getOut().setHeader("CamelFileNameOnly", filename);
                    }
                )
                .to("ref:betsysUt").id("betsysJMS")
                .log("Fil med navn: ${header.CamelFileNameOnly} ferdig kopiert fra Agresso til Betsys")
                .to("micrometer:timer:agresso.to.betsys.timer?action=stop")
                .to("micrometer:counter:agresso.to.betsys.total.counter")
                .end();
    }


    private String getOutboundAgressoSftpPath(String url, String username, String password) {
        return "sftp://" +
                username +
                "@" +
                url +
                "/outbound";
    }

    private String getBetsysSftpPath(String url, String username) {
        return "sftp://" +
                "betsys" +
                "@" +
                url +
                "/outbound";
    }
}
