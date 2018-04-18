package no.nav.oko.betsys.agresso.integrasjonskomponent.config;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;

import static no.nav.oko.betsys.agresso.integrasjonskomponent.config.PrometheusLabels.*;

public class PrometheusMetrics {

    private static final String NAMESPACE = "agresso-betsys";

    public static final Gauge isReady = Gauge.build()
            .namespace(NAMESPACE)
            .name("app_is_ready")
            .help("App is ready to recieve traffic")
            .register();

    public static final Counter agressoCounter = Counter.build()
            .namespace(NAMESPACE)
            .name("agresso_total_counter")
            .help("Counts total number of files copied from Agresso to Betsys")
            .labelNames(LABEL_PROCESS, LABEL_EVENT)
            .register();

    public static final Counter betsysCounter = Counter.build()
            .namespace(NAMESPACE)
            .name("betsys_total_counter")
            .help("Counts total number of files copied from Betsys to Agresso")
            .labelNames(LABEL_PROCESS, LABEL_EVENT)
            .register();

    public static final Counter exceptionCounter = Counter.build()
            .namespace(NAMESPACE)
            .name("agresso_exception_total_counter")
            .help("Counts total number of exceptions")
            .labelNames(LABEL_PROCESS, LABEL_ERROR_TYPE, LABEL_EXCEPTION_NAME)
            .register();
}
