package io.camunda.example;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.camunda.connector.api.error.ConnectorInputException;
import io.camunda.connector.runtime.test.outbound.OutboundConnectorContextBuilder;
import io.camunda.example.model.ConcatenationConnectorRequest;
import org.junit.jupiter.api.Test;

public class ConcatenationRequestTest {

    String input1, input2;

    @Test
    void shouldFailWhenValidate_NoInput1() throws JsonProcessingException {
        var input = new ConcatenationConnectorRequest(input1,input2);

        var context = OutboundConnectorContextBuilder
            .create()
            .variables(input).build();

        assertThatThrownBy(() -> context.bindVariables(ConcatenationConnectorRequest.class))
            .isInstanceOf(ConnectorInputException.class)
            .hasMessageContaining("input1");
    }

    @Test
    void shouldFailWhenValidate_NoInput2() throws JsonProcessingException {
        var input = new ConcatenationConnectorRequest(input1,input2);

        var context = OutboundConnectorContextBuilder
            .create()
            .variables(input).build();

        assertThatThrownBy(() -> context.bindVariables(ConcatenationConnectorRequest.class))
            .isInstanceOf(ConnectorInputException.class)
            .hasMessageContaining("input2");
    }

}
