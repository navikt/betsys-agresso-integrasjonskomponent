package no.nav.oko.betsys.agresso.integrasjonskomponent;

import no.nav.generer.sbdh.SbdhService;
import no.nav.generer.sbdh.generer.SbdhType;
import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.sshd.server.SshServer;
import org.awaitility.Duration;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.*;
import static org.junit.Assert.*;

@RunWith(CamelSpringBootRunner.class)
@DirtiesContext
@SpringBootTest(classes = JmsTestConfig.class)
public class IntegrasjonskomponentITest
{

    @Autowired
    private Sender sender;

    @Autowired
    private Receiver receiver;

    private ClassLoader classLoader = IntegrasjonskomponentITest.class.getClassLoader();
    private URI baseFolder = classLoader.getResource("").toURI();
    private String mainPath = Paths.get(baseFolder).toString();

    private final String filstiStagingArea= "stagingArea/";
    private final String filstiTilBetsysUt = "Betsys/srv/nais_apps/q0/naisnfs/out/";
    private final  String filstiTilAgressoUt = "Agresso/outbound/";
    private final String filstiTilAgressoInn = "Agresso/inbound/";

    public IntegrasjonskomponentITest() throws URISyntaxException {
    }

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

        String filnavn = "Agresso_44.lis";
        Files.copy(Paths.get(classLoader.getResource(filstiStagingArea + filnavn).toURI()), Paths.get(mainPath,filstiTilAgressoUt + filnavn), StandardCopyOption.REPLACE_EXISTING);
        receiver.getLatch().await(100000, TimeUnit.MILLISECONDS);
        assertEquals(0, receiver.getLatch().getCount());
        await().atMost(Duration.FIVE_SECONDS).until( () ->  classLoader.getResource(filstiTilBetsysUt + filnavn) != null);
        assertNotNull(classLoader.getResource(filstiTilBetsysUt + filnavn));
        Files.delete(Paths.get(classLoader.getResource(filstiTilBetsysUt + filnavn).toURI()));
    }

    @Test
    public void enFilFraBetsysTilAgresso() throws URISyntaxException, IOException {
        String filnavn = "Agresso_45.xml";
        Files.copy(Paths.get(classLoader.getResource(filstiStagingArea + filnavn).toURI()),Paths.get(mainPath, filstiTilBetsysUt + filnavn) , StandardCopyOption.REPLACE_EXISTING);
        sender.send("agresso", SbdhService.opprettStringSBDH(SbdhType.PAIN001,filnavn.replace(".xml", ""), "test","test"));
        await().atMost(Duration.FIVE_SECONDS).until( () ->  classLoader.getResource(filstiTilAgressoInn + filnavn) != null);
        assertNotNull(classLoader.getResource(filstiTilAgressoInn + filnavn));
        Files.delete(Paths.get(classLoader.getResource(filstiTilAgressoInn + filnavn).toURI()));
    }
}
