package no.nav.oko.sbdh.generer;

public class SbdhParameters {

    private String filnavn;
    private SenderMottaker senderMottaker;
    private SbdhType sbdhType;

    public SbdhParameters(SbdhType sbdhType, String filnavn, String sender, String mottaker) {
        this.filnavn = filnavn;
        this.senderMottaker = new SenderMottaker(sender,mottaker);
        this.sbdhType = sbdhType;
    }

    public SbdhParameters(SbdhParameterBuilder builder) {
        filnavn = builder.filnavn;
        sbdhType = builder.sbdhType;
        senderMottaker = builder.senderMottaker;
    }

    String getFilnavn() {
        return filnavn;
    }

    SbdhType getSbdhType() {
        return sbdhType;
    }

    SenderMottaker getSenderMottaker() {
        return senderMottaker;
    }

    public static class SbdhParameterBuilder {
        private String filnavn;
        private SenderMottaker senderMottaker;
        private SbdhType sbdhType;

        /**
         * Navnet på filen som det sendes SBDH for
         *
         * @param filnavn uten suffix. F.eks ikke 'fil.xml', men kun 'fil'.
         */
        public SbdhParameterBuilder setFilnavn(String filnavn) {
            this.filnavn = filnavn;
            return this;
        }

        /**
         * Hvilken meldingstype SBDH skal sendes for
         *
         * @param senderMottaker P001 eller CAMT055
         */
        public SbdhParameterBuilder setSenderMottaker(SenderMottaker senderMottaker) {
            this.senderMottaker = senderMottaker;
            return this;
        }

        /**
         * Hvilken meldingstype SBDH skal sendes for
         *
         * @param sbdhType P001 eller CAMT055
         */
        public SbdhParameterBuilder setSbdhType(SbdhType sbdhType) {
            this.sbdhType = sbdhType;
            return this;
        }

        public SbdhParameters build() {
            return new SbdhParameters(this);
        }
    }
}
