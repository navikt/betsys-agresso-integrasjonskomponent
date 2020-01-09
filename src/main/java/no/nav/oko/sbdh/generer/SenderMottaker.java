package no.nav.oko.sbdh.generer;

public class SenderMottaker {

    private String kundenummer;
    private String orgnummer;


    public SenderMottaker(String kundenummer, String orgnummer) {
        this.kundenummer = kundenummer;
        this.orgnummer = orgnummer;
    }

    public String getKundenummer() {
        return kundenummer;
    }

    public String getOrgnummer() {
        return orgnummer;
    }
}
