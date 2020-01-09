package no.nav.oko.sbdh;

import no.difi.commons.sbdh.jaxb.StandardBusinessDocumentHeader;
import no.nav.oko.sbdh.generer.OpprettSbdh;
import no.nav.oko.sbdh.generer.SbdhParameters;
import no.nav.oko.sbdh.generer.SbdhType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SbdhService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SbdhService.class);
    private static OpprettSbdh opprettSbdh = new OpprettSbdh();

    private SbdhService(){
        throw new IllegalAccessError("Skal ikke instansieres, utility klasse.");
    }

    public static String opprettStringSBDH(SbdhType sbdhType, String filnavn, String sender, String mottaker) {
        return opprettStringSBDH(new SbdhParameters(sbdhType, filnavn, sender, mottaker));
    }

    public static String opprettStringSBDH(SbdhParameters sbdhParameters) {
        try {
            return opprettSbdh.marshalSbdh(opprettSbdh.genererSbdh(sbdhParameters));
        } catch (Exception e) {
            LOGGER.error("Klarte ikke opprette SBDH. Ingen melding sendt.");
            throw e;
        }
    }

    public static StandardBusinessDocumentHeader opprettSBDH(SbdhType sbdhType, String filnavn, String sender, String mottaker) {
        return opprettSBDH(new SbdhParameters(sbdhType, filnavn, sender, mottaker));
    }

    public static StandardBusinessDocumentHeader opprettSBDH(SbdhParameters sbdhParameters) {
        return opprettSbdh.genererSbdh(sbdhParameters);
    }
}
