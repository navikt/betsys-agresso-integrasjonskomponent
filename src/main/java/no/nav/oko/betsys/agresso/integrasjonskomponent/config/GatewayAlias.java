package no.nav.oko.betsys.agresso.integrasjonskomponent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "mqgateway01")
public class GatewayAlias {

    private String hostname;
    private String name;
    private int port;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
