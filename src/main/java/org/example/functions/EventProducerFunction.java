package org.example.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.EventHubOutput;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import org.springframework.cloud.function.adapter.azure.AzureSpringBootRequestHandler;

public class EventProducerFunction extends AzureSpringBootRequestHandler<String, String> {
    @FunctionName("producer")
    @EventHubOutput(name = "event", eventHubName = "%KAFKA_TOPIC%", connection = "KAFKA_PASSWORD")
    public String run(
        @TimerTrigger(name = "timerInfo", schedule = "0 0/1 * * * *") String timerInfo,
        final ExecutionContext context
    ) {
        return handleRequest(timerInfo, context);
    }
}
