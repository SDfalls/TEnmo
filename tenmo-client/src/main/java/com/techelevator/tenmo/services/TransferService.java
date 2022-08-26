package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.sql.SQLOutput;
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
    public List<Transfer> getTransferHistory() {
        List<Transfer> transfersList = null;
        try {
            Transfer[] transfers = restTemplate.exchange(BASE_URL + "/transfer", HttpMethod.GET, makeAuthEntity(currentUser), Transfer[].class).getBody();
            transfersList = Arrays.asList(transfers);
        } catch (RestClientResponseException e) {
            System.out.println("Error getting transfers: " + e.getMessage());
        }
        if (transfersList.size()==0){
            System.out.println("Uh oh, it looks like you have not made any transfer transactions");
        }
        return transfersList;
    }


    public Transfer [] getTransfersByAccountId(int accountId) {
        Transfer[] transfersAccount;
        transfersAccount = restTemplate.exchange(BASE_URL + "transfer/" + accountId, HttpMethod.GET, makeAuthEntity(currentUser), Transfer[].class).getBody();
        return transfersAccount;
    }
    public void createTransferTransaction(int fromAccountId, int toAccountId, BigDecimal amountToSend) {
//        Integer transferId = null;

        Transfer transfer = new Transfer();
        transfer.setAmount(amountToSend);
        transfer.setAccount_from(fromAccountId);
        transfer.setAccount_to(toAccountId);
        // JUST TO SEE WHAT HAPPENS
        transfer.setTransfer_id(1);
        transfer.setTransfer_type_id(1);

        try {
            restTemplate.exchange(BASE_URL+"transfer/createTransfer", HttpMethod.PUT, makeAuthEntity(this.currentUser), Integer.class, transfer);
        } catch (RestClientResponseException e) {
            System.out.println("An error occurred while creating transfer transaction "+e.getMessage());
        }




    }

    public void changeAccountBalance( BigDecimal amount, int accountId) {
        HttpEntity entity = makeAuthEntity(this.currentUser);

        try {
            restTemplate.exchange(BASE_URL +  "transfer/updateBalance?newBalance=" + amount + "&accountId=" + accountId, HttpMethod.PUT, entity, Integer.class);
        } catch (RestClientResponseException e) {
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
    }

    private HttpEntity<Void> makeAuthEntity(AuthenticatedUser currentUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return entity;
    }



}




