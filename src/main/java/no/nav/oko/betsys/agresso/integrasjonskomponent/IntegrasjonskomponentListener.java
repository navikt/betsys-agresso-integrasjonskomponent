//package no.nav.oko.betsys.agresso.integrasjonskomponent;
//
//import io.prometheus.client.hotspot.DefaultExports;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.ApplicationListener;
//import org.springframework.stereotype.Component;
//
//@Component
//public class IntegrasjonskomponentListener implements ApplicationListener<ApplicationReadyEvent> {
//
//    @Override
//    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
//        startPrometheus();
//    }
//
//    private void startPrometheus() {
//        DefaultExports.initialize();
//    }
//}
