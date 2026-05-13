package io.camunda.example;

import static org.assertj.core.api.Assertions.assertThat;

import io.camunda.connector.runtime.test.outbound.OutboundConnectorContextBuilder;
import io.camunda.example.model.ConcatenationConnectorRequest;
import io.camunda.example.model.ConcatenationConnectorResult;
import org.junit.jupiter.api.Test;

public class ConcatenationFunctionTest {

    @Test
    void shouldReturnExpectedResultWhenExecute() throws Exception {
        var input = new ConcatenationConnectorRequest("my_input1_value","my_input2_value");

        var function = new ConcatenationConnectorFunction();
        var context = OutboundConnectorContextBuilder.create()
            .variables(input)
            .build();

        var result = function.execute(context);

        assertThat(result)
            .isInstanceOf(ConcatenationConnectorResult.class)
            .extracting("concatenationResult")
            .isEqualTo("my_input1_value my_input2_value");
    }

}
