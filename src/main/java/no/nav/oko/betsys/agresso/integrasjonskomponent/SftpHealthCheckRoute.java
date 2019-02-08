package no.nav.oko.betsys.agresso.integrasjonskomponent;

import io.micrometer.core.instrument.MeterRegistry;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class SftpHealthCheckRoute extends RouteBuilder {


    private static final Logger LOGGER = LoggerFactory.getLogger(SftpHealthCheckRoute.class);

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

    private MeterRegistry registry;

    public SftpHealthCheckRoute(MeterRegistry registry){
        this.registry = registry;
    }

    @Override
    public void configure() {
        final String agressoSftpOptions =
                "?maxMessagesPerPoll=1" +
                        "&throwExceptionOnConnectFailed=true" +
                        "&delete=true" +
                        "&knownHostsFile=" + vaultPath + "/known_hosts" +
                        "&privateKeyFile=" + vaultPath + "/betsysKey" +
                        "&privateKeyPassphrase=" + betsysKeyPassphrase;

        final String betsysSftpOptions =
                "?throwExceptionOnConnectFailed=true" +
                        "&maxMessagesPerPoll=1" +
                        "&useFixedDelay=15000" +
                        "&delete=true" +
                        "&knownHostsFile=" + vaultPath + "/known_hosts" +
                        "&privateKeyFile=" + vaultPath + "/betsysKey" +
                        "&privateKeyPassphrase=" + agressoKeyPassphrase;

        final String agressoOutbound = "sftp://" + agressoSftpUser + "@" + agressoSftpPath + "/outbound/.health";
        final String betsysOutbound = "sftp://" + betsysSftpUser + "@" + betsysSftpPath + "/outbound/.health";

        LOGGER.info("Setter opp Agresso og Betsys health check Camel-route");

        from(agressoOutbound +  agressoSftpOptions )
                .log("reading health file from agresso to betsys")
                .routeId("sftpHealthCheck")
                .to(betsysOutbound + betsysSftpOptions)
                .end();

        from(betsysOutbound + betsysSftpOptions )
                .log("reading health file from betsys to agresso")
                    .to(agressoOutbound +  agressoSftpOptions)
                    .process(exchange ->  registry.gauge("agresso_to_betsys_sftp_health_check", new AtomicLong()).set(System.currentTimeMillis()/ 1000))
                .end();
    }

}


