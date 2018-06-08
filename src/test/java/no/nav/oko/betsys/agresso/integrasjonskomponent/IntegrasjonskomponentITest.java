package no.nav.oko.betsys.agresso.integrasjonskomponent;

import no.nav.generer.sbdh.SbdhService;
import no.nav.generer.sbdh.generer.SbdhType;
import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.sshd.server.SshServer;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.awaitility.Duration;
import org.junit.Before;
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
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(CamelSpringBootRunner.class)
@DirtiesContext
@SpringBootTest(classes = JmsTestConfig.class)
public class IntegrasjonskomponentITest
{

    @Autowired
    private Sender sender;

    @Autowired
    private Receiver receiver;

    @Autowired
    private DLQReceiver dlqReceiver;




    private static final ClassLoader classLoader = IntegrasjonskomponentITest.class.getClassLoader();
    private static  URI baseFolder;

    static {
        try {
            baseFolder = classLoader.getResource("").toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static final String mainPath = Paths.get(baseFolder).toString();

    private static final String filstiStagingArea= "stagingArea/";
    private static final String filstiTilBetsysUt = "Betsys/outbound/";
    private static final String filstiTilBetsysInn = "Betsys/inbound/";
    private static final  String filstiTilAgressoUt = "Agresso/outbound/";
    private static final String filstiTilAgressoInn = "Agresso/inbound/";

    private static SshServer agressoServer;
    private static SshServer betsysServer;

    public IntegrasjonskomponentITest(){
    }

    @BeforeClass
    public static void setup() throws IOException {

        //TODO build directory path in order to remove empty files in agresso
        Files.createDirectories(Paths.get(mainPath,filstiTilAgressoUt));
        Files.createDirectories(Paths.get(mainPath,filstiTilAgressoInn));
        Files.createDirectories(Paths.get(mainPath,filstiTilBetsysUt));
        Files.createDirectories(Paths.get(mainPath,filstiTilBetsysInn));
        SFTPServerConfig serverConfig = new SFTPServerConfig();
        agressoServer = serverConfig.configure("127.0.0.4",2222, "Agresso");
        betsysServer = serverConfig.configure("127.0.0.5",2222, "Betsys");
        agressoServer.start();
        betsysServer.start();
    }

    @Before
    public void setUp(){
        try{
            FileUtils.cleanDirectory(Paths.get(mainPath,filstiTilAgressoUt).toFile());
        }catch (Exception e) {
            e.printStackTrace();
        }
        try{
            FileUtils.cleanDirectory(Paths.get(mainPath,filstiTilAgressoInn).toFile());
        }catch (Exception e) {
            e.printStackTrace();
        }
        try{
            FileUtils.cleanDirectory(Paths.get(mainPath,filstiTilBetsysUt).toFile());
        }catch (Exception e) {
            e.printStackTrace();
        }
        try{
            FileUtils.cleanDirectory(Paths.get(mainPath,filstiTilBetsysInn).toFile());
        }catch (Exception e) {
            e.printStackTrace();
        }
        receiver.resetCountDownLatch();
        dlqReceiver.resetCountDownLatch();
    }

    @ClassRule
    public static EmbeddedActiveMQBroker broker = new EmbeddedActiveMQBroker();


    @Test
    public void enFilFraAgressoTilBetsys() throws Exception {
        String filnavn = "Agresso_44.lis";
        Files.copy(Paths.get(classLoader.getResource(filstiStagingArea + filnavn).toURI()), Paths.get(mainPath,filstiTilAgressoUt + filnavn));
        receiver.getLatch().await(120, TimeUnit.SECONDS);
        assertEquals(0, receiver.getLatch().getCount());
        await().atMost(Duration.ONE_MINUTE).until( () ->  classLoader.getResource(filstiTilBetsysUt + filnavn) != null);
        assertNotNull(classLoader.getResource(filstiTilBetsysUt + filnavn));
    }

    @Test
    public void enFilFraBetsysTilAgresso() throws URISyntaxException, IOException {
        String filnavn = "Agresso_45.xml";
        Files.copy(Paths.get(classLoader.getResource(filstiStagingArea + filnavn).toURI()),Paths.get(mainPath, filstiTilBetsysInn + filnavn));
        sender.send("agresso", SbdhService.opprettStringSBDH(SbdhType.PAIN001,filnavn.replace(".xml", ""), "test","test"));
        await().atMost(Duration.ONE_MINUTE).until( () ->  classLoader.getResource(filstiTilAgressoInn + filnavn) != null);
        assertNotNull(classLoader.getResource(filstiTilAgressoInn + filnavn));
    }

    @Test
    public void feilFilFraAgressoTilBetsys() throws URISyntaxException, IOException {
        //todo sende Alarm ved slik feil
        String filnavn = "feilFil.xml";
        String errorMappe= "/Error/";
        Files.copy(Paths.get(classLoader.getResource(filstiStagingArea + filnavn).toURI()), Paths.get(mainPath,filstiTilAgressoUt + filnavn));
        await().atMost(Duration.ONE_MINUTE).until( () ->  classLoader.getResource(filstiTilAgressoUt + errorMappe + filnavn) != null);
        assertNotNull(classLoader.getResource(filstiTilAgressoUt + errorMappe + filnavn));
    }

    @Test
    public void manglendeFilFraBetsysTilAgresso() throws InterruptedException {
        //TODO sende alarm
        String filnavn = "noeTull.xml";
        sender.send("agresso", SbdhService.opprettStringSBDH(SbdhType.PAIN001,filnavn.replace(".xml", ""), "test","test"));
        dlqReceiver.getLatch().await(2, TimeUnit.MINUTES);
        assertEquals(0,dlqReceiver.getLatch().getCount());
    }
    @Test
    public void manglendeKontaktMedBetsysKo(){
        //TODO implement test
    }
    @Test
    public void manglendeKontaktMedAgressoKo(){
        //TODO implement test
    }
    @Test
    public void fullkoPaaBetsys(){
        //TODO implement test
    }
    @Test
    public void fullKoPaaAgresso(){
        //TODO implement test
    }
    @Test
    public void manglendeKontaktMedAgressoFilserver(){

    }
//    @Test
//    public void manglendeKontaktMedBetsysFilserver() throws InterruptedException, URISyntaxException, IOException {
//        //TODO sett opp retry her? eller i hvert fall alarm om feil
//        betsysServer.stop(true);
//        String filnavn = "Agresso_45.lis";
//        String errorMappe= "/Error/";
//        Files.copy(Paths.get(classLoader.getResource(filstiStagingArea + filnavn).toURI()), Paths.get(mainPath,filstiTilAgressoUt + filnavn), StandardCopyOption.REPLACE_EXISTING);
//        dlqReceiver.getLatch().await(120, TimeUnit.SECONDS);
//        assertEquals(1, dlqReceiver.getLatch().getCount());
//        await().atMost(Duration.ONE_MINUTE).until( () ->  classLoader.getResource(filstiTilAgressoUt + errorMappe + filnavn) != null);
//        assertNotNull(classLoader.getResource(filstiTilAgressoUt + errorMappe + filnavn) != null);
//
//        betsysServer.start();
//
//
//    }
    @Test
    public void fullDiskPaaAgressoFilserver(){
        //TODO implement test
    }
    @Test
    public void fullDiskPaaBetsysFilserver(){
        //TODO implement test
    }

}
