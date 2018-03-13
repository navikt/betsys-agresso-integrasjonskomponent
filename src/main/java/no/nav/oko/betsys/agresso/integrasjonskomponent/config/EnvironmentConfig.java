package no.nav.oko.betsys.agresso.integrasjonskomponent.config;

public class EnvironmentConfig {
    public static final String nfsusername;
    public static final String nfsPassword;
    public static final String nfsHost;
    public static final String nfsPort;
    public static final String nfsFilePath;


    static {
        nfsusername = getStringVariable("NFSUSERNAME");
        nfsPassword = getStringVariable("NFSPASSWORD");
        nfsHost = getStringVariable("NFSHOST");
        nfsPort = getStringVariable("NFSPORT");
        nfsFilePath = getStringVariable("NFSFILEPATH");


    }

    public static String getStringVariable(String envVariable) {
        String var = System.getenv(envVariable);
        if (var == null)
            throw new RuntimeException("Missing environment variable: \"" + envVariable + "\"");
        return var;
    }
}
