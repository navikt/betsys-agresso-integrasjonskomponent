package no.nav.oko.betsys.agresso.integrasjonskomponent;

import io.micrometer.core.instrument.MeterRegistry;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.UseAdviceWith;
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
        properties = { "camel.springboot.java-routes-include-pattern=**/AgressoTilBetsysRoute*"})
@UseAdviceWith
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class AgressoTilBetsysRouteTest {

    @Autowired
    private CamelContext context;

    @Autowired
    private ProducerTemplate template;

    @Autowired
    private MeterRegistry registry;

    @Before
    public void cleanDir() throws Exception {
        deleteDirectory("target/inbound");
    }

    @Test
    public void testReplaceFromWithEndpoints() throws Exception {
        RouteDefinition route = context.getRouteDefinition("KopierFilFraAgresso");
        route.adviceWith(context, new AdviceWithRouteBuilder() {
            public void configure() throws Exception {
                replaceFromWith("file://target/inbound");
                mockEndpoints("validator:*");
               // weaveById("sftp://admin@127.0.0.44:4696/outbound").replace().to("file://target/outbound");
                mockEndpointsAndSkip("sftp*");
               // mockEndpointsAndSkip("validator*");
                //mockEndpointsAndSkip("betsysJMS");
                weaveById("betsysJMS").remove();
                //  interceptSendToEndpoint("betsysJMS").to("mock:ref:betsysUt");
            }
        });
        MockEndpoint mockvalidator = context.getEndpoint("mock:validator:file:pain.001.001.03.xsd", MockEndpoint.class);
        mockvalidator.expectedMessageCount(1);
        MockEndpoint outboundMock = context.getEndpoint("mock:sftp:admin@127.0.0.44:4696/outbound", MockEndpoint.class);
        outboundMock.expectedHeaderReceived(Exchange.FILE_NAME, "Agresso_44.xml");

        NotifyBuilder notify = new NotifyBuilder(context).whenCompleted(1).create();

        context.start();

//        MockEndpoint mockOut = context.getEndpoint("mock:betsysSftp", MockEndpoint.class);
//        mockOut.expectedMessageCount(1);
//        mockOut.expectedBodiesReceived("Camel rocks");

        //       template.sendBodyAndHeader("direct:hitme",
        //               "Hello World", Exchange.FILE_NAME, "hello.lis");
        template.sendBodyAndHeader("file://target/inbound",
                new File("target/test-classes/input/Agresso_44.lis"), Exchange.FILE_NAME, "Agresso_44.lis");
        //template.sendBodyAndHeaders("direct:hitme",);
        // template.sendBody("direct:hitme", "Camel rocks");
        // template.sendBody("direct:hitme", "Bad donkey");
        assertTrue(notify.matchesMockWaitTime());
        mockvalidator.assertIsSatisfied();
        outboundMock.assertIsSatisfied();
//        mockOut.assertIsSatisfied();
    }


}