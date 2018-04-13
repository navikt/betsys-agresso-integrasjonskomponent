package no.nav.oko.betsys.agresso.integrasjonskomponent;

import org.apache.camel.spring.SpringRouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LesFilFraBetsysRoute extends SpringRouteBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(LesFilFraBetsysRoute.class);

    private static final String SFTP_OPTIONS = "&useUserKnownHostsFile=false&delay=60000&move=Arkiv";

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


    @Override
    public void configure() throws Exception {
        String sftpPath = getSftpPathWithReadLock(sftpUrl, sftpUsername, sftpPassword);
        String betsysSftpPath = "sftp://" + betsysSftpUsername + "@" + betsysSftpUrl + "/srv/nais_apps/q0/naisnfs/out" + "?password=" + betsysSftpPassword + "&useUserKnownHostsFile=false";

        LOGGER.info("Setter opp Camel-route");
        LOGGER.info(sftpPath);

        from("ref:betsysInn")
                .log("Body: ${body}")
               // .to("processor")
                .to("direct:fileTransferRoute")
                .end();

        from("direct:fileTransferRoute")
                .routeId("fileTransferRoute")
                .from(betsysSftpPath)
                .to(sftpPath);




//        onException(ValidationException.class)
//                .log("Caught ValidationException")
//                .end();
//
//
//        from(sftpPath)
//                .log("Lest fil med navn: ${header.CamelFileNameOnly}")
//                .log("Body: ${body}")
//                .to("validator:file:pain.001.001.03.xsd")
//                .to(betsysSftpPath)
//               // .bean(sbdhMessage)
//                .to("ref:betsysUt")
//                .end();

    }


    private String getSftpPathWithReadLock(String url, String username, String password) {
        return "sftp://" +
                username +
                "@" +
                url +
                "/outbound" +
                "?password=" +
                password +
                SFTP_OPTIONS;
    }


}
