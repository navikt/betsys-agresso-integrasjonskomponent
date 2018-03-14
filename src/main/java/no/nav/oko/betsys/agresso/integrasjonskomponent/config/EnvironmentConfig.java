package no.nav.oko.betsys.agresso.integrasjonskomponent.config;

public class EnvironmentConfig {
    public static final String NFSUSERNAME;
    public static final String NFSPASSWORD;
    public static final String NFSHOST;
    public static final String NFSPORT;
    public static final String NFSFILEPATH;


    static {
        NFSUSERNAME = getStringVariable("NFSUSERNAME");
        NFSPASSWORD = getStringVariable("NFSPASSWORD");
        NFSHOST = getStringVariable("NFSHOST");
        NFSPORT = getStringVariable("NFSPORT");
        NFSFILEPATH = getStringVariable("NFSFILEPATH");


    }

    private EnvironmentConfig(){}

    private static String getStringVariable(String envVariable) {
        String var = System.getenv(envVariable);
        if (var == null)
            throw new RuntimeException("Missing environment variable: \"" + envVariable + "\"");
        return var;
    }
}
