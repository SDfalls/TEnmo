package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class TransferService {

    private final String BASE_URL;

    private final RestTemplate restTemplate = new RestTemplate();

    public AuthenticatedUser currentUser;

    public TransferService(String url, AuthenticatedUser currentUser){
        this.currentUser = currentUser;
        BASE_URL = url;
    }
    public List<Transfer> getTransferHistory(AuthenticatedUser currentUser) {
        List<Transfer> transfersList = null;
        try {
            Transfer[] transfers = restTemplate.exchange(BASE_URL + "/transfer", HttpMethod.GET, makeAuthEntity(currentUser), Transfer[].class).getBody();
            transfersList = Arrays.asList(transfers);
        } catch (RestClientResponseException e) {
            System.out.println("Error getting transfers: " + e.getMessage());
        }
        return transfersList;
    }


    public Transfer [] getTransfersByAccountId(int accountId) {
        Transfer[] transfersAccount;
        transfersAccount = restTemplate.exchange(BASE_URL + "transfer/" + accountId, HttpMethod.GET, makeAuthEntity(currentUser), Transfer[].class).getBody();
        return transfersAccount;
    }
    public Integer sendTransaction(AuthenticatedUser currentUser, int fromAccountId, int toAccountId, BigDecimal amountToSend) {
        Integer transferId = null;

        Transfer transfers = new Transfer();
        transfers.setAmount(amountToSend);
        transfers.setAccount_from(fromAccountId);
        transfers.setAccount_to(toAccountId);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Transfer> entity = new HttpEntity<>(transfers, headers);

        try {
            transferId = restTemplate.exchange(BASE_URL+"/transfer/send", HttpMethod.POST, entity, Integer.class).getBody();
        } catch (RestClientResponseException e) {
            System.out.println("An error occurred when sending transfer: "+e.getMessage());
        }

        return transferId;


    }
    private HttpEntity<Void> makeAuthEntity(AuthenticatedUser currentUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return entity;
    }



}




