package no.nav.oko.betsys.agresso.integrasjonskomponent;

import com.jcraft.jsch.JSchException;
import io.prometheus.client.Counter;
import io.prometheus.client.exporter.MetricsServlet;
import no.nav.oko.betsys.agresso.integrasjonskomponent.config.EnvironmentConfig;
import no.nav.oko.betsys.agresso.integrasjonskomponent.endpoint.SelfcheckHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Integrasjonskomponent
{
    private static final Logger log = LoggerFactory.getLogger(Integrasjonskomponent.class);

    private static final Counter testCounter = Counter.build().name("test").help("tester").register();


    public static void main( String[] args ) {
        new Integrasjonskomponent().start();
    }

    private void start() {
        Server server = new Server(8080);
        HandlerCollection handlerCollection = new HandlerCollection();
        ServletContextHandler prometheusServletHandler = new ServletContextHandler();
        prometheusServletHandler.setContextPath("/prometheus");
        prometheusServletHandler.addServlet(MetricsServlet.class, "/");
        handlerCollection.setHandlers(new Handler[] { prometheusServletHandler, new SelfcheckHandler() });
        server.setHandler(handlerCollection);
        testCounter.inc();


        try {
            server.start();
        } catch (Exception e) {
            log.error("Failed to start Server:", e);
            System.exit(1);
        }

        try {

            SftpConnection connection = new SftpConnection(EnvironmentConfig.NFSUSERNAME, EnvironmentConfig.NFSHOST, Integer.parseInt(EnvironmentConfig.NFSPORT), EnvironmentConfig.NFSPASSWORD);
            connection.checkForNewFile();
        } catch (JSchException e) {
            log.error("JSchException: ", e);
        }

    }

}
