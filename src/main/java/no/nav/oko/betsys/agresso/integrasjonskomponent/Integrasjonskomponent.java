package no.nav.oko.betsys.agresso.integrasjonskomponent;

import com.jcraft.jsch.JSchException;
import io.prometheus.client.exporter.MetricsServlet;
import no.nav.oko.betsys.agresso.integrasjonskomponent.config.EnvironmentConfig;
import no.nav.oko.betsys.agresso.integrasjonskomponent.endpoint.SelfcheckHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class Integrasjonskomponent
{
    private Server server;
    private final static Logger log = LoggerFactory.getLogger(Integrasjonskomponent.class);

    public static void main( String[] args ) throws IOException {
        new Integrasjonskomponent().start(args);
    }

    private void start(String[] args) throws IOException {
        server = new Server(8080);
        HandlerCollection handlerCollection = new HandlerCollection();
        ServletContextHandler prometheusServletHandler = new ServletContextHandler();
        prometheusServletHandler.setContextPath("/metrics");
        prometheusServletHandler.addServlet(MetricsServlet.class, "/");
        handlerCollection.setHandlers(new Handler[] { prometheusServletHandler, new SelfcheckHandler() });
        server.setHandler(handlerCollection);

        try {
            server.start();
        } catch (Exception e) {
            log.error("Failed to start Server:", e);
            System.exit(1);
        }

        try {
            SftpConnection connection = new SftpConnection(EnvironmentConfig.nfsusername, EnvironmentConfig.nfsHost, Integer.parseInt(EnvironmentConfig.nfsPort), EnvironmentConfig.nfsPassword);
            connection.checkForNewFile();
        } catch (JSchException e) {
            log.error("JSchException: ", e);
        }

    }

}
