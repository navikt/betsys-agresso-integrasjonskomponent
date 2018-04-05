package no.nav.oko.betsys.agresso.integrasjonskomponent;

import no.nav.oko.betsys.agresso.integrasjonskomponent.config.EnvironmentConfig;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.routepolicy.quartz.CronScheduledRoutePolicy;
import org.apache.camel.routepolicy.quartz.SimpleScheduledRoutePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class LesFilFraAgressoRoute extends RouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(LesFilFraAgressoRoute.class);

    @Override
    public void configure() throws Exception {

        // sftp://[username@]hostname[:port]/directoryname[?options]

//        CronScheduledRoutePolicy startPolicy = new CronScheduledRoutePolicy();
//        startPolicy.setRouteStartTime("*/30 * * * * ? *");
//        startPolicy.setRoute

        String sftpPath = getFtpPath("filmottak.preprod.local", EnvironmentConfig.SFTPUSERNAME, EnvironmentConfig.SFTPPASSWORD);

        LOGGER.info("Setter opp Camel-route");
        LOGGER.info(sftpPath);

        from(sftpPath)
//                .routePolicy(startPolicy)
//                .noAutoStartup()
                // Kopier til betsys
                // Send SBDH på kø
                // Flytt samme fil til arkiv-mappen på FTP-server
                // Hvor ofte skal man polle FTP-serveren?
                // Hvor lenge skal man vente fra man poller til man leser filene?
                .log("Lest fil med navn: ${header.CamelFileNameOnly}")
                .log("Body: ${body}");
    }

    private String getFtpPath(String type, String username, String password) {
        return "sftp://" +
                username +
                "@" +
                type +
                "/inbound" +
                "?password=" +
                password + "&delay=15000";
    }
}
