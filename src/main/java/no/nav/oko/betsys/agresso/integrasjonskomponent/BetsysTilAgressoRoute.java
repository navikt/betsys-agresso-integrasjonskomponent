package no.nav.oko.betsys.agresso.integrasjonskomponent;

import org.apache.camel.spring.SpringRouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class BetsysTilAgressoRoute extends SpringRouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(BetsysTilAgressoRoute.class);

    @Value("${agressoSftpUser}")
    private String agressoSftpUser;

    @Value("${agressoKeyPassphrase}")
    private String agressoKeyPassphrase;

    @Value("${agressoSftpPath}")
    private String agressoSftpPath;

    @Value("${betsysSftpPath}")
    private String betsysSftpPath;

    @Value("${betsysSftpUser}")
    private String betsysSftpUser;

    @Value("${betsysKeyPassphrase}")
    private String betsysKeyPassphrase;

    @Value("${vaultPath}")
    private String vaultPath;

    @Override
    public void configure() {
        final String betsysSftpOptions =
                "?throwExceptionOnConnectFailed=true" +
                "&strictHostKeyChecking=yes" +
                "&knownHostsFile=" + vaultPath + "/known_hosts" +
                "&privateKeyFile=" + vaultPath + "/betsysKey" +
                "&privateKeyPassphrase=" + betsysKeyPassphrase;

        final String agressoSftpOptions =
                "?knownHostsFile=" + vaultPath + "/known_hosts" +
                "&strictHostKeyChecking=yes" +
                "&privateKeyFile=" + vaultPath + "/betsysKey" +
                "&privateKeyPassphrase=" + agressoKeyPassphrase;

        final String betsysInbound = "sftp://" + betsysSftpUser + "@" + betsysSftpPath + "/inbound";
        final String agressoInbound ="sftp://" + agressoSftpUser + "@" + agressoSftpPath + "/inbound";

        LOGGER.info("Setter opp BetsysTilAgresso Camel-route");

        from("ref:betsysInn")
                .routeId("KopierFilFraBetsys")
                .to("micrometer:timer:betsys.to.agresso.timer?action=start")
                .split(xpath("//n:DocumentIdentification/n:InstanceIdentifier/text()")
                        .namespace("n", "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader"))
                .log("Forsøker å lese fil med navn: ${body} fra Betsys til Agresso")
                .pollEnrich().simple(betsysInbound + betsysSftpOptions + "&fileName=${body}" + ".xml").timeout(10000).id("fromBetsysServer")
                .to(agressoInbound + agressoSftpOptions).id("toAgressoServer")
                .log("Fil med navn:  ${header.CamelFileNameOnly} ferdig kopiert fra Betsys til Agresso")
                .to("micrometer:timer:betsys.to.agresso.timer?action=stop")
                .to("micrometer:counter:betsys.to.agresso.total.counter")
                .end();
    }

}
