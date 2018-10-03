package no.nav.oko.betsys.agresso.integrasjonskomponent.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MicrometerMetrics {

    private static final String NAMESPACE = "agresso_betsys";

    public MicrometerMetrics(MeterRegistry registry) {

//        Counter.builder("agresso_total_counter")
//                //.namespace(NAMESPACE)
//                //.name("agresso_total_counter")
//                .description("Counts total number of files copied from Agresso to Betsys")
//                .tags(LABEL_PROCESS, LABEL_EVENT)
//                .register(registry);
    }
//
//    @Bean
//    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
//        return registry -> registry.config().commonTags("Agresso_Betsys", "Agresso_Betsys");
//    }
}
