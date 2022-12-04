package com.project.ewallet.service;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransactionService implements UserDetailsService {
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

    public String initiateTransaction(String username, String receiver, String purpose, String amount) {
        log.info("Inside TransactionService: sender {}, receiver {}, purpose {}, amount {}", username, receiver, purpose, amount);
        return null;
    }
}
