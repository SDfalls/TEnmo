package com.techelevator.tenmo;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TransferService;

import java.math.BigDecimal;
import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService(API_BASE_URL);
    private final TransferService transfersService = new TransferService(API_BASE_URL);
    private AuthenticatedUser currentUser;

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
        // TODO Auto-generated method stub
        List<Transfer> transfersList = TransferService.getTransferHistory(AuthenticatedUser currentUser);
        for (Transfer transfers : transfersList) {
            String output = Integer.toString(transfers.getTransfer_id());
            if (transfers.getAccount_from()) {
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
        List<Account> accountList = AccountService.listAccounts(currentUser);

        long currentuserIndex = -1;


        for (int i = 0; i < accountList.size(); i++) {
            if (accountList.get(i).getUserId() == currentUser.getUser().getId()) {
                currentuserIndex = i;
                continue;
            }
            int displayIndex = i + 1;
            if (currentuserIndex != -1) {
                displayIndex--;
            }
            System.out.println(displayIndex + ": " + accountList.get(i).getUserId());
        }
        int selection = consoleService.promptForInt("Select user: ") - 1;
        if (selection >= currentuserIndex) {

            selection++;
        }
        if (selection < 0 || selection >= accountList.size()) {
            System.out.println("Invalid selection");
            return;


        }

        //private void requestBucks() {
            // TODO Auto-generated method stub

        }

    }
}
