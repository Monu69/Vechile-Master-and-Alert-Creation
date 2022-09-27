package com.elogist.vehicle_master_and_alert_creation.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.Instant;

@Component
public class AlertKafkaProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlertKafkaProducer.class);



    @Qualifier("atKafkaTemplate")
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;



    @Async("kafkaThreadPoolTaskExecutorNew")
    public void sendMessageToNewKafka(String message, String topic, String key) {
        Instant start = Instant.now();
        try {
            LOGGER.debug("Message being sent to new kafka : {},topic:{}", message,topic);
            ListenableFuture<SendResult<String, String>> send = kafkaTemplate.send(topic, key, message);//As this is new parser, can send deviceid.

            send.addCallback(new ListenableFutureCallback<>() {
                @Override
                public void onSuccess(SendResult<String, String> result) {

                }

                @Override
                public void onFailure(Throwable ex) {
                    LOGGER.error("Failure in sending the message to new kafka : cause : {}   , Message :{}", ex.getCause(), ex.getMessage());
                }
            });
        } catch(Exception exception) {
            LOGGER.error("Exception occurred while sending message to new kafka : {}, trace:{}", exception.getMessage(),exception.getStackTrace());
            //todo message queuing

        }
        LOGGER.debug("Time taken in send message to new kafka : {}", Instant.now().toEpochMilli() - start.toEpochMilli());
    }

}