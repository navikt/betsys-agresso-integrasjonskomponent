package no.nav.oko.betsys.agresso.integrasjonskomponent;

import org.apache.camel.spring.SpringRouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LesFilFraAgressoRoute extends SpringRouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(LesFilFraAgressoRoute.class);

    private static final String SFTP_OPTIONS = "&delay=60000&streamDownload=true&move=Arkiv";

    //readLock=changed&readLockMinAge=60000 Hvis vi skal bruke dette må vi vente på ny versjon av camel-ftp
    // da dette skaper en bug som blir rettet i versjon 2.21.1

    @Value("${SFTPUSERNAME}")
    private String sftpUsername;

    @Value("${SFTPPASSWORD}")
    private String sftpPassword;

    @Value("${SFTPSERVERURL}")
    private String sftpUrl;

    private TilBetsysProcessor tilBetsysProcessor;

    @Autowired
    public LesFilFraAgressoRoute(TilBetsysProcessor tilBetsysProcessor) {
        this.tilBetsysProcessor = tilBetsysProcessor;
    }

    @Override
    public void configure() throws Exception {
        String sftpPath = getSftpPathWithReadLock(sftpUrl, sftpUsername, sftpPassword);

        LOGGER.info("Setter opp Camel-route");
        LOGGER.info(sftpPath);

        from(sftpPath)
                .log("Lest fil med navn: ${header.CamelFileNameOnly}")
                .log("Body: ${body}")
                .process(tilBetsysProcessor)
                .to("ref:betsysInn");
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
