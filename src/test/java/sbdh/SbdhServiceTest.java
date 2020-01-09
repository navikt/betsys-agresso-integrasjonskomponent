package sbdh;

import no.difi.commons.sbdh.jaxb.StandardBusinessDocumentHeader;
import no.nav.oko.sbdh.SbdhService;
import no.nav.oko.sbdh.generer.SbdhParameters;
import no.nav.oko.sbdh.generer.SbdhType;
import no.nav.oko.sbdh.generer.SenderMottaker;
import org.junit.Assert;
import org.junit.Test;

public class SbdhServiceTest {

    @Test
    public void opprettStringSBDH() throws Exception {
        SenderMottaker senderMottaker = new SenderMottaker("kundenummer", "orgnummer");
        SbdhParameters sbdhParameters = new SbdhParameters.SbdhParameterBuilder()
                .setSenderMottaker(senderMottaker)
                .setSbdhType(SbdhType.PAIN001)
                .setFilnavn("filnavn")
                .build();
        StandardBusinessDocumentHeader sbdh = new StandardBusinessDocumentHeader();

        String result = SbdhService.opprettStringSBDH(sbdhParameters);
        Assert.assertNotNull(result);
    }

    @Test
    public void opprettStringSBDH_med_flere_inputparametere() throws Exception {
        SenderMottaker senderMottaker = new SenderMottaker("kundenummers", "orgnummer");
        SbdhParameters sbdhParameters = new SbdhParameters.SbdhParameterBuilder()
                .setSenderMottaker(senderMottaker)
                .setSbdhType(SbdhType.PAIN001)
                .setFilnavn("filnavn2")
                .build();
        StandardBusinessDocumentHeader sbdh = new StandardBusinessDocumentHeader();

        String result = SbdhService.opprettStringSBDH(SbdhType.PAIN001, "filnavn2", "kundenummers", "orgnummer");
        Assert.assertNotNull(result);
    }

    @Test(expected = RuntimeException.class)
    public void opprettStringSBDH_forventerFeil() throws Exception {
        SbdhParameters sbdhParameters = new SbdhParameters.SbdhParameterBuilder().build();

        SbdhService.opprettStringSBDH(sbdhParameters);
    }

    @Test
    public void opprettSBDH() throws Exception {
        SenderMottaker senderMottaker = new SenderMottaker("kundenummer", "orgnummer");
        SbdhParameters sbdhParameters = new SbdhParameters.SbdhParameterBuilder()
                .setSenderMottaker(senderMottaker)
                .setSbdhType(SbdhType.PAIN001)
                .setFilnavn("filnavn")
                .build();

        StandardBusinessDocumentHeader result = SbdhService.opprettSBDH(sbdhParameters);
        Assert.assertNotNull(result);
        Assert.assertEquals("9908:kundenummer", result.getSender().get(0).getIdentifier().getValue());
        Assert.assertEquals("filnavn", result.getDocumentIdentification().getInstanceIdentifier());
    }

    @Test
    public void opprettSBDH_med_flere_inputparametere() throws Exception {
        SenderMottaker senderMottaker = new SenderMottaker("kundenummer3", "orgnummer");
        SbdhParameters sbdhParameters = new SbdhParameters.SbdhParameterBuilder()
                .setSenderMottaker(senderMottaker)
                .setSbdhType(SbdhType.CAMT055)
                .setFilnavn("filnavn3")
                .build();

        StandardBusinessDocumentHeader result = SbdhService.opprettSBDH(SbdhType.CAMT055, "filnavn3", "kundenummer3", "orgnummer");
        Assert.assertNotNull(result);
        Assert.assertEquals("9908:kundenummer3", result.getSender().get(0).getIdentifier().getValue());
        Assert.assertEquals("filnavn3", result.getDocumentIdentification().getInstanceIdentifier());
    }
}
