package no.nav.oko.betsys.agresso.integrasjonskomponent.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.apache.camel.CamelContext;
import org.apache.camel.component.micrometer.eventnotifier.MicrometerExchangeEventNotifier;
import org.apache.camel.component.micrometer.messagehistory.MicrometerMessageHistoryFactory;
import org.apache.camel.component.micrometer.routepolicy.MicrometerRoutePolicyFactory;
import org.apache.camel.component.micrometer.spi.InstrumentedThreadPoolFactory;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableJms
@EnableTransactionManagement
@EnableCaching
@EnableConfigurationProperties({GatewayAlias.class})
@Configuration
public class AgressoBetsysConfig {

    private MeterRegistry registry;

    public AgressoBetsysConfig(MeterRegistry registry) {
        this.registry = registry;
    }

//    @Bean
//    CamelContextConfiguration contextConfiguration() {
//        return new CamelContextConfiguration() {
//            @Override
//            public void beforeApplicationStart(CamelContext context) {
//                MicrometerRoutePolicyFactory factory = new MicrometerRoutePolicyFactory();
//                factory.setMeterRegistry(registry);
//                context.addRoutePolicyFactory(factory);
//                //  context.setMessageHistoryFactory(new MicrometerMessageHistoryFactory());
//                //  context.getManagementStrategy().addEventNotifier(new MicrometerExchangeEventNotifier());
//            }
//
//            @Override
//            public void afterApplicationStart(CamelContext camelContext) {
//
//            }
//        };
//
//
//    }
}