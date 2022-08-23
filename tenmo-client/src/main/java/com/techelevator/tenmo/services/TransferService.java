package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class TransferService {

    private final String BASE_URL;

    private final RestTemplate restTemplate = new RestTemplate();

    public AuthenticatedUser currentUser;

    public TransferService(String url, AuthenticatedUser currentUser){
        this.currentUser = currentUser;
        BASE_URL = url;
    }

    public Transfer [] getTransfersByAccountId(int accountId){
       Transfer[] transfersAccount;
       transfersAccount = restTemplate.exchange(BASE_URL + "transfer/" + accountId, HttpMethod.GET, makeTAuthEntity(), Transfer[].class).getBody();
       return transfersAccount;



    }
    private HttpEntity makeTAuthEntity() {
        HttpHeaders headers = new HttpHeaders();;
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity entity = new HttpEntity<>(headers);

        return entity;
    }

}




