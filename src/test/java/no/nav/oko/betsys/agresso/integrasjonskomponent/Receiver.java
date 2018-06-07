package no.nav.oko.betsys.agresso.integrasjonskomponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class Receiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);

    private CountDownLatch latch = new CountDownLatch(1);

    public CountDownLatch getLatch() {
        return latch;
    }

    public void resetCountDownLatch(){
        latch = new CountDownLatch(1);
    }

    @JmsListener(destination = "${SENDING_TIL_BANK_QUEUE}")
    public void receive(String message) {
        LOGGER.info("received message='{}'", message);
        latch.countDown();
    }
}