# File Watch Inbound Connector

A Camunda 8 inbound connector that monitors a directory for file system events (create, modify, delete) using Java's `WatchService` and correlates them to process instances.

Based on the [Camunda Connector SDK](https://github.com/camunda/connectors).

# Important concepts

When building an inbound connector, there are several important concepts to understand:

- **Activation:** When a process definition with this connector is deployed. This is a synchronous operation, so long-running tasks should be started asynchronously.
- **[Activation condition](https://docs.camunda.io/docs/components/connectors/use-connectors/#activation-condition):** ⚠️ Do not mistaken with the Activation. An activation condition is a BPMN expression that must evaluate to true for the connector to correlate.
- **Deactivation:** When the process definition is deleted or a new version is deployed. Use this to clean up resources.
- **[Correlation](https://docs.camunda.io/docs/components/connectors/use-connectors/#correlation):** Matches incoming events to waiting process instances using correlation keys.
- **[Deduplication](https://docs.camunda.io/docs/components/connectors/use-connectors/inbound/#connector-deduplication):** Sometimes you might want to have multiple BPMN elements listening to the same event source. For example, you might want to link multiple connector events to the same message queue consumer and activate only one of them based on the message content.
- **[Message ID](https://docs.camunda.io/docs/components/connectors/use-connectors/#message-id-expression):** Used for deduplication - events with the same message ID are processed only once.

For more details, see the [Inbound Connector SDK documentation](https://docs.camunda.io/docs/components/connectors/custom-built-connectors/connector-sdk).

We strongly recommend reading through the [Messages documentation](https://docs.camunda.io/docs/components/concepts/messages/) as Inbound connectors rely heavily on the concepts explained there.

# Connector Overview

An inbound connector that watches a filesystem directory and triggers process instances when files are created, modified, or deleted. The polling interval and event type are configurable.

## Build

You can package the Connector by running the following command:

```bash
mvn clean package
```

This will create the following artifacts:

- A thin JAR without dependencies.
- A fat JAR containing all dependencies, potentially shaded to avoid classpath conflicts. This will not include the SDK
  artifacts since those are in scope `provided` and will be brought along by the respective Connector Runtime executing
  the Connector.
- All element templates

### Shading dependencies

You can use the `maven-shade-plugin` defined in the [Maven configuration](./pom.xml) to relocate common dependencies
that are used in other Connectors and
the [Connector Runtime](https://github.com/camunda/connectors).
This helps to avoid classpath conflicts when the Connector is executed.

For example, without shading, you might encounter errors like:
```
java.lang.NoSuchMethodError: com.fasterxml.jackson.databind.ObjectMapper.setserializationInclusion(Lcom/fasterxml/jackson/annotation/JsonInclude$Include;)Lcom/fasterxml/jackson/databind/ObjectMapper;
```
This occurs when your connector and the runtime use different versions of the same library (e.g., Jackson).

Use the `relocations` configuration in the Maven Shade plugin to define the dependencies that should be shaded.
The [Maven Shade documentation](https://maven.apache.org/plugins/maven-shade-plugin/examples/class-relocation.html)
provides more details on relocations.

## API

### Input

| Name            | Description                                    | Example                        |
|-----------------|------------------------------------------------|--------------------------------|
| eventToMonitor  | File event type to watch                       | `ENTRY_CREATE`                 |
| directory       | Filesystem directory to monitor                | `/tmp/Camunda8`                |
| pollingInterval | Polling interval in seconds                    | `30`                           |

Valid event types: `ENTRY_CREATE`, `ENTRY_MODIFY`, `ENTRY_DELETE`

### Output

```json
{
  "event":{
    "monitoredEvent":"ENTRY_CREATE",
    "directory":"/tmp/Camunda8",
    "fileName":"arquivo.txt"
  }
}
```

## Testing
### Unit and Integration Tests

You can run the unit and integration tests by executing the following Maven command:
```bash
mvn clean verify
```

### Local environment

#### Prerequisites
You will need the following tools installed on your machine:
1. Camunda Modeler, which is available in two variants:
    - [Desktop Modeler](https://camunda.com/download/modeler/) for a local installation.
    - [Web Modeler](https://modeler.camunda.io/) for an online experience.

2. A watched directory (e.g. `/tmp/Camunda8`). Create it with:
   ```shell
   mkdir -p /tmp/Camunda8
   ```
   > **Note for macOS:** Avoid using `~/Downloads` as the watched directory — macOS TCC privacy protections block `WatchService` with `Operation not permitted`.

#### Using Your Connector with SaaS

1. Configure your `src/test/resources/application.properties` with your Camunda SaaS cluster credentials (from Console > API > Create new Client).
2. Start the connector by running `io.camunda.connector.inbound.LocalConnectorRuntime`.
3. In Web Modeler, upload the element templates from `element-templates/` and publish them.
4. Create a new BPMN diagram, add a Start Event or Intermediate Catch Event with the File Watch connector.
5. Configure the connector properties:
   - **Event type:** `ENTRY_CREATE`
   - **Directory:** `/tmp/Camunda8`
   - **Polling interval:** `30`
6. Deploy the process to your Camunda cluster.
7. Create or delete a file in `/tmp/Camunda8`:
   ```shell
   touch /tmp/Camunda8/arquivo.txt
   rm /tmp/Camunda8/arquivo.txt
   ```
8. Verify the process instance in Camunda Operate.

## Element Templates

Element templates for this connector are located in:

- [`element-templates/file_watch_start_connector.json`](./element-templates/file_watch_start_connector.json) — for BPMN Start Events
- [`element-templates/file_watch_intermediate_connector.json`](./element-templates/file_watch_intermediate_connector.json) — for Intermediate Catch Events

Templates can also be auto-generated via the Maven build:

```shell
mvn clean package
```

The generation is driven by the [Element Template Generator](https://github.com/camunda/connectors/tree/main/element-template-generator/core).
