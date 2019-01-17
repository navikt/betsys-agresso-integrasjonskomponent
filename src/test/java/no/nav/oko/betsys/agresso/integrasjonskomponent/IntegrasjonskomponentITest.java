//package no.nav.oko.betsys.agresso.integrasjonskomponent;
//
//import io.micrometer.core.instrument.MeterRegistry;
//import no.nav.generer.sbdh.SbdhService;
//import no.nav.generer.sbdh.generer.SbdhType;
//import org.apache.activemq.junit.EmbeddedActiveMQBroker;
//import org.apache.camel.test.spring.CamelSpringBootRunner;
//import org.apache.sshd.server.SshServer;
//import org.apache.tomcat.util.http.fileupload.FileUtils;
//import org.awaitility.Duration;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.DirtiesContext;
//
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.concurrent.TimeUnit;
//
//import static org.awaitility.Awaitility.await;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
//
//@RunWith(CamelSpringBootRunner.class)
//@SpringBootTest(classes = JmsTestConfig.class)
//@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
//public class IntegrasjonskomponentITest
//{
//
//    @Autowired
//    private Sender sender;
//
//    @Autowired
//    private Receiver receiver;
//
//    @Autowired
//    private DLQReceiver dlqReceiver;
//
//    @Autowired
//    private MeterRegistry registry;
//
//
//
//    private static final ClassLoader classLoader = IntegrasjonskomponentITest.class.getClassLoader();
//    private static  URI baseFolder;
//
//    static {
//        try {
//            baseFolder = classLoader.getResource("").toURI();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static final String mainPath = Paths.get(baseFolder).toString();
//
//    private static final String filstiStagingArea= "stagingArea/";
//    private static final String filstiTilBetsysUt = "Betsys/outbound/";
//    private static final String filstiTilBetsysInn = "Betsys/inbound/";
//    private static final  String filstiTilAgressoUt = "Agresso/outbound/";
//    private static final String filstiTilAgressoInn = "Agresso/inbound/";
//
//    private static SshServer agressoServer;
//    private static SshServer betsysServer;
//
//    public IntegrasjonskomponentITest(){
//    }
//
//    @BeforeClass
//    public static void setup() throws IOException {
//        Files.createDirectories(Paths.get(mainPath,filstiTilAgressoUt));
//        Files.createDirectories(Paths.get(mainPath,filstiTilAgressoInn));
//        Files.createDirectories(Paths.get(mainPath,filstiTilBetsysUt));
//        Files.createDirectories(Paths.get(mainPath,filstiTilBetsysInn));
//
//    }
//
//    @Before
//    public void setUp() throws IOException {
//        FileUtils.cleanDirectory(Paths.get(mainPath,filstiTilAgressoUt).toFile());
//        FileUtils.cleanDirectory(Paths.get(mainPath,filstiTilAgressoInn).toFile());
//        FileUtils.cleanDirectory(Paths.get(mainPath,filstiTilBetsysUt).toFile());
//        FileUtils.cleanDirectory(Paths.get(mainPath,filstiTilBetsysInn).toFile());
//        SFTPServerConfig serverConfig = new SFTPServerConfig();
//        agressoServer = serverConfig.configure("127.0.0.33",4696, "Agresso");
//        betsysServer = serverConfig.configure("127.0.0.44",4696, "Betsys");
//        agressoServer.start();
//        betsysServer.start();
//
//
//    }
//
//    @After
//    public void tearDown() throws IOException {
//        agressoServer.stop(true);
//        betsysServer.stop(true);
//    }
//
//    @Rule
//    public EmbeddedActiveMQBroker broker = new EmbeddedActiveMQBroker();
//
//    @Test
//    public void enFilFraAgressoTilBetsys() throws Exception {
//        String filnavn = "Agresso_44.lis";
//        Files.copy(Paths.get(classLoader.getResource(filstiStagingArea + filnavn).toURI()), Paths.get(mainPath,filstiTilAgressoUt + filnavn));
//        receiver.getLatch().await(120, TimeUnit.SECONDS);
//        assertEquals(0, receiver.getLatch().getCount());
//        await().atMost(Duration.ONE_MINUTE).until( () ->  classLoader.getResource(filstiTilBetsysUt + filnavn.replace(".lis", ".xml")) != null);
//        assertNotNull(classLoader.getResource(filstiTilBetsysUt +  filnavn.replace(".lis", ".xml")));
//    }
//
//    @Test
//    public void enFilFraBetsysTilAgresso() throws URISyntaxException, IOException {
//      //  sender.send("agresso", SbdhService.opprettStringSBDH(SbdhType.PAIN001,"merTullball", "test","test"));
//        // TODO lag en test for hva som skjer ved først søppel så riktig?
//        String filnavn = "Agresso_45.xml";
//        Files.copy(Paths.get(classLoader.getResource(filstiStagingArea + filnavn).toURI()),Paths.get(mainPath, filstiTilBetsysInn + filnavn));
//        sender.send("agresso", SbdhService.opprettStringSBDH(SbdhType.PAIN001,filnavn.replace(".xml", ""), "test","test"));
//        await().atMost(Duration.FIVE_MINUTES).until( () ->  classLoader.getResource(filstiTilAgressoInn + filnavn) != null);
//        assertNotNull(classLoader.getResource(filstiTilAgressoInn + filnavn));
//    }
//
////    @Test
////    public void feilFilFraAgressoTilBetsys() throws URISyntaxException, IOException {
////        //todo sende Alarm ved slik feil
////        String filnavn = "feilFil.xml";
////        String errorMappe= "/Error/";
////        Files.copy(Paths.get(classLoader.getResource(filstiStagingArea + filnavn).toURI()), Paths.get(mainPath,filstiTilAgressoUt + filnavn));
////        await().atMost(Duration.ONE_MINUTE).until( () ->  classLoader.getResource(filstiTilAgressoUt + errorMappe + filnavn) != null);
////        assertNotNull(classLoader.getResource(filstiTilAgressoUt + errorMappe + filnavn));
////    }
//    @Test
//    public void manglendeFilFraBetsysTilAgresso() throws InterruptedException {
//        String filnavn = "noeTull.xml";
//        sender.send("agresso", SbdhService.opprettStringSBDH(SbdhType.PAIN001,filnavn.replace(".xml", ""), "test","test"));
//        dlqReceiver.getLatch().await(2, TimeUnit.MINUTES);
//        assertEquals(0,dlqReceiver.getLatch().getCount());
//    }
////    @Test
////    public void manglendeKontaktMedBetsysKo(){
////        //TODO implement test
////    }
////    @Test
////    public void manglendeKontaktMedAgressoKo(){
////        //TODO implement test
////    }
////    @Test
////    public void fullkoPaaBetsys(){
////        //TODO implement test
////    }
////    @Test
////    public void fullKoPaaAgresso(){
////        //TODO implement test
////    }
//    @Test
//    public void manglendeKontaktMedAgressoFilserverFraAgressoRoute() throws IOException {
//            agressoServer.stop(true);
//        await().atMost(Duration.ONE_MINUTE).until(() -> registry.find("agresso_to_betsys_exception_counter")
//                                                                .tag("Exception", "GenericFileOperationFailedException")
//                                                                .counter() != null);
//
//        assertTrue(registry.get("agresso_to_betsys_exception_counter")
//                                     .tag("Exception","GenericFileOperationFailedException")
//                                     .counter().count() > 0);
//    }
//    @Test
//    public void manglendeKontaktMedBetsysFilserverFraAgressoRoute() throws URISyntaxException, IOException {
//        betsysServer.stop(true);
//        String filnavn = "Agresso_44.lis";
//        Files.copy(Paths.get(classLoader.getResource(filstiStagingArea + filnavn).toURI()), Paths.get(mainPath,filstiTilAgressoUt + filnavn));
//
//        await().atMost(Duration.ONE_MINUTE).until(() -> registry.find("agresso_to_betsys_exception_counter")
//                .tag("Exception", "GenericFileOperationFailedException")
//                .counter() != null);
//
//        assertTrue(registry.get("agresso_to_betsys_exception_counter")
//                .tag("Exception","GenericFileOperationFailedException")
//                .counter().count() > 0);
//
//        assertEquals(1, receiver.getLatch().getCount());
//
//        assertNotNull(classLoader.getResource(filstiTilAgressoUt + filnavn));
//    }
//
//    @Test
//    public void manglendeKontaktMedBetsysFilserverFraBetsysRoute() throws URISyntaxException, IOException {
//        betsysServer.stop(true);
//        String filnavn = "Agresso_45.xml";
//        Files.copy(Paths.get(classLoader.getResource(filstiStagingArea + filnavn).toURI()), Paths.get(mainPath,filstiTilBetsysInn + filnavn));
//        sender.send("agresso", SbdhService.opprettStringSBDH(SbdhType.PAIN001,filnavn.replace(".xml", ""), "test","test"));
//        await().atMost(Duration.ONE_MINUTE).until(() -> registry.find("betsys_to_agresso_exception_counter")
//                .tag("Exception", "GenericFileOperationFailedException")
//                .counter() != null);
//
//        assertTrue(registry.get("betsys_to_agresso_exception_counter")
//                .tag("Exception","GenericFileOperationFailedException")
//                .counter().count() > 0);
//
//    }
//
//    @Test
//    public void manglendeKontaktMedAgressoFilserverFraBetsysRoute() throws URISyntaxException, IOException {
//        agressoServer.stop(true);
//        String filnavn = "Agresso_45.xml";
//        Files.copy(Paths.get(classLoader.getResource(filstiStagingArea + filnavn).toURI()), Paths.get(mainPath,filstiTilBetsysInn + filnavn));
//        sender.send("agresso", SbdhService.opprettStringSBDH(SbdhType.PAIN001,filnavn.replace(".xml", ""), "test","test"));
//        await().atMost(Duration.ONE_MINUTE).until(() -> registry.find("betsys_to_agresso_exception_counter")
//                .tag("Exception", "GenericFileOperationFailedException")
//                .counter() != null);
//        assertTrue(registry.get("betsys_to_agresso_exception_counter")
//                .tag("Exception","GenericFileOperationFailedException")
//                .counter().count() > 0);
//
//    }
////    @Test
////    public void fullDiskPaaAgressoFilserver(){
////        //TODO implement test
////    }
////    @Test
////    public void fullDiskPaaBetsysFilserver(){
////        //TODO implement test
////    }
////
//  }
