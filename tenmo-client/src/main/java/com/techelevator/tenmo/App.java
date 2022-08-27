package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TransferService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private AuthenticatedUser currentUser;
    private Account currentUserAccount;
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private AccountService accountService;
    private TransferService transfersService;


    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            accountService = new AccountService(API_BASE_URL, currentUser);
            transfersService = new TransferService(API_BASE_URL,currentUser);
            currentUserAccount = accountService.getAccountByUserId(currentUser.getUser().getId());
            mainMenu();

        }
    }

    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

//    private void transferMenu() {
//
//    }

    private void viewCurrentBalance() {
        // TODO Auto-generated method stub
        try {
            accountService.getBalance();
        } catch (NullPointerException e) {
            System.out.println("No balance found");
        }
    }

    private void viewTransferHistory() {
//         TODO Auto-generated method stub
        //ADD view by transfer Id

        List<Transfer> transfersList = transfersService.getTransfersByAccountId(currentUserAccount.getAccountId());
        for (Transfer transfers : transfersList) {
            String output = "";
            if (transfers.getAccount_from()==currentUserAccount.getAccountId()&& transfers.getTransfer_status_id()!=1) {
                output += "Transfer ID: " + transfers.getTransfer_id();
                output += " To: "
                        + accountService.getUserById(accountService.getAccountById((transfers.getAccount_to())).getUserId()).getUsername();
            } else if (transfers.getAccount_to()==currentUserAccount.getAccountId()){
                output += "Transfer ID: " + transfers.getTransfer_id();
                output += " From: " + accountService.getUserById(accountService.getAccountById((transfers.getAccount_from())).getUserId()).getUsername();
            }else{
                continue;
            }
            output += " Amount: $" + transfers.getAmount();
            System.out.println(output);
        }
    }

    private void viewPendingRequests() {
        // TODO Auto-generated method stub
        //SAME ISSUE AS WITH TRANSFER HISTORY
        List<Transfer> transfersList = transfersService.getTransferHistory();
        Map<Integer, Transfer> pendingRequests = new HashMap<>();
        System.out.println("Pending Requests: ");
        int i=1;
        for (Transfer transfers : transfersList) {
            String output = i + ": ";
            if (transfers.getAccount_to() == currentUserAccount.getAccountId() && transfers.getTransfer_status_id() == 1) {
                output += "Transfer ID: " + transfers.getTransfer_id();
                output += " From: " + accountService.getUserById(accountService.getAccountById((transfers.getAccount_from())).getUserId()).getUsername();
            }else{
                continue;
            }
            output += " Amount: $" + transfers.getAmount();
            System.out.println(output);
            pendingRequests.put(i,transfers);
            i++;
        }
        if(pendingRequests.size()>0) {
            String updateRequest = consoleService.promptForString("Would you like to update any request? (Yes/No) :");
            if (updateRequest.equalsIgnoreCase("Yes")) {
                int transferToUpdate = consoleService.promptForInt("Please choose a transfer: ");

                System.out.println("1: Accept \n2: Reject");
                int transferStatus = consoleService.promptForInt("Please choose an option: ");
                String tStatusString = "";

                if (transferStatus == 1) {
                    tStatusString += "Approved";
                } else if (transferStatus == 2) {
                    tStatusString += "Rejected";
                } else {
                    System.out.println("Invalid Selection");
                }
                if (!tStatusString.isEmpty()) {
                    //ONLY ACCEPT IF ACCOUNT HAS ENOUGH FUNDS
                    Transfer transfer = pendingRequests.get(transferToUpdate);
                    transfersService.updateTransferStatus(transfer.getTransfer_id(), tStatusString);
                    System.out.println("Transaction completed! Transfer has been " + tStatusString + " successfully");
                }
            }
        }else{
            System.out.println("You have no pending requests");
        }
    }

    private void sendBucks() {
        List<User> usersList = this.accountService.listUsers(currentUser);
        long currentuserIndex = -1;
        Map<Integer, User> numbersToSelect = new HashMap<>();



        for (int i = 0; i < usersList.size(); i++) {
            if (usersList.get(i).getId() == currentUser.getUser().getId()) {
                currentuserIndex = i;
                continue;
            }
            int displayIndex = i + 1;
            if (currentuserIndex != -1) {
                displayIndex--;
            }
            numbersToSelect.put(displayIndex,usersList.get(i));
            System.out.println(displayIndex + ": " + usersList.get(i).getUsername());
        }
        int selection = consoleService.promptForInt("Select user: ") ;
        if (selection > 0 && selection<=numbersToSelect.size()) {

            BigDecimal amountToTransfer = consoleService.promptForBigDecimal("Enter the amount to transfer: ");
            //Checking if balance is greater or equal to amount to transfer
            if ((currentUserAccount.getBalance().compareTo(amountToTransfer))>= 0 && amountToTransfer.compareTo(BigDecimal.valueOf(0))>0) {

                Account accountToTransfer =
                        this.accountService.getAccountByUserId(numbersToSelect.get(selection).getId());
                BigDecimal receiverNewBalance = accountToTransfer.getBalance().add(amountToTransfer);
                BigDecimal senderNewBalance = currentUserAccount.getBalance().subtract(amountToTransfer);
                //Changing the balance of the current user locally, this will prevent them from going over their balance to transfer while the app is open.
                accountToTransfer.setBalance(senderNewBalance);
                this.transfersService.changeAccountBalance(senderNewBalance, currentUserAccount.getAccountId());

                this.transfersService.changeAccountBalance(receiverNewBalance, accountToTransfer.getAccountId());

                //sendingBucks will always be approved
                int transferNumber = transfersService.createTransferTransaction(currentUserAccount.getAccountId(),
                        accountToTransfer.getAccountId(), amountToTransfer, "Send", "Approved");
                System.out.println("Transaction completed successfully, transfer ID: " + transferNumber);
            } else if( amountToTransfer.compareTo(BigDecimal.valueOf(0))<=0){
                System.out.println("Amount should be greater than zero");

            }else {
                System.out.println("Sorry you do not posses enough funds.");
            }



        }else{
            consoleService.printInvalidSelection();
        }
    }

        private void requestBucks() {
            // TODO Auto-generated method stub
            List<User> usersList = this.accountService.listUsers(currentUser);
            long currentuserIndex = -1;
            Map<Integer, User> numbersToSelect = new HashMap<>();

            for (int i = 0; i < usersList.size(); i++) {
                if (usersList.get(i).getId() == currentUser.getUser().getId()) {
                    currentuserIndex = i;
                    continue;
                }
                int displayIndex = i + 1;
                if (currentuserIndex != -1) {
                    displayIndex--;
                }
                numbersToSelect.put(displayIndex,usersList.get(i));
                System.out.println(displayIndex + ": " + usersList.get(i).getUsername());
            }
            int selection = consoleService.promptForInt("Select user: ") ;
            if (selection > 0 && selection<=numbersToSelect.size()) {

                BigDecimal transferAmount = consoleService.promptForBigDecimal("Enter the amount to request: ");
                if( transferAmount.compareTo(BigDecimal.valueOf(0))>0) {
                    Account accountToTransfer =
                            this.accountService.getAccountByUserId(numbersToSelect.get(selection).getId());


                    int transferNumber = transfersService.createTransferTransaction(currentUserAccount.getAccountId(),
                            accountToTransfer.getAccountId(), transferAmount, "Request", "Pending");
                    System.out.println("Transaction request completed successfully, transfer ID: " + transferNumber);
                }else {
                    System.out.println("Amount should be greater than zero");
                }

            }else {
                consoleService.printInvalidSelection();
            }

        }


}
