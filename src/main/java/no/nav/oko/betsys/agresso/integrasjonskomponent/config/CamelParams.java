package no.nav.oko.betsys.agresso.integrasjonskomponent.config;

import java.util.StringJoiner;

public class CamelParams {
    private StringJoiner joiner = new StringJoiner("&","?","");

    public String getCamelParams(){
        return  joiner.toString();
    }

    public void addParam(String param) {
        if(param.contains("&") || param.contains("?")){
            throw new RuntimeException("Camel params must be inserted one at a time without & or ?");
        }else if(!param.contains("=") || param.indexOf("=") != param.lastIndexOf("=")){
            throw new RuntimeException("Camel params must contain =");
        }else{
            joiner.add(param);
        }
    }
}
