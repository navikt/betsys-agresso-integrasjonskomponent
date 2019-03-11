package no.nav.oko.betsys.agresso.integrasjonskomponent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamelSftpConfig {

    @Value("${agressoSftpUser}")
    private String agressoSftpUser;

    @Value("${agressoKeyPassphrase}")
    private String agressoKeyPassphrase;

    @Value("${agressoSftpPath}")
    private String agressoSftpPath;

    @Value("${betsysSftpPath}")
    private String betsysSftpPath;

    @Value("${betsysSftpUser}")
    private String betsysSftpUser;

    @Value("${betsysKeyPassphrase}")
    private String betsysKeyPassphrase;

    @Value("${vaultPath}")
    private String vaultPath;

    public String agressoTilBetsysAgressoSftp() {
        String agressoOutbound = "sftp://" + agressoSftpUser + "@" + agressoSftpPath + "/outbound";
        CamelParams agressoParams = new CamelParams();
        setupSecurity(agressoParams);
        agressoParams.addParam("initialDelay=15000");
        agressoParams.addParam("maxMessagesPerPoll=1");
        agressoParams.addParam("delay=15000");
        agressoParams.addParam("move=Arkiv");
        agressoParams.addParam("readLock=changed");
        agressoParams.addParam("bridgeErrorHandler=true");
        agressoParams.addParam("stepwise=false");
        return agressoOutbound + agressoParams.getCamelParams();
    }

    public String agressoTilBetsysBetsysSftp() {
        String betsysOutbound = "sftp://" + betsysSftpUser + "@" + betsysSftpPath + "/outbound";
        CamelParams betsysParams = new CamelParams();
        setupSecurity(betsysParams);
        betsysParams.addParam("throwExceptionOnConnectFailed=true");
        betsysParams.addParam("stepwise=false");
        return betsysOutbound + betsysParams.getCamelParams();
    }

    public String betsysTilAgressoAgressoSftp(){
        String  agressoInbound ="sftp://" + agressoSftpUser + "@" + agressoSftpPath + "/inbound";
        CamelParams agressoParams = new CamelParams();
        setupSecurity(agressoParams);
        return agressoInbound + agressoParams.getCamelParams();
    }

    public String betsysTilAgressoBetsysSftp(){
        String betsysInbound = "sftp://" + betsysSftpUser + "@" + betsysSftpPath + "/inbound";
        CamelParams betsysParams = new CamelParams();
        setupSecurity(betsysParams);
        betsysParams.addParam("throwExceptionOnConnectFailed=true");
        return betsysInbound + betsysParams.getCamelParams();
    }

    public String sftpHealthCheckRouteAgressoSftp() {
        String agressoOutbound = "sftp://" + agressoSftpUser + "@" + agressoSftpPath + "/outbound/.health";
        CamelParams agressoParams = new CamelParams();
        setupSecurity(agressoParams);
        agressoParams.addParam("maxMessagesPerPoll=1");
        agressoParams.addParam("throwExceptionOnConnectFailed=true");
        agressoParams.addParam("delete=true");
        return agressoOutbound + agressoParams.getCamelParams();
    }

    public String sftpHealthCheckRouteBetsysSftp() {
        String betsysOutbound = "sftp://" + betsysSftpUser + "@" + betsysSftpPath + "/outbound/.health";
        CamelParams betsysParams = new CamelParams();
        setupSecurity(betsysParams);
        betsysParams.addParam("throwExceptionOnConnectFailed=true");
        betsysParams.addParam("maxMessagesPerPoll=1");
        betsysParams.addParam("delay=1m");
        betsysParams.addParam("delete=true");
        return betsysOutbound + betsysParams.getCamelParams();
    }

    private void setupSecurity(CamelParams camelParams) {
        camelParams.addParam("strictHostKeyChecking=yes");
        camelParams.addParam("knownHostsFile=" + vaultPath + "/known_hosts");
        camelParams.addParam("privateKeyFile=" + vaultPath + "/betsysKey");
        camelParams.addParam("privateKeyPassphrase=" + agressoKeyPassphrase);
    }
}