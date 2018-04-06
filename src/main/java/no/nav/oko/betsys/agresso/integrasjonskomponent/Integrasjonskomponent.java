package no.nav.oko.betsys.agresso.integrasjonskomponent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Integrasjonskomponent {

//    private static final Logger log = LoggerFactory.getLogger(Integrasjonskomponent.class);
//
//    private static final Counter testCounter = Counter.build().name("test").help("tester").register();

    public static void main(String[] args) {
        SpringApplication.run(Integrasjonskomponent.class, args);
    }

//    private void start() {
//        Server server = new Server(8080);
//        HandlerCollection handlerCollection = new HandlerCollection();
//        ServletContextHandler prometheusServletHandler = new ServletContextHandler();
//        prometheusServletHandler.setContextPath("/prometheus");
//        prometheusServletHandler.addServlet(MetricsServlet.class, "/");
//        handlerCollection.setHandlers(new Handler[]{prometheusServletHandler, new SelfcheckHandler()});
//        server.setHandler(handlerCollection);
//        testCounter.inc();
//
//        try {
//            server.start();
//        } catch (Exception e) {
//            log.error("Failed to start Server:", e);
//            System.exit(1);
//        }
//
//        new CamelConfig().setUpCamelContext();
//    }

}
