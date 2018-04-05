package no.nav.oko.betsys.agresso.integrasjonskomponent;

import io.prometheus.client.Counter;
import io.prometheus.client.exporter.MetricsServlet;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import no.nav.oko.betsys.agresso.integrasjonskomponent.config.CamelConfig;
import no.nav.oko.betsys.agresso.integrasjonskomponent.config.EnvironmentConfig;
import no.nav.oko.betsys.agresso.integrasjonskomponent.endpoint.SelfcheckHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;


public class Integrasjonskomponent {

    private static final Logger log = LoggerFactory.getLogger(Integrasjonskomponent.class);

    private static final Counter testCounter = Counter.build().name("test").help("tester").register();


    public static void main(String[] args) {
        new Integrasjonskomponent().start();
    }

    private void start() {
        Server server = new Server(8080);
        HandlerCollection handlerCollection = new HandlerCollection();
        ServletContextHandler prometheusServletHandler = new ServletContextHandler();
        prometheusServletHandler.setContextPath("/prometheus");
        prometheusServletHandler.addServlet(MetricsServlet.class, "/");
        handlerCollection.setHandlers(new Handler[]{prometheusServletHandler, new SelfcheckHandler()});
        server.setHandler(handlerCollection);
        testCounter.inc();

        try {
            server.start();
        } catch (Exception e) {
            log.error("Failed to start Server:", e);
            System.exit(1);
        }

        new CamelConfig().setUpCamelContext();
        // Start consuming from ftp

//
//        try {
//
//            SftpConnection connection = new SftpConnection(EnvironmentConfig.NFSUSERNAME, EnvironmentConfig.NFSHOST, Integer.parseInt(EnvironmentConfig.NFSPORT), EnvironmentConfig.NFSPASSWORD);
//            connection.checkForNewFile();
//        } catch (JSchException e) {
//            log.error("JSchException: ", e);
//        }

        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, EnvironmentConfig.DFSUSERNAME, EnvironmentConfig.DFSPASSWORD);

        try {
            log.info("Trying to contact DFS share");
            SmbFile[] file = new SmbFile(EnvironmentConfig.DFSDOMAIN, auth).listFiles();

            log.info("Sucessfully retrieved files list", String.valueOf(file));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SmbException e) {
            e.printStackTrace();
        }

    }

}
