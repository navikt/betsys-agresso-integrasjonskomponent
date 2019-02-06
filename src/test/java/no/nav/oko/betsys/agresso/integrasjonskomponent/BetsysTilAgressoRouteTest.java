package no.nav.oko.betsys.agresso.integrasjonskomponent;

import io.micrometer.core.instrument.MeterRegistry;
import no.nav.generer.sbdh.SbdhService;
import no.nav.generer.sbdh.generer.SbdhType;
import org.apache.camel.*;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.stub.StubConsumer;
import org.apache.camel.component.stub.StubEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.DisableJmx;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.io.File;

import static org.apache.camel.test.junit4.TestSupport.deleteDirectory;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {Integrasjonskomponent.class},
        properties = { "camel.springboot.java-routes-include-pattern=**/BetsysTilAgressoRoute*"})
@UseAdviceWith
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@DisableJmx
public class BetsysTilAgressoRouteTest {

    @Autowired
    private CamelContext context;

    @Autowired
    private ProducerTemplate template;

    @Autowired
    private MeterRegistry registry;

    @EndpointInject(uri = "stub:jms:betsysInn")
    private StubEndpoint stubJmsBetsysInn;

    private NotifyBuilder notify;

    @AfterClass
    public static void clenup(){
        deleteDirectory("target/inbound");
        deleteDirectory("target/outbound");
    }

    @Before
    public void setUp() throws Exception {
        deleteDirectory("target/inbound");
        deleteDirectory("target/outbound");
        context.getRouteDefinition("KopierFilFraBetsys").adviceWith(context, new AdviceWithRouteBuilder() {
            public void configure() throws Exception {
                replaceFromWith(stubJmsBetsysInn);
                weaveById("betsysServer").replace().pollEnrich("file://target/inbound?fileName=${body}");
                weaveById("agressoServer").replace().to("file://target/outbound");
                mockEndpoints();
            }
        });
        notify = new NotifyBuilder(context).whenCompleted(1).create();
        context.start();
    }

    @Test
    public void sendOneFileFromBetsysToAgresso() {
        template.sendBodyAndHeader("file://target/inbound",
                new File("target/test-classes/input/Agresso_44.lis"), Exchange.FILE_NAME, "Agresso_44.xml");
        template.sendBody(stubJmsBetsysInn,SbdhService.opprettStringSBDH(SbdhType.PAIN001,"Agresso_44","11111111111", "22222222222"));
        assertTrue("Route does not take more than 10 seconds", notify.matchesMockWaitTime());
        File target = new File("target/outbound/Agresso_44.xml");
        assertTrue("File is transferred ", target.exists());
    }


}