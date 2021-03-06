package no.nav.oko.betsys.agresso.integrasjonskomponent.config;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.mq.jms.MQQueue;
import com.ibm.msg.client.wmq.WMQConstants;
import com.ibm.msg.client.wmq.compat.base.internal.MQC;
import org.apache.camel.component.jms.JmsConfiguration;
import org.apache.camel.component.jms.JmsEndpoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;

@Configuration
@ConditionalOnProperty(value = "optConfig", havingValue = "production")
public class JmsConfig {

    @Value("${CHANNELNAME}")
    private String channelName;

    @Value("${mqUser}")
    private String mqUser;

    @Value("${mqPassword:}")
    private String mqPassword;

    @Bean
    public Queue betsysInnQueue(@Value("${SENDING_TIL_AGRESSO_QUEUE}") String betsysInnQueue) throws JMSException {
        return new MQQueue(betsysInnQueue);
    }

    @Bean
    public Queue betsysUtQueue(@Value("${SENDING_TIL_BANK_QUEUE}") String betsysUtQueue) throws JMSException {
        MQQueue mqQueue = new MQQueue(betsysUtQueue);
        mqQueue.setTargetClient(WMQConstants.WMQ_CLIENT_NONJMS_MQ);
        return mqQueue;
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

    @Bean
    public ConnectionFactory wmqConnectionFactory(GatewayAlias gateway) throws JMSException {
        MQConnectionFactory connectionFactory = new MQConnectionFactory();
        connectionFactory.setHostName(gateway.getHostname());
        connectionFactory.setPort(gateway.getPort());
        connectionFactory.setChannel(channelName);
        connectionFactory.setQueueManager(gateway.getName());
        connectionFactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
        connectionFactory.setCCSID(1208);
        connectionFactory.setIntProperty(WMQConstants.JMS_IBM_ENCODING, MQC.MQENC_NATIVE);
        connectionFactory.setIntProperty(WMQConstants.JMS_IBM_CHARACTER_SET, 1208);

        UserCredentialsConnectionFactoryAdapter adapter = new UserCredentialsConnectionFactoryAdapter();
        adapter.setTargetConnectionFactory(connectionFactory);
        adapter.setUsername(mqUser);
        adapter.setPassword(mqPassword);

        return adapter;
    }
}
