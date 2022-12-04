package com.project.ewallet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ewallet.CommonConstants;
import com.project.ewallet.UserIdentifier;
import com.project.ewallet.model.Wallet;
import com.project.ewallet.repository.WalletRepository;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WalletRepository walletRepository;

    @KafkaListener(topics = {CommonConstants.USER_CREATION_TOPIC}, groupId = "group123")
    public void createWallet(String message) throws ParseException {
//        JSONObject data = objectMapper.convertValue(message, JSONObject.class);
        JSONObject data = (JSONObject) new JSONParser().parse(message);
        Long userId = (Long) data.get(CommonConstants.USER_CREATION_TOPIC_USERID);
        String phoneNumber = (String) data.get(CommonConstants.USER_CREATION_TOPIC_PHONE_NUMBER);
        String userIdentifier = (String) data.get(CommonConstants.USER_CREATION_TOPIC_IDENTIFIER_KEY);
        String identifierValue = (String) data.get(CommonConstants.USER_CREATION_TOPIC_IDENTIFIER_VALUE);

        Wallet wallet = Wallet.builder()
                .userId(userId)
                .phoneNumber(phoneNumber)
                .userIdentifier(UserIdentifier.valueOf(userIdentifier))
                .identifierValue(identifierValue)
                .balance(10.0)
                .build();

        walletRepository.save(wallet);
    }

    @KafkaListener(topics = {CommonConstants.TRANSACTION_CREATION_TOPIC}, groupId = "group123")
    public void updatedWalletForTransaction(String message) throws ParseException {
        JSONObject data = (JSONObject) new JSONParser().parse(message);
        String sender = (String) data.get("sender");
        String receiver = (String) data.get("receiver");
        Double amount = (Double) data.get("amount");
        String transactionId = (String) data.get("transactionId");

        Wallet senderWallet = walletRepository.findByPhoneNumber(sender);
        Wallet receiverWallet = walletRepository.findByPhoneNumber(receiver);

        if(senderWallet==null || receiverWallet==null || senderWallet.getBalance()<amount){
            //mark this transaction as failed.
        }

        walletRepository.updateWallet(sender, 0-amount);
        walletRepository.updateWallet(receiver, amount);

        //TODO: produce an event for updating a transaction.
    }
}
