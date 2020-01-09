package no.nav.oko.sbdh.generer;

public enum SbdhType {
    PAIN001("urn:iso:std:iso:20022:tech:xsd:pain.001.001.03::Document##urn:fdc:bits.no:2017:iso20022:1.5::03",
            "urn:iso:std:iso:20022:tech:xsd:pain.001.001.03",
            "03",
            "urn:fdc:bits.no:2017:profile:01:1.0"),
    CAMT055("urn:iso:std:iso:20022:tech:xsd:camt.055.001.01::Document##urn:fdc:bits.no:2017:iso20022:1.5::01",
            "urn:iso:std:iso:20022:tech:xsd:camt.055.001.01",
            "01",
            "urn:fdc:bits.no:2017:profile:02:1.0");

    String documentScopeId;
    String documentIdStandard;
    String documentIdTypeversion;
    String prossessIdScopeIdentitfier;


    SbdhType(String documentScopeId, String documentIdStandard, String documentIdTypeversion,String prossessIdScopeIdentitfier) {
        this.documentScopeId = documentScopeId;
        this.documentIdStandard = documentIdStandard;
        this.documentIdTypeversion = documentIdTypeversion;
        this.prossessIdScopeIdentitfier = prossessIdScopeIdentitfier;
    }

    public String getDocumentScopeId() {
        return documentScopeId;
    }

    public String getDocumentIdStandard() {
        return documentIdStandard;
    }

    public String getDocumentIdTypeversion() {
        return documentIdTypeversion;
    }

    public String getProssessIdScopeIdentitfier() {
        return prossessIdScopeIdentitfier;
    }
}
