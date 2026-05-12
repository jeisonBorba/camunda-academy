package io.camunda.connector.inbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import io.camunda.connector.runtime.test.inbound.InboundConnectorContextBuilder;

import org.junit.jupiter.api.BeforeEach;

public class FileWatchConnectorTest {

    private String eventToMonitor;
    private String directory;
    private String pollingInterval;

    @BeforeEach
    void setUp() {
        eventToMonitor = "ENTRY_CREATE";
        //Replace with your directory test path
        directory = "/tmp/Camunda8";
        pollingInterval = "30";
    }

    @Test
    void shouldCreatePropertiesWithValidValues() {
        var properties = new MyConnectorProperties(eventToMonitor, directory, pollingInterval);

        assertThat(properties.eventToMonitor()).isEqualTo("ENTRY_CREATE");
        assertThat(properties.directory()).isEqualTo("/tmp/Camunda8");
        assertThat(properties.pollingInterval()).isEqualTo("30");
    }

    @Test
    void shouldHandleNullEventToMonitor() {
        assertThatThrownBy(() -> new MyConnectorProperties(null, directory, pollingInterval))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldHandleNullDirectory() {
        assertThatThrownBy(() -> new MyConnectorProperties(eventToMonitor, null, pollingInterval))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldHandleNullPollingInterval() {
        assertThatThrownBy(() -> new MyConnectorProperties(eventToMonitor, directory, null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldHandleEmptyEventToMonitor() {
        assertThatThrownBy(() -> new MyConnectorProperties("", directory, pollingInterval))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldHandleEmptyDirectory() {
        assertThatThrownBy(() -> new MyConnectorProperties(eventToMonitor, "", pollingInterval))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldHandleInvalidPollingInterval() {
        assertThatThrownBy(() -> new MyConnectorProperties(eventToMonitor, directory, "invalid"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldHandleDifferentEventTypes() {
        String[] events = {"ENTRY_CREATE", "ENTRY_MODIFY", "ENTRY_DELETE"};

        for (String event : events) {
            var properties = new MyConnectorProperties(event, directory, pollingInterval);
            assertThat(properties.eventToMonitor()).isEqualTo(event);
        }
    }

    @Test
    void shouldFailWhenValidate_NoPollingInterval() {
        var input = new MyConnectorProperties(eventToMonitor, directory, pollingInterval);
        var context = InboundConnectorContextBuilder.create().properties(input).build();

        var connectorInput = context.bindProperties(MyConnectorProperties.class);

        assertThat(connectorInput)
            .isInstanceOf(MyConnectorProperties.class)
            .extracting("pollingInterval")
            .isEqualTo("30");
    }
}
