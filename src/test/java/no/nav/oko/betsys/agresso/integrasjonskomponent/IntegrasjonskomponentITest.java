package no.nav.oko.betsys.agresso.integrasjonskomponent;

import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.sshd.server.SshServer;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(CamelSpringBootRunner.class)
@DirtiesContext
@SpringBootTest(classes = JmsTestConfig.class)


public class IntegrasjonskomponentITest
{

    @Autowired
    private Sender sender;

    @Autowired
    private Receiver receiver;

    @BeforeClass
    public static void setup() throws IOException {

        SFTPServerConfig serverConfig = new SFTPServerConfig();
        SshServer server = serverConfig.configure("127.0.0.1",2222, "Agresso");
        SshServer server2 = serverConfig.configure("127.0.0.2",2222, "Betsys");
        server.start();
        server2.start();
    }

    @ClassRule
    public static EmbeddedActiveMQBroker broker = new EmbeddedActiveMQBroker();


    @Test
    public void enFilFraAgressoTilBetsys() throws Exception {
        receiver.getLatch().await(100000, TimeUnit.MILLISECONDS);
        assertThat(receiver.getLatch().getCount()).isEqualTo(0);
    }
}
