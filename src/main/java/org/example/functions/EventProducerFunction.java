package org.example.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.EventHubOutput;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import com.microsoft.azure.functions.kafka.BrokerAuthenticationMode;
import com.microsoft.azure.functions.kafka.BrokerProtocol;
import com.microsoft.azure.functions.kafka.annotation.KafkaOutput;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.function.adapter.azure.AzureSpringBootRequestHandler;

public class EventProducerFunction extends AzureSpringBootRequestHandler<String, String> {
    private static final String KAFKA_TOPIC = System.getenv("KAFKA_TOPIC");
    @FunctionName("producer")
    public String run(
            @TimerTrigger(name = "timerInfo", schedule = "0 0/1 * * * *") String timerInfo,
            // Replace 'topic' value with %KAFKA_TOPIC% after fixing
            // https://github.com/Azure/azure-functions-kafka-extension/issues/158
            @KafkaOutput(topic = "evh-kafka",
                brokerList = "%BROKERS%",
                username = "%KAFKA_USER%",
                password = "%KAFKA_PASSWORD%",
                authenticationMode = BrokerAuthenticationMode.PLAIN,
                protocol = BrokerProtocol.SASLSSL) OutputBinding<String> output,
        final ExecutionContext context
    ) {
        String value = handleRequest(timerInfo, context);
        output.setValue(value);
        return value;
    }
}
