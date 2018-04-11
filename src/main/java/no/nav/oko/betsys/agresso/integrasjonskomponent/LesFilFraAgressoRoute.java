package no.nav.oko.betsys.agresso.integrasjonskomponent;

import org.apache.camel.ValidationException;
import org.apache.camel.spring.SpringRouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LesFilFraAgressoRoute extends SpringRouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(LesFilFraAgressoRoute.class);

    private static final String SFTP_OPTIONS = "&useUserKnownHostsFile=false&delay=60000&move=Arkiv";

    //readLock=changed&readLockMinAge=60000 Hvis vi skal bruke dette må vi vente på ny versjon av camel-ftp
    // da dette skaper en bug som blir rettet i versjon 2.21.1

    @Value("${SFTPUSERNAME}")
    private String sftpUsername;

    @Value("${SFTPPASSWORD}")
    private String sftpPassword;

    @Value("${SFTPSERVERURL}")
    private String sftpUrl;

    @Value("${BETSYS_SFTP_SERVER_URL}")
    private String betsysSftpUrl;

    @Value("${BETSYSUSERNAME}")
    private String betsysSftpUsername;

    @Value("${BETSYSPASSWORD}")
    private String betsysSftpPassword;


    private TilBetsysProcessor tilBetsysProcessor;

    @Autowired
    public LesFilFraAgressoRoute(TilBetsysProcessor tilBetsysProcessor) {
        this.tilBetsysProcessor = tilBetsysProcessor;
    }

    @Override
    public void configure() throws Exception {
        String sftpPath = getSftpPathWithReadLock(sftpUrl, sftpUsername, sftpPassword);
        String betsysSftpPath = "sftp://" + betsysSftpUsername + "@" + betsysSftpUrl + "/srv/nais_apps/q0/naisnfs" + "?password=" + betsysSftpPassword + "&useUserKnownHostsFile=false";

        LOGGER.info("Setter opp Camel-route");
        LOGGER.info(sftpPath);

        onException(ValidationException.class)
                .log("Caught ValidationException")
                .end();


        from(sftpPath)
//        from(betsysSftpPath)
                .log("Lest fil med navn: ${header.CamelFileNameOnly}")
                .log("Body: ${body}")
                .to("validator:file:pain.001.001.03.xsd")
                .to(betsysSftpPath)
                .to("ref:betsysUt")
                .end();




//                .process(tilBetsysProcessor)
//
//
//        from("file://inbox")
//                .log("Lest fil med navn: ${header.CamelFileNameOnly}")
//                .log("Body: ${body}")

//                .to(sftpPath);
            //    .to("ref:betsysInn");
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
