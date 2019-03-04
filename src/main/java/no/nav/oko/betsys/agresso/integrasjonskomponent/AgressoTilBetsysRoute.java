package no.nav.oko.betsys.agresso.integrasjonskomponent;

import no.nav.generer.sbdh.SbdhService;
import no.nav.generer.sbdh.generer.SbdhType;
import org.apache.camel.Exchange;
import org.apache.camel.spring.SpringRouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AgressoTilBetsysRoute extends SpringRouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgressoTilBetsysRoute.class);

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

    @Value("${bankSender}")
    private String sender;

    @Value("${bankReceiver}")
    private String receiver;

    @Override
    public void configure() {
        final String agressoSftpOptions =
                "?initialDelay=15000" +
                "&maxMessagesPerPoll=1" +
                "&delay=15000" +
                "&move=Arkiv" +
                "&readLock=changed" +
                "&bridgeErrorHandler=true" +
                "&strictHostKeyChecking=yes" +
                        "&stepwise=false" +
                "&knownHostsFile=" + vaultPath + "/known_hosts" +
                "&privateKeyFile=" + vaultPath + "/betsysKey" +
                "&privateKeyPassphrase=" + betsysKeyPassphrase;

        final String betsysSftpOptions =
                "?throwExceptionOnConnectFailed=true" +
                        "&stepwise=false" +
                "&strictHostKeyChecking=yes" +
                "&knownHostsFile=" + vaultPath + "/known_hosts" +
                "&privateKeyFile=" + vaultPath + "/betsysKey" +
                "&privateKeyPassphrase=" + agressoKeyPassphrase;

        final String agressoOutbound = "sftp://" + agressoSftpUser + "@" + agressoSftpPath + "/outbound";
        final String betsysOutbound = "sftp://" + betsysSftpUser + "@" + betsysSftpPath + "/outbound";

        LOGGER.info("Setter opp AgressoTilBetys Camel-route");

        from(agressoOutbound +  agressoSftpOptions )
                .routeId("KopierFilFraAgresso")
                .log("Lest fil med navn: ${header.CamelFileNameOnly} fra Agresso")
                .to("micrometer:timer:agresso.to.betsys.timer?action=start")
                .to("validator:file:pain.001.001.03.xsd")
                .setHeader(Exchange.FILE_NAME, header(Exchange.FILE_NAME).regexReplaceAll("(.*)\\.lis$", "$1.xml").getExpression())
                .to(betsysOutbound + betsysSftpOptions).id("toBetsysServer")
                .process(exchange -> {
                  String filename = exchange.getIn().getHeader("CamelFileNameOnly", String.class).replace(".lis", "");
                  exchange.getOut().setBody(
                            SbdhService.opprettStringSBDH(SbdhType.PAIN001,filename,sender, receiver));
                  exchange.getOut().setHeader("CamelFileNameOnly", filename);
                    }
                )
                .to("ref:betsysUt").id("betsysJMS")
                .log("Fil med navn: ${header.CamelFileNameOnly} ferdig kopiert fra Agresso til Betsys")
                .to("micrometer:timer:agresso.to.betsys.timer?action=stop")
                .to("micrometer:counter:agresso.to.betsys.total.counter")
                .end();
    }

}
