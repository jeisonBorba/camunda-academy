# Concatenation Connector — Camunda 8 Outbound Connector

A custom **outbound** connector built with the Camunda Connector SDK (Classic Function API) that concatenates two string inputs.

## Architecture

This connector uses the **Classic Function API** — a single class implementing `OutboundConnectorFunction` with a dedicated input/output record.

| File | Purpose |
|---|---|
| `ConcatenationConnectorFunction.java` | Main connector logic (implements `OutboundConnectorFunction`) |
| `ConcatenationConnectorRequest.java` | Input record with `input1` and `input2` |
| `ConcatenationConnectorResult.java` | Output record with `concatenationResult` |
| `META-INF/services/io.camunda.connector.api.outbound.OutboundConnectorFunction` | SPI registration |

## How it works

The connector takes two strings (`input1` and `input2`) and returns them concatenated with a space:

```
input1 = "Hello"
input2 = "World"
→ result = "Hello World"
```

## Build

```bash
mvn clean package
```

Produces:
- A thin JAR
- A fat JAR (shaded)
- The regenerated element template under `element-templates/`

## Run locally

```bash
mvn -Dexec.mainClass=io.camunda.example.LocalConnectorRuntime test-compile exec:java
```

## Test

```bash
mvn test
```

Two test classes:
- `ConcatenationFunctionTest` — validates the connector output
- `ConcatenationRequestTest` — validates input constraints (`@NotEmpty` on both fields)

## Element template

Upload `element-templates/my-connector.json` to your Modeler (Desktop or Web) to use the connector in BPMN diagrams.

The template exposes:
- **First text** (`input1`) — required
- **Second text** (`input2`) — required
- **Result Variable** — variable name to store the response

## Connector configuration

| Property | Value |
|---|---|
| Connector type | `io.camunda:concatenation-api:1` |
| Input variables | `input1`, `input2` |
| Annotations | `@OutboundConnector`, `@TemplateProperty`, `@NotEmpty` |
| SDK version | `8.9.1` |
| Java | 21 |
