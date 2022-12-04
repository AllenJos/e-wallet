package com.project.ewallet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ewallet.CommonConstants;
import com.project.ewallet.model.Transaction;
import com.project.ewallet.model.TransactionStatus;
import com.project.ewallet.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransactionService implements UserDetailsService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //somehow this method have to return UserDetails.

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth("txn_service", "txn123");
        HttpEntity request = new HttpEntity<>(httpHeaders);

        JSONObject requestedUser = restTemplate
                .exchange("http://localhost:6001/admin/user/"+username, HttpMethod.GET,request, JSONObject.class)
                .getBody();
//        JSONObject requestedUser = entity.getBody();
        List<LinkedHashMap<String, String>> requestedAuthorities =
                (List<LinkedHashMap<String, String>>) requestedUser.get("authorities");
        List<SimpleGrantedAuthority> authorities = requestedAuthorities.stream()
                .map(x-> x.get("authority"))
                .map(x-> new SimpleGrantedAuthority(x))
                .collect(Collectors.toList());
        return new User((String)requestedUser.get("username"), (String)requestedUser.get("password"), authorities);
    }

    public String initiateTransaction(String sender, String receiver, String purpose, String amount) throws JsonProcessingException {

        Transaction transaction = Transaction.builder()
                .sender(sender)
                .receiver(receiver)
                .purpose(purpose)
                .transactionId(UUID.randomUUID().toString())
                .amount(amount)
                .transactionStatus(TransactionStatus.PENDING)
                .build();

        transactionRepository.save(transaction);

        //TODO: publish kafka event.
        ObjectMapper objectMapper = new ObjectMapper();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sender", transaction.getSender());
        jsonObject.put("receiver", transaction.getReceiver());
        jsonObject.put("amount", transaction.getAmount());
        jsonObject.put("transactionId", transaction.getTransactionId());

        kafkaTemplate.send(CommonConstants.TRANSACTION_CREATION_TOPIC, objectMapper.writeValueAsString(jsonObject));

        return transaction.getTransactionId();
    }
}
