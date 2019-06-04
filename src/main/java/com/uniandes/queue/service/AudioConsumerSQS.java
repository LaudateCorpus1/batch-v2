package com.uniandes.queue.service;

import com.uniandes.configuration.properties.ConfigConstants;
import com.uniandes.encoding.audio.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AudioConsumerSQS {

    private final Consumer consumer;

    public AudioConsumerSQS(Consumer consumer) {
        this.consumer = consumer;
    }

    @SqsListener(ConfigConstants.PROCESSING_QUEUE_NAME)
    public void receiveMessage(String message) {
        log.info("Evidences processing queue - Received: {}", message);
        consumer.audioConvert(message);
    }
}
