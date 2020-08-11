package org.example.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.kafka.BrokerAuthenticationMode;
import com.microsoft.azure.functions.kafka.BrokerProtocol;
import com.microsoft.azure.functions.kafka.annotation.KafkaTrigger;
import org.springframework.cloud.function.adapter.azure.AzureSpringBootRequestHandler;

public class EventConsumerFunction extends AzureSpringBootRequestHandler<String, String> {
    @FunctionName("receiver")
    public String run(
            @KafkaTrigger(topic = "%KAFKA_TOPIC%",
                    brokerList = "%BROKERS%",
                    username = "%KAFKA_USER%",
                    password = "%KAFKA_PASSWORD%",
                    authenticationMode = BrokerAuthenticationMode.PLAIN,
                    protocol = BrokerProtocol.SASLSSL,
                    sslCaLocation = "cacert.pem",
                    consumerGroup = "$Default")
                    String kafkaEventData,
            ExecutionContext context) {
        context.getLogger().info(String.format("[Basic] Received message %s", kafkaEventData));
        return handleRequest(kafkaEventData, context);
    }
}
