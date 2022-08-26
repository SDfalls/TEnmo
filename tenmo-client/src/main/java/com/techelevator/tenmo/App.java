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
        List<Transfer> transfersList = transfersService.getTransferHistory();
        for (Transfer transfers : transfersList) {
            String output = Integer.toString(transfers.getTransfer_id());
            if (transfers.getAccount_from()==currentUserAccount.getAccountId()) {
                output += " To: " + transfers.getAccount_to();
            } else {
                output += " From: " + transfers.getAccount_from();
            }
            output += " $" + transfers.getAmount();
            System.out.println(output);
        }
    }

    private void viewPendingRequests() {
        // TODO Auto-generated method stub

    }

    private void sendBucks() {
//        NEED TO MAKE IT POST A NEW TRANSFER
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
        if (selection > 0 && selection<numbersToSelect.size()) {

            BigDecimal amountToTransfer = consoleService.promptForBigDecimal("Enter the amount to transfer: ");

            Account accountToTransfer =
                    this.accountService.getAccountByUserId(numbersToSelect.get(selection).getId());
            BigDecimal receiverNewBalance = accountToTransfer.getBalance().add(amountToTransfer);
            BigDecimal senderNewBalance = currentUserAccount.getBalance().subtract(amountToTransfer);
            ///IM NOT SURE IF THIS IS NEEDED
//            accountToTransfer.setBalance(newBalance);
            this.transfersService.changeAccountBalance(senderNewBalance,currentUserAccount.getAccountId());

            this.transfersService.changeAccountBalance(receiverNewBalance,accountToTransfer.getAccountId());

            int transferNumber = transfersService.createTransferTransaction(currentUserAccount.getAccountId(),
                    accountToTransfer.getAccountId(),amountToTransfer,"Send","Approved");
            System.out.println("Transaction completed successfully, transfer ID: " + transferNumber);


            selection++;

        }
        if (selection < 0 || selection >= usersList.size()) {
            System.out.println("Invalid selection");
        }
    }

        private void requestBucks() {
            // TODO Auto-generated method stub

        }


}
