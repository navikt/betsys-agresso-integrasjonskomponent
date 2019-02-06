package no.nav.oko.betsys.agresso.integrasjonskomponent;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


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

    @Value("${vaultPath}")
    private String vaultPath;

    public  BetsysTilAgressoRoute(){
    }


    @Override
    public void configure() {
        final String betsysSftpOptions =
                "?throwExceptionOnConnectFailed=true" +
                "&knownHostsFile=" + vaultPath + "/known_hosts" +
                "&privateKeyFile=" + vaultPath + "/betsysKey" +
                "&privateKeyPassphrase=betsysTest";

        final String agressoSftpOptions =
                "?knownHostsFile=" + vaultPath + "/known_hosts" +
                "&privateKeyFile=" + vaultPath + "/betsysKey" +
                "&privateKeyPassphrase=betsysTest";

        String agressoInbound = getInboundAgressoSftpPath(agressoSftpUrl, agressoSftpUsername, agressoSftpPassword);
        String betsysSftpPath = getBetsysSftpPath(betsysSftpUrl, betsysSftpUsername, betsysSftpPassword);

        LOGGER.info("Setter opp BetsysTilAgresso Camel-route");

        from("ref:betsysInn")
                .routeId("KopierFilFraBetsys")
                .to("micrometer:timer:betsys.to.agresso.timer?action=start")
                .split(xpath("//n:DocumentIdentification/n:InstanceIdentifier/text()")
                        .namespace("n", "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader"))
                .log("Forsøker å lese fil med navn: ${body} fra Betsys til Agresso")
                .pollEnrich().simple(betsysSftpPath + betsysSftpOptions + "&fileName=${body}" + XML_SUFFIX).timeout(POLL_TIMEOUT).id("betsysServer")
                .to(agressoInbound + agressoSftpOptions).id("agressoServer")
                .log("Fil med navn:  ${header.CamelFileNameOnly} ferdig kopiert fra Betsys til Agresso")
                .to("micrometer:timer:betsys.to.agresso.timer?action=stop")
                .to("micrometer:counter:betsys.to.agresso.total.counter")
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
                "/inbound";
    }

}
