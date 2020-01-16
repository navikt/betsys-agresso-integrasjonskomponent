package no.nav.oko.betsys.agresso.integrasjonskomponent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamelSftpConfig {

    @Value("${agressoSftpUser}")
    private String agressoSftpUser;

    @Value("${agressoKeyPassphrase}")
    private String agressoKeyPassphrase;

    @Value("${agressoTilBetsysRouteAgressoSftpPath}")
    private String agressoTilBetsysRouteAgressoSftpPath;

    @Value("${agressoTilBetsysRouteBetsysSftpPath}")
    private String agressoTilBetsysRouteBetsysSftpPath;

    @Value("${betsysTilAgressoRouteAgressoSftpPath}")
    private String betsysTilAgressoRouteAgressoSftpPath;

    @Value("${betsysTilAgressoRouteBetsysSftpPath}")
    private String betsysTilAgressoRouteBetsysSftpPath;

    @Value("${betsysSftpUser}")
    private String betsysSftpUser;

    @Value("${betsysKeyPassphrase}")
    private String betsysKeyPassphrase;

    @Value("${vaultPath}")
    private String vaultPath;

    public String agressoTilBetsysAgressoSftp() {
        String agressoOutbound = "sftp://" + agressoSftpUser + "@" + agressoTilBetsysRouteAgressoSftpPath;
        CamelParams agressoParams = new CamelParams();
        setupSecurity(agressoParams);
        agressoParams.addParam("initialDelay=15000");
        agressoParams.addParam("maxMessagesPerPoll=1");
        agressoParams.addParam("delay=15000");
        agressoParams.addParam("delete=true");
        agressoParams.addParam("readLock=changed");
        agressoParams.addParam("bridgeErrorHandler=true");
        agressoParams.addParam("stepwise=false");
        return agressoOutbound + agressoParams.getCamelParams();
    }

    public String agressoTilBetsysBetsysSftp() {
        String betsysOutbound = "sftp://" + betsysSftpUser + "@" +agressoTilBetsysRouteBetsysSftpPath;
        CamelParams betsysParams = new CamelParams();
        setupSecurity(betsysParams);
        betsysParams.addParam("throwExceptionOnConnectFailed=true");
        betsysParams.addParam("stepwise=false");
        return betsysOutbound + betsysParams.getCamelParams();
    }

    public String betsysTilAgressoAgressoSftp() {
        String agressoInbound = "sftp://" + agressoSftpUser + "@" + betsysTilAgressoRouteAgressoSftpPath;
        CamelParams agressoParams = new CamelParams();
        setupSecurity(agressoParams);
        return agressoInbound + agressoParams.getCamelParams();
    }

    public String betsysTilAgressoBetsysSftp() {
        String betsysInbound = "sftp://" + betsysSftpUser + "@" +betsysTilAgressoRouteBetsysSftpPath;
        CamelParams betsysParams = new CamelParams();
        setupSecurity(betsysParams);
        betsysParams.addParam("throwExceptionOnConnectFailed=true");
        return betsysInbound + betsysParams.getCamelParams();
    }

    public String sftpHealthCheckRouteAgressoSftp() {
        String agressoOutbound = "sftp://" + agressoSftpUser + "@" + agressoTilBetsysRouteAgressoSftpPath + "/.health";
        CamelParams agressoParams = new CamelParams();
        setupSecurity(agressoParams);
        agressoParams.addParam("maxMessagesPerPoll=1");
        agressoParams.addParam("throwExceptionOnConnectFailed=true");
        agressoParams.addParam("delete=true");
        return agressoOutbound + agressoParams.getCamelParams();
    }

    public String sftpHealthCheckRouteBetsysSftp() {
        String betsysOutbound = "sftp://" + betsysSftpUser + "@" + agressoTilBetsysRouteBetsysSftpPath +"/.health";
        CamelParams betsysParams = new CamelParams();
        setupSecurity(betsysParams);
        betsysParams.addParam("throwExceptionOnConnectFailed=true");
        betsysParams.addParam("maxMessagesPerPoll=1");
        betsysParams.addParam("delay=5m");
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