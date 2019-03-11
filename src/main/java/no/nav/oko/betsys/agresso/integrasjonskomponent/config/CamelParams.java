package no.nav.oko.betsys.agresso.integrasjonskomponent.config;

import java.util.StringJoiner;

class CamelParams {
    private StringJoiner joiner = new StringJoiner("&", "?", "");

    String getCamelParams() {
        return joiner.toString();
    }

    void addParam(String param) {
        if (param.contains("&") || param.contains("?")) {
            throw new RuntimeException("Camel params must be inserted one at a time without & or ?");
        } else if (!param.contains("=") || param.indexOf("=") != param.lastIndexOf("=")) {
            throw new RuntimeException("Camel params must contain = and be inserted one at a time");
        } else {
            joiner.add(param);
        }
    }
}
