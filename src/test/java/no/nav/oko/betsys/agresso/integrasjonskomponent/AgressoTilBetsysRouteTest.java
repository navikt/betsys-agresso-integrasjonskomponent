package no.nav.oko.betsys.agresso.integrasjonskomponent;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.DisableJmx;
import org.apache.camel.test.spring.UseAdviceWith;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
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
        properties = {"camel.springboot.java-routes-include-pattern=**/AgressoTilBetsysRoute*"})
@UseAdviceWith
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@DisableJmx
@Ignore
//TODO fix tests
public class AgressoTilBetsysRouteTest {

    @Autowired
    private CamelContext context;

    @Autowired
    private ProducerTemplate template;

    @EndpointInject(uri = "mock:jms:betsys")
    private MockEndpoint mockJmsBetsys;

    private NotifyBuilder notify;

    private MockEndpoint mockValidator;

    @AfterClass
    public static void clenup() {
        deleteDirectory("target/inbound");
        deleteDirectory("target/outbound");
    }

    @Before
    public void setUp() throws Exception {
        deleteDirectory("target/inbound");
        deleteDirectory("target/outbound");
        context.getRouteDefinition("KopierFilFraAgresso").adviceWith(context, new AdviceWithRouteBuilder() {
            public void configure() throws Exception {
                replaceFromWith("file://target/inbound");
                mockEndpoints();
                weaveById("toBetsysServer").replace().to("file://target/outbound");
                weaveById("betsysJMS").replace().to(mockJmsBetsys);
            }
        });
        notify = new NotifyBuilder(context).whenCompleted(1).create();
        mockValidator = context.getEndpoint("mock:validator:file:pain.001.001.03.xsd", MockEndpoint.class);
        context.start();
    }

    @Test
    public void sendOneFileFromAgressoToBetsys() throws Exception {
        mockValidator.expectedMessageCount(1);
        mockJmsBetsys.expectedMessageCount(1);
        template.sendBodyAndHeader("file://target/inbound",
                new File("target/test-classes/input/Agresso_44.lis"), Exchange.FILE_NAME, "Agresso_44.lis");

        assertTrue("Route does not take more than 10 seconds", notify.matchesMockWaitTime());
        mockValidator.assertIsSatisfied();
        mockJmsBetsys.assertIsSatisfied();

        File target = new File("target/outbound/Agresso_44.xml");
        assertTrue("File is transferred ", target.exists());
    }
}