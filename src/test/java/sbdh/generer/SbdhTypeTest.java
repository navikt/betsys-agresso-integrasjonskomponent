package sbdh.generer;


import no.nav.oko.sbdh.generer.SbdhType;
import org.junit.Assert;
import org.junit.Test;

public class SbdhTypeTest {

    @Test
    public void getDocumentScopeId_camt055() throws Exception {
        String expResult = "urn:iso:std:iso:20022:tech:xsd:camt.055.001.01::Document##urn:fdc:bits.no:2017:iso20022:1.5::01";
        String result = SbdhType.CAMT055.getDocumentScopeId();
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void getDocumentScopeId_Pain001() throws Exception {
        String expResult = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.03::Document##urn:fdc:bits.no:2017:iso20022:1.5::03";
        String result = SbdhType.PAIN001.getDocumentScopeId();
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void getDocumentIdStandard_camt055() throws Exception {
        String expResult = "urn:iso:std:iso:20022:tech:xsd:camt.055.001.01";
        String result = SbdhType.CAMT055.getDocumentIdStandard();
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void getDocumentIdStandard_Pain001() throws Exception {
        String expResult = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.03";
        String result = SbdhType.PAIN001.getDocumentIdStandard();
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void getDocumentIdTypeversion_camt055() throws Exception {
        String expResult = "01";
        String result = SbdhType.CAMT055.getDocumentIdTypeversion();
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void getDocumentIdTypeversion_Pain001() throws Exception {
        String expResult = "03";
        String result = SbdhType.PAIN001.getDocumentIdTypeversion();
        Assert.assertEquals(expResult, result);
    }
}
