package no.nav.oko.betsys.agresso.integrasjonskomponent.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import static no.nav.oko.betsys.agresso.integrasjonskomponent.config.PrometheusLabels.LABEL_EVENT;
import static no.nav.oko.betsys.agresso.integrasjonskomponent.config.PrometheusLabels.LABEL_PROCESS;

@Configuration
public class MicrometerMetrics {

    private MeterRegistry registry;

    private static final String NAMESPACE = "agresso_betsys";

    public MicrometerMetrics(MeterRegistry registry){
        this.registry = registry;

        Counter.builder("agresso_total_counter")
                //.namespace(NAMESPACE)
                //.name("agresso_total_counter")
                .description("Counts total number of files copied from Agresso to Betsys")
                .tags(LABEL_PROCESS, LABEL_EVENT)
                .register(registry);
    }
}
