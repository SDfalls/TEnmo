package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class AccountService {
    private String BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser currentUser;

    public AccountService(String url, AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
        BASE_URL = url;
    }

    public BigDecimal getBalance() {
        BigDecimal balance = new BigDecimal(0);
        try {
            balance = restTemplate.exchange(BASE_URL + "balance/" + currentUser.getUser().getId(), HttpMethod.GET,
                    makeAuthEntity(), BigDecimal.class).getBody();
            System.out.println("Your current account balance is: $" + balance);
        } catch (RestClientException e) {
            System.out.println("Error getting balance");
        }
        return balance;
    }
    public List<User> listUsers(AuthenticatedUser currentUser) {
        List<User> userList = null;
        try {
            User[] accounts = restTemplate.exchange(BASE_URL + "listusers", HttpMethod.GET, makeAuthEntity(),User[].class).getBody();
            userList = Arrays.asList(accounts);
        } catch (RestClientResponseException e) {
            System.out.println("Error getting users: "+e.getMessage());
        }
        return userList;
    }

    public Account getAccountByUserId(int id) {
        Account account = new Account();
        try {
            account = restTemplate.exchange(BASE_URL + "account?user_id="+ id + "&account_id=0", HttpMethod.GET, makeAuthEntity(), Account.class).getBody();
        } catch (RestClientResponseException e) {
            System.err.println("Error getting account");
        }
        if (account==null){
            System.err.println("Error getting account");
        }
        return account;
    }

    public Account getAccountById(int id) {
        Account account = new Account();
        try {
            account = restTemplate.exchange(BASE_URL + "account?user_id=0&account_id="+ id, HttpMethod.GET, makeAuthEntity(), Account.class).getBody();
        } catch (RestClientResponseException e) {
            System.err.println("Error getting account");
        }
        if (account==null){
            System.err.println("Error getting account");
        }
        return account;
    }

    public User getUserById(int id) {
        User user = new User();
        try {
            user = restTemplate.exchange(BASE_URL + "user?user_id="+ id, HttpMethod.GET, makeAuthEntity(), User.class).getBody();
        } catch (RestClientResponseException e) {
            System.err.println("Error getting user");
        }
        return user;
    }




    private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(this.currentUser.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }
}
