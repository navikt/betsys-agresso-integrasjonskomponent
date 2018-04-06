package no.nav.oko.betsys.agresso.integrasjonskomponent;

import no.nav.oko.betsys.agresso.integrasjonskomponent.config.EnvironmentConfig;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LesFilFraAgressoRoute extends RouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(LesFilFraAgressoRoute.class);

    private static final String SFTP_OPTIONS = "&delay=60000&readLock=changed&readLockMinAge=600000&move=Arkiv";

    @Override
    public void configure() throws Exception {
        String sftpPath = getSftpPathWithReadLock("filmottak.preprod.local", EnvironmentConfig.SFTPUSERNAME, EnvironmentConfig.SFTPPASSWORD);

        LOGGER.info("Setter opp Camel-route");
        LOGGER.info(sftpPath);

        from(sftpPath)
                // Kopier til betsys
                // Send SBDH på kø
                // Flytt samme fil til arkiv-mappen på FTP-server
                // Hvor ofte skal man polle FTP-serveren?
                // Hvor lenge skal man vente fra man poller til man leser filene?
                .log("Lest fil med navn: ${header.CamelFileNameOnly}")
                .log("Body: ${body}");
//                .process(new TilBetsysProcessor());
    }


    private String getSftpPathWithReadLock(String url, String username, String password) {
        return "sftp://" +
                username +
                "@" +
                url +
                "/inbound" +
                "?password=" +
                password +
                SFTP_OPTIONS;
    }
}
