package com.example.streambridgetest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.function.cloudevent.CloudEventMessageBuilder;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.MimeTypeUtils;


@SpringBootTest
class StreamBridgeTestApplicationTests {

    @Autowired
    private OutputDestination outputDestination;

    @Autowired
    private StreamBridge streamBridge;


    @Test
    void checkIfHeaderAreSet_CloudEvents() {
        String payload = "test payload";


        Message<String> message = CloudEventMessageBuilder.withData(payload)
                .setType("CustomType")
                .setSource("CustomSource")
                .setSubject("CustomSubject")
                .build();

        streamBridge.send("channel-out-0", message);

        Message<byte[]> messageReceived = outputDestination.receive(1000, "channel-out-0");


        String payloadReceived = new String(messageReceived.getPayload());
        Assertions.assertEquals(payloadReceived, payload);

        MessageHeaders headers = messageReceived.getHeaders();

        Assertions.assertEquals(headers.get("ce_type"), "CustomType");
        Assertions.assertEquals(headers.get("ce_source"), "CustomSource");
        Assertions.assertEquals(headers.get("ce_subject"), "CustomSubject");
    }
}
