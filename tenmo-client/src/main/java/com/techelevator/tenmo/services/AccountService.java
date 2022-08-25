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
                    makeAuthEntity(currentUser), BigDecimal.class).getBody();
            System.out.println("Your current account balance is: $" + balance);
        } catch (RestClientException e) {
            System.out.println("Error getting balance");
        }
        return balance;
    }
    public List<User> listUsers(AuthenticatedUser currentUser) {
        List<User> userList = null;
        try {
            User[] accounts = restTemplate.exchange(BASE_URL + "listusers", HttpMethod.GET, makeAuthEntity(currentUser),User[].class).getBody();
            userList = Arrays.asList(accounts);
        } catch (RestClientResponseException e) {
            System.out.println("Error getting users: "+e.getMessage());
        }
        return userList;
    }

    public Account getAccountByUserId(AuthenticatedUser currentUser) {
        Account account = new Account();
        try {
            account = restTemplate.exchange(BASE_URL + "account?user_id="+currentUser.getUser().getId(), HttpMethod.GET, makeAuthEntity(currentUser), Account.class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("Error getting account");
        }
        return account;
    }



    private HttpEntity makeAuthEntity(AuthenticatedUser currentUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(this.currentUser.getToken());
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }
}
