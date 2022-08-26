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
    public int createTransferTransaction(int fromAccountId, int toAccountId, BigDecimal amountToSend, String type, String status) {
        int transferNumber = 0;


        try {
           transferNumber = restTemplate.exchange(BASE_URL+"transfer/createTransfer?accountFrom="+ fromAccountId +
                           "&accountTo=" + toAccountId + "&amount=" + amountToSend + "&transferId="+ transferType(type) + "&statusId=" +
                   transferStatus(status), HttpMethod.POST, makeAuthEntity(this.currentUser), Integer.class).getBody();
           return transferNumber;
        } catch (RestClientResponseException e) {
            System.out.println("An error occurred while creating transfer transaction "+e.getMessage());
        }
        return transferNumber;
    }
    public void updateTransferStatus (int transferId, String status) {
        try {
             restTemplate.exchange(BASE_URL+"transfer?transferId="+ transferId + "&transferStatus=" + transferStatus(status),
                     HttpMethod.PUT, makeAuthEntity(this.currentUser), Integer.class);
        } catch (RestClientResponseException e) {
            System.out.println("An error occurred while updating transfer transaction "+e.getMessage());
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

    private int transferType(String type) {
        int typeNumber = 0;
            if (type.equals("Request")) {
                typeNumber = 1;
            } else if (type.equals("Send")) {
                typeNumber = 2;
            }
            return typeNumber;
    }

    private int transferStatus(String status) {
        int statusNumber = 0;
                if(status.equals("Pending")){
                    statusNumber=1;
                } else if (status.equals("Approved")) {
                    statusNumber=2;
                } else if (status.equals("Rejected")) {
                    statusNumber=3;
                }
                return statusNumber;
    }

    private HttpEntity<Void> makeAuthEntity(AuthenticatedUser currentUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return entity;
    }



}




