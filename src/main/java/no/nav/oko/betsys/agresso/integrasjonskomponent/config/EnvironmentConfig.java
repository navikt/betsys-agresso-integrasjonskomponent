package no.nav.oko.betsys.agresso.integrasjonskomponent.config;

public class EnvironmentConfig {
    public static final String NFSUSERNAME;
    public static final String NFSPASSWORD;
    public static final String NFSHOST;
    public static final String NFSPORT;
    public static final String NFSFILEPATH;
    public static final String DFSUSERNAME;
    public static final String DFSPASSWORD;
    public static final String DFSDOMAIN;



    static {
        NFSUSERNAME = getStringVariable("NFSUSERNAME");
        NFSPASSWORD = getStringVariable("NFSPASSWORD");
        NFSHOST = getStringVariable("NFSHOST");
        NFSPORT = getStringVariable("NFSPORT");
        NFSFILEPATH = getStringVariable("NFSFILEPATH");
        DFSUSERNAME = getStringVariable("DFSUSERNAME");
        DFSPASSWORD = getStringVariable("DFSPASSWORD");
        DFSDOMAIN = getStringVariable("DFSDOMAIN");
    }

    private EnvironmentConfig(){}

    private static String getStringVariable(String envVariable) {
        String var = System.getenv(envVariable);
        if (var == null)
            throw new RuntimeException("Missing environment variable: \"" + envVariable + "\"");
        return var;
    }
}
