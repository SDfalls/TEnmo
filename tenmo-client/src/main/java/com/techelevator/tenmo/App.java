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

    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService(API_BASE_URL, currentUser);
    private final TransferService transfersService = new TransferService(API_BASE_URL,currentUser);

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
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
        AccountService as = new AccountService(API_BASE_URL, currentUser);
        try {
            as.getBalance();
        } catch (NullPointerException e) {
            System.out.println("No balance found");
        }
    }

    private void viewTransferHistory() {
//         TODO Auto-generated method stub
//        List<Transfer> transfersList = transfersService.getTransferHistory(currentUser);
//        for (Transfer transfers : transfersList) {
//            String output = Integer.toString(transfers.getTransfer_id());
//            if (transfers.getAccount_from()) {
//                output += " To: " + transfers.getAccount_to();
//            } else {
//                output += " From: " + transfers.getAccount_from();
//            }
//            output += " $" + transfers.getAmount();
//            System.out.println(output);
//        }
    }

    private void viewPendingRequests() {
        // TODO Auto-generated method stub

    }

    private void sendBucks() {
        AccountService as = new AccountService(API_BASE_URL, currentUser);
        TransferService transferService = new TransferService(API_BASE_URL,currentUser);
        List<User> usersList = as.listUsers(currentUser);

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
        int selection = consoleService.promptForInt("Select user: ") - 1;
        if (selection >= currentuserIndex) {

            BigDecimal amountToTransfer = consoleService.promptForBigDecimal("Enter the amount to transfer: ");
            Account account = as.getAccountByUserId(numbersToSelect.get(selection);
            transferService.changeAccountBalance(currentUser,amountToTransfer,account.getAccountId());
//            transferService.sendTransaction(currentUser,as.getAccountByUserId(currentUser.getUser().getId()).getAccountId(), selection,amountToTransfer);

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
