package io.camunda.connector.inbound.subscription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class WatchServiceSubscription {

    private static final Logger LOG = LoggerFactory.getLogger(WatchServiceSubscription.class);

    private final String eventToMonitor;
    private final String directory;
    private final int pollingInterval;
    private final Consumer<WatchServiceSubscriptionEvent> callback;
    private volatile boolean running;
    private WatchService watchService;

    public WatchServiceSubscription(String eventToMonitor, String directory, String pollingInterval, Consumer<WatchServiceSubscriptionEvent> callback) {
        this.eventToMonitor = eventToMonitor;
        this.directory = directory;
        this.pollingInterval = Integer.parseInt(pollingInterval);
        this.callback = callback;
        this.running = true;
    }

    public void start() {
        LOG.info("Activating WatcherService subscription");
        try {
            watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(directory);

            path.register(watchService,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY);

            while (running) {
                WatchKey key = watchService.poll(pollingInterval, TimeUnit.SECONDS);
                if (key != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        LOG.info("Event kind : {} - File : {} event to monitor: {}", event.kind(), event.context(), eventToMonitor);
                        if (event.kind().toString().equals(eventToMonitor)) {
                            WatchServiceSubscriptionEvent wsse = new WatchServiceSubscriptionEvent(eventToMonitor, directory, event.context().toString());
                            callback.accept(wsse);
                        }
                    }
                    key.reset();
                } else {
                    LOG.info("No files during interval");
                }
            }
        } catch (Exception e) {
            LOG.error("Problem with connector", e);
        } finally {
            close();
        }
    }

    public void stop() {
        running = false;
        close();
    }

    private void close() {
        if (watchService != null) {
            try {
                watchService.close();
            } catch (IOException e) {
                LOG.warn("Error closing watch service", e);
            }
        }
    }
}
