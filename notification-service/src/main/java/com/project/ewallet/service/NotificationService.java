package com.project.ewallet.service;

import com.project.ewallet.CommonConstants;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
public class NotificationService {

    @KafkaListener(topics = {CommonConstants.TRANSACTION_COMPLETED_TOPIC}, groupId = "group123")
    public void sendNotification(String message) throws ParseException {
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(message);
    }
}
