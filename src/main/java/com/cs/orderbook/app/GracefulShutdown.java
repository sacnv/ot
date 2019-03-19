
package com.cs.orderbook.app;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
public class GracefulShutdown implements TomcatConnectorCustomizer,
        ApplicationListener<ContextClosedEvent> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(GracefulShutdown.class);

    private Connector connector;

    @Bean
    public ConfigurableServletWebServerFactory
            webServerFactory(final GracefulShutdown gracefulShutdown) {
        TomcatServletWebServerFactory factory =
                new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(gracefulShutdown);

        return factory;
    }

    @Override
    public void customize(Connector conn) {
        this.connector = conn;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        this.connector.pause();
        Executor executor = this.connector.getProtocolHandler().getExecutor();

        if (executor instanceof ThreadPoolExecutor) {

            try {

                ThreadPoolExecutor threadPoolExecutor =
                        (ThreadPoolExecutor) executor;
                threadPoolExecutor.shutdown();

                if (!threadPoolExecutor.awaitTermination(30,
                        TimeUnit.SECONDS)) {
                    LOGGER.warn(
                            "Tomcat thread pool did not shut down gracefully "
                                    + "within 30 seconds. Proceeding with "
                                    + "forceful shutdown");
                    }
                } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

        }

    }

}
