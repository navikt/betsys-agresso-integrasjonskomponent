package no.nav.oko.betsys.agresso.integrasjonskomponent;

import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(CamelSpringBootRunner.class)
@DirtiesContext
@SpringBootTest(classes = JmsTestConfig.class)


public class IntegrasjonskomponentTest
{

    @Autowired
    private Sender sender;

    @Autowired
    private Receiver receiver;

    @BeforeClass
    public static void setup(){

        SFTPServerConfig server = new SFTPServerConfig();
        server.start(22, "first");
        server.start(23, "second");
    }

    @ClassRule
    public static EmbeddedActiveMQBroker broker = new EmbeddedActiveMQBroker();


    @Test
    public void testReceive() throws Exception {
        sender.send("helloworld.q", "Hello Spring JMS ActiveMQ!");

        receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
        assertThat(receiver.getLatch().getCount()).isEqualTo(0);

    }
}
