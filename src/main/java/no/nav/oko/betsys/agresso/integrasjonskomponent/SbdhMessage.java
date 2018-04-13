package no.nav.oko.betsys.agresso.integrasjonskomponent;

import no.difi.commons.sbdh.jaxb.BusinessScope;
import no.difi.commons.sbdh.jaxb.DocumentIdentification;
import no.difi.commons.sbdh.jaxb.Partner;
import no.difi.commons.sbdh.jaxb.PartnerIdentification;
import no.difi.commons.sbdh.jaxb.Scope;
import no.difi.commons.sbdh.jaxb.StandardBusinessDocumentHeader;
import org.apache.camel.Handler;
import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

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

@Component
public class SbdhMessage {

    private static final Logger LOGGER = LoggerFactory.getLogger(SbdhMessage.class);

    private static final String DOCUMENT_SCOPE_TYPE = "DOCUMENTID";

    private static final String DOCUMENT_SCOPE_IDENTIFIER = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.03::Document##urn:fdc:bits.no:2017:iso20022:1.5::03";

    private static final String PROCESSID_SCOPE_TYPE = "PROCESSID";

    private static final String PROCESSID_SCOPE_IDENTIFIER = "urn:fdc:bits.no:2017:profile:01:1.0";

    private static final String DOCUMENT_IDENTIFICATION_STANDARD = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.03";

    private static final String DOCUMENT_IDENTIFICATION_TYPEVERSION = "03";

    private static final String DOCUMENT_IDENTIFICATION_TYPE = "Document";


    private String makeSbdh(String bankId, String filnavn, Long isoMeldingId) {
        StandardBusinessDocumentHeader sbdh = createHeader();
        sbdh.setBusinessScope(createURBusinessScope());
        sbdh.getSender().add(createPartner(null, bankId, true));
        sbdh.getReceiver().add(createPartner(null, bankId, false));
        sbdh.setDocumentIdentification(createURDocumentIdentification(filnavn));

        Jaxb2Marshaller marshallerSBDHTilBetsys = new Jaxb2Marshaller();
        marshallerSBDHTilBetsys.setClassesToBeBound(new Class[]{StandardBusinessDocumentHeader.class});

        LOGGER.info("Sender SBDH for filen {}", sbdh.getDocumentIdentification().getInstanceIdentifier());
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);

        marshallerSBDHTilBetsys.marshal(new JAXBElement<>(new QName("StandardBusinessDocumentHeader"), StandardBusinessDocumentHeader.class, sbdh), result);



        return sw.toString();
    }

    private BusinessScope createURBusinessScope() {
        BusinessScope businessScope = new BusinessScope();

        Map.Entry<String, String> docPair = new AbstractMap.SimpleEntry<>(DOCUMENT_SCOPE_TYPE, DOCUMENT_SCOPE_IDENTIFIER);
        Map.Entry<String, String> proPair = new AbstractMap.SimpleEntry<>(PROCESSID_SCOPE_TYPE, PROCESSID_SCOPE_IDENTIFIER);

        businessScope.getScope().add(createSimpleScope(docPair));
        businessScope.getScope().add(createSimpleScope(proPair));
        return businessScope;
    }
    private DocumentIdentification createURDocumentIdentification(String filNavn) {
        DocumentIdentification docId = new DocumentIdentification();
        docId.setStandard(DOCUMENT_IDENTIFICATION_STANDARD);
        docId.setTypeVersion(DOCUMENT_IDENTIFICATION_TYPEVERSION);
        docId.setType(DOCUMENT_IDENTIFICATION_TYPE);
        docId.setInstanceIdentifier(filNavn);
        docId.setCreationDateAndTime(getNow());
        return docId;

    }
    private StandardBusinessDocumentHeader createHeader() {
        StandardBusinessDocumentHeader header = new StandardBusinessDocumentHeader();
        header.setHeaderVersion("1.0");
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

    private Partner createPartner(String authority, String identifier, boolean sender) {
        Partner partner = new Partner();
        String partnerKundenummer = "testPartnerKundenummer";
        if (sender) {
          //  partnerKundenummer = kundenummerRepository.getKundenummerForSystem(identifier).trim();
        } else {
          //  partnerKundenummer = kundenummerRepository.getOrgnummerForSystem(identifier).trim();
        }
        String knr = "9908:" + partnerKundenummer;

        PartnerIdentification partnerIdentification = new PartnerIdentification();
        partnerIdentification.setAuthority(authority); //todo hardkodet verdi?
        partnerIdentification.setValue(knr);
        partner.setIdentifier(partnerIdentification);
        return partner;
    }





    @Handler
    public  String sendSbdh(@Header("CamelFileNameOnly") String filnavn) {

     return makeSbdh("test", filnavn.replace(".lis",  ""), 123L);

    }
}
