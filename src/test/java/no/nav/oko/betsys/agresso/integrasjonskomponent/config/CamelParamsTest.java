package no.nav.oko.betsys.agresso.integrasjonskomponent.config;

import org.junit.Test;

import static org.junit.Assert.*;

public class CamelParamsTest {

    @Test(expected = RuntimeException.class)
    public void checkRequireseEuals(){
        CamelParams params = new CamelParams();
        params.addParam("keyValue");
    }

    @Test(expected = RuntimeException.class)
    public void checkRejectsTwoParams(){
        CamelParams params = new CamelParams();
        params.addParam("key1=value1&key2=value2");
    }

    @Test(expected = RuntimeException.class)
    public void checkRejectsPartialParams(){
        CamelParams params = new CamelParams();
        params.addParam("key1=value1&key2");
    }

    @Test(expected = RuntimeException.class)
    public void checkRejectsAmpersand(){
        CamelParams params = new CamelParams();
        params.addParam("&key=value");
    }

    @Test(expected = RuntimeException.class)
    public void CheckRejectsQuestionMark(){
        CamelParams params = new CamelParams();
        params.addParam("?key=value");
    }

    @Test
    public void CheckStartsWithQuestionmark(){
        CamelParams params = new CamelParams();
        params.addParam("key=value");
        assertEquals("?key=value", params.getCamelParams());
    }

    @Test
    public void checkAddsAmpersand(){
        CamelParams params = new CamelParams();
        params.addParam("key1=value1");
        params.addParam("key2=value2");
        assertEquals("?key1=value1&key2=value2", params.getCamelParams());
    }
}