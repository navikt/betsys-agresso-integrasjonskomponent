package sbdh.generer;

import no.difi.commons.sbdh.jaxb.StandardBusinessDocumentHeader;

import no.nav.oko.sbdh.generer.OpprettSbdh;
import no.nav.oko.sbdh.generer.SbdhParameters;
import no.nav.oko.sbdh.generer.SbdhType;
import no.nav.oko.sbdh.generer.SenderMottaker;
import org.junit.Assert;
import org.junit.Test;

public class OpprettSbdhTest {

    private OpprettSbdh opprettSbdh = new OpprettSbdh();

    @Test
    public void genererSbdh_parameter_konstruktor() throws Exception {
        SbdhParameters sbdhParameters = new SbdhParameters(SbdhType.PAIN001, "filnavn", "kundenummer", "orgnummer");

        StandardBusinessDocumentHeader result = opprettSbdh.genererSbdh(sbdhParameters);
        Assert.assertNotNull(result);
    }

    @Test
    public void genererSbdhPain001() throws Exception {
        SenderMottaker senderMottaker = new SenderMottaker("kundenummer", "orgnummer");
        SbdhParameters sbdhParameters = new SbdhParameters.SbdhParameterBuilder()
                .setSenderMottaker(senderMottaker)
                .setSbdhType(SbdhType.PAIN001)
                .setFilnavn("filnavn")
                .build();

        StandardBusinessDocumentHeader result = opprettSbdh.genererSbdh(sbdhParameters);
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.getBusinessScope().getScope().size());
        Assert.assertEquals(result.getBusinessScope().getScope().get(1).getInstanceIdentifier(), "urn:fdc:bits.no:2017:profile:01:1.0");
    }

    @Test
    public void genererSbdhCamt005() throws Exception {
        SenderMottaker senderMottaker = new SenderMottaker("kundenummer", "orgnummer");
        SbdhParameters sbdhParameters = new SbdhParameters.SbdhParameterBuilder()
                .setSenderMottaker(senderMottaker)
                .setSbdhType(SbdhType.CAMT055)
                .setFilnavn("filnavn")
                .build();

        StandardBusinessDocumentHeader result = opprettSbdh.genererSbdh(sbdhParameters);
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.getBusinessScope().getScope().size());
        Assert.assertEquals(result.getBusinessScope().getScope().get(1).getInstanceIdentifier(), "urn:fdc:bits.no:2017:profile:02:1.0");
    }

    @Test
    public void marshalSbdh() throws Exception {
        SenderMottaker senderMottaker = new SenderMottaker("kundenummer", "orgnummer");
        SbdhParameters sbdhParameters = new SbdhParameters.SbdhParameterBuilder()
                .setSenderMottaker(senderMottaker)
                .setSbdhType(SbdhType.PAIN001)
                .setFilnavn("filnavn")
                .build();

        StandardBusinessDocumentHeader sbdh = opprettSbdh.genererSbdh(sbdhParameters);
        String result = opprettSbdh.marshalSbdh(sbdh);
        Assert.assertNotNull(result);
    }
}
