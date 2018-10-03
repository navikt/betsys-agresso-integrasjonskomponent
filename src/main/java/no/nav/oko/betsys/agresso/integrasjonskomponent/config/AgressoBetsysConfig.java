package no.nav.oko.betsys.agresso.integrasjonskomponent.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableJms
@EnableTransactionManagement
@EnableCaching
@EnableConfigurationProperties({GatewayAlias.class})
@Configuration
public class AgressoBetsysConfig {}
