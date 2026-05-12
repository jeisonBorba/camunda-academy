package io.camunda.connector.inbound;

public record MyConnectorProperties(
    String eventToMonitor,
    String directory,
    String pollingInterval
) {

  public MyConnectorProperties {
    if (eventToMonitor == null || eventToMonitor.isBlank()) {
      throw new IllegalArgumentException("eventToMonitor must not be null or blank");
    }

    if (directory == null || directory.isBlank()) {
      throw new IllegalArgumentException("directory must not be null or blank");
    }

    if (pollingInterval == null) {
      throw new IllegalArgumentException("pollingInterval must not be null");
    }

    try {
      Integer.parseInt(pollingInterval);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("pollingInterval must be a valid integer");
    }
  }
}
