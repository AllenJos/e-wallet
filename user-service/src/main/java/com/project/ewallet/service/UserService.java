package com.project.ewallet.service;

import com.project.ewallet.configuration.CacheConfig;
import com.project.ewallet.model.User;
import com.project.ewallet.repository.CacheRepository;
import com.project.ewallet.repository.UserRepository;
import com.project.ewallet.request.UserCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CacheRepository cacheRepository;

    @Override
    public User loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        User user = cacheRepository.get(phoneNumber);
        if(user==null){
            user = userRepository.findByPhoneNumber(phoneNumber);
            if(user!=null)
                cacheRepository.set(user);
        }

        return user;
    }


    public void create(UserCreateRequest userCreateRequest) {
        User user = userCreateRequest.to();
        user.setPassword(encryptPassword(user.getPassword()));

        userRepository.save(user);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public String encryptPassword(String rawPassword){
        return passwordEncoder.encode(rawPassword);
    }
}
