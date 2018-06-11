package no.nav.oko.betsys.agresso.integrasjonskomponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class DLQReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(DLQReceiver.class);

    private CountDownLatch latch = new CountDownLatch(1);

    public CountDownLatch getLatch() {
        return latch;
    }

    public void resetCountDownLatch(){
        latch = new CountDownLatch(1);
    }

    @JmsListener(destination = "ActiveMQ.DLQ")
    public void receive(String message) {
        latch.countDown();
        LOGGER.info("received message at time '{}' with payload '{}'",System.currentTimeMillis(),  message);

    }


}