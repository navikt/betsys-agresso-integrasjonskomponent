package no.nav.oko.betsys.agresso.integrasjonskomponent;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.component.jms.JmsConfiguration;
import org.apache.camel.component.jms.JmsEndpoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;

@TestConfiguration
@EnableJms
public class JmsTestConfig {

    @Bean
    @Primary
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL("vm://embedded-broker?create=false");
        return activeMQConnectionFactory;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        return new CachingConnectionFactory(activeMQConnectionFactory());
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(cachingConnectionFactory());
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(activeMQConnectionFactory());
        factory.setConcurrency("3-10");
        return factory;
    }

    @Bean
    public Receiver receiver() {
        return new Receiver();
    }

    @Bean
    public DLQReceiver dlqReceiver() {
        return new DLQReceiver();
    }


    @Bean
    public Sender sender() {
        return new Sender();
    }



    @Bean
    public Queue betsysInnQueue(@Value("${SENDING_TIL_AGRESSO_QUEUE}") String betsysInnQueue){
        return () -> betsysInnQueue;
    }

    @Bean
    public Queue betsysUtQueue(@Value("${SENDING_TIL_BANK_QUEUE}") String betsysUtQueue) {
        return () -> betsysUtQueue;
    }

    @Bean("betsysInn")
    public JmsEndpoint betsysInnEndpoint(Queue betsysInnQueue,
                                         JmsConfiguration jmsConfiguration) throws JMSException {
        JmsEndpoint jmsEndpoint = JmsEndpoint.newInstance(betsysInnQueue);
        jmsEndpoint.setConfiguration(jmsConfiguration);
        return jmsEndpoint;
    }

    @Bean("betsysUt")
    public JmsEndpoint betsysUtEndpoint(Queue betsysUtQueue,
                                        JmsConfiguration jmsConfiguration) throws JMSException {
        JmsEndpoint jmsEndpoint = JmsEndpoint.newInstance(betsysUtQueue);
        jmsEndpoint.setConfiguration(jmsConfiguration);
        return jmsEndpoint;
    }

    @Bean
    public JmsConfiguration jmsConfiguration(ConnectionFactory connectionFactory) {
        JmsConfiguration jmsConfiguration = new JmsConfiguration();
        jmsConfiguration.setConnectionFactory(connectionFactory);
        jmsConfiguration.setTransacted(true);
        jmsConfiguration.setLazyCreateTransactionManager(true);
        return jmsConfiguration;
    }

}
