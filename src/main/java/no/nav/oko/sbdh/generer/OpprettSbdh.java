package no.nav.oko.sbdh.generer;

import no.difi.commons.sbdh.jaxb.BusinessScope;
import no.difi.commons.sbdh.jaxb.DocumentIdentification;
import no.difi.commons.sbdh.jaxb.Partner;
import no.difi.commons.sbdh.jaxb.PartnerIdentification;
import no.difi.commons.sbdh.jaxb.Scope;
import no.difi.commons.sbdh.jaxb.StandardBusinessDocumentHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.AbstractMap;
import java.util.GregorianCalendar;
import java.util.Map;

public class OpprettSbdh {

    private static final String KUNDENUMMER_PREFIX = "9908:";
    private static final String HEADER_VERSION = "1.0";
    private static final Logger LOGGER = LoggerFactory.getLogger(OpprettSbdh.class);
    private static final String DOCUMENT_SCOPE_TYPE = "DOCUMENTID";
    private static final String PROCESSID_SCOPE_TYPE = "PROCESSID";
    private static final String DOCUMENT_IDENTIFICATION_TYPE = "Document";
    private static final String AUTHORITY = "iso6523-actorid-upis";

    public StandardBusinessDocumentHeader genererSbdh(SbdhParameters sbdhParameters) {
        StandardBusinessDocumentHeader sbdh = createHeader();
        sbdh.setBusinessScope(createURBusinessScope(sbdhParameters));
        sbdh.getSender().add(createPartner(sbdhParameters.getSenderMottaker().getKundenummer()));
        sbdh.getReceiver().add(createPartner(sbdhParameters.getSenderMottaker().getOrgnummer()));
        sbdh.setDocumentIdentification(createURDocumentIdentification(sbdhParameters));
        return sbdh;
    }

    public String marshalSbdh(StandardBusinessDocumentHeader sbdh) {
        Jaxb2Marshaller marshallerSBDHTilBetsys = new Jaxb2Marshaller();
        marshallerSBDHTilBetsys.setClassesToBeBound(StandardBusinessDocumentHeader.class);

        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);

        marshallerSBDHTilBetsys.marshal(new JAXBElement<>(new QName("StandardBusinessDocumentHeader"), StandardBusinessDocumentHeader.class, sbdh), result);

        return sw.toString();
    }

    private BusinessScope createURBusinessScope(SbdhParameters sbdhParameters) {
        BusinessScope businessScope = new BusinessScope();

        Map.Entry<String, String> docPair = new AbstractMap.SimpleEntry<>(DOCUMENT_SCOPE_TYPE, sbdhParameters.getSbdhType().getDocumentScopeId());
        Map.Entry<String, String> proPair = new AbstractMap.SimpleEntry<>(PROCESSID_SCOPE_TYPE, sbdhParameters.getSbdhType().getProssessIdScopeIdentitfier());

        businessScope.getScope().add(createSimpleScope(docPair));
        businessScope.getScope().add(createSimpleScope(proPair));
        return businessScope;
    }

    private DocumentIdentification createURDocumentIdentification(SbdhParameters sbdhParameters) {
        DocumentIdentification docId = new DocumentIdentification();
        docId.setStandard(sbdhParameters.getSbdhType().getDocumentIdStandard());
        docId.setTypeVersion(sbdhParameters.getSbdhType().getDocumentIdTypeversion());
        docId.setType(DOCUMENT_IDENTIFICATION_TYPE);
        docId.setInstanceIdentifier(sbdhParameters.getFilnavn());
        docId.setCreationDateAndTime(getNow());
        return docId;
    }

    private StandardBusinessDocumentHeader createHeader() {
        StandardBusinessDocumentHeader header = new StandardBusinessDocumentHeader();
        header.setHeaderVersion(HEADER_VERSION);
        return header;
    }

    private XMLGregorianCalendar getNow() {
        try {
            DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
            return datatypeFactory
                    .newXMLGregorianCalendar(new GregorianCalendar());
        } catch (DatatypeConfigurationException dce) {
            LOGGER.info("Kan ikke opprette XMLGregorianCalendar", dce);
            return null;
        }
    }

    private Scope createSimpleScope(Map.Entry<String, String> pair) {
        Scope scope = new Scope();
        scope.setType(pair.getKey());
        scope.setInstanceIdentifier(pair.getValue());
        return scope;
    }

    private Partner createPartner(String partnerKundenummer) {
        Partner partner = new Partner();
        String knr = KUNDENUMMER_PREFIX + partnerKundenummer;

        PartnerIdentification partnerIdentification = new PartnerIdentification();
        partnerIdentification.setAuthority(AUTHORITY);
        partnerIdentification.setValue(knr);
        partner.setIdentifier(partnerIdentification);
        return partner;
    }
}
