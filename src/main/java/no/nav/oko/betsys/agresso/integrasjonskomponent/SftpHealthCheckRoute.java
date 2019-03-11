package no.nav.oko.betsys.agresso.integrasjonskomponent;

import io.micrometer.core.instrument.MeterRegistry;
import no.nav.oko.betsys.agresso.integrasjonskomponent.config.CamelSftpConfig;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spring.SpringRouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class SftpHealthCheckRoute extends SpringRouteBuilder {


    private static final Logger LOGGER = LoggerFactory.getLogger(SftpHealthCheckRoute.class);

    private ProducerTemplate template;

    private MeterRegistry registry;

    private AtomicLong atomicLong;

    private CamelSftpConfig sftpConfig;

    public SftpHealthCheckRoute(MeterRegistry registry, ProducerTemplate template, CamelSftpConfig sftpConfig) {
        this.registry = registry;
        this.template = template;
        atomicLong = new AtomicLong();
        this.sftpConfig = sftpConfig;
    }

    @Override
    public void configure() {

        LOGGER.info("Sender oppstartsfil");
        template.sendBodyAndHeader(sftpConfig.sftpHealthCheckRouteAgressoSftp(),
                "health check", Exchange.FILE_NAME, "healthCheck");

        LOGGER.info("Setter opp Agresso og Betsys health check Camel-route");
        from(sftpConfig.sftpHealthCheckRouteAgressoSftp())
                .routeId("sftpHealthCheck")
                .to(sftpConfig.sftpHealthCheckRouteBetsysSftp())
                .end();

        from(sftpConfig.sftpHealthCheckRouteBetsysSftp())
                .to(sftpConfig.sftpHealthCheckRouteAgressoSftp())
                .process(exchange ->
                        Objects.requireNonNull(registry.gauge("agresso_to_betsys_sftp_health_check", atomicLong))
                                .set(System.currentTimeMillis() / 1000))
                .end();
    }

}


