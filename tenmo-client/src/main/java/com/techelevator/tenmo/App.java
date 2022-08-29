package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TransferService;
import com.techelevator.util.TableGenerator;

import java.math.BigDecimal;
import java.util.*;

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
            } else if (menuSelection== 6) {
                getTransferByTransferID();

            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

    private void getTransferByTransferID() {
        int transferId = consoleService.promptForInt("Enter Transfer ID: ");
        List<Transfer> transfers = new ArrayList<>();
        Transfer transfer = transfersService.getTransferById(transferId);
//        TableGenerator tableGenerator = new TableGenerator();
//        tableGenerator.transferTableGenerator(transfers,currentUserAccount);

        System.out.println("Transfer ID:" + transfer.getTransfer_id() + " From : " + transfer.getAccount_from()
        + "To: " + transfer.getAccount_to() + "Amount: " + transfer.getAmount());


    }

    private void updateRequestsMenu(Map<Integer,Transfer> transfers) {
        int menuSelection = -1;
        while (menuSelection!=2) {
            consoleService.printYesAndNoMenu("Would you like to update any request?");
            menuSelection = consoleService.promptForInt("Please choose an option: ");
            if (menuSelection == 1 ) {
                int transferToUpdate = consoleService.promptForInt("Please choose a transfer from the list: ");
                if (transfers.get(transferToUpdate) != null) {
                    consoleService.printAcceptAndRejectMenu("Would you like to accept or reject this transaction?");
                    int acceptOrReject = consoleService.promptForInt("Please choose an option: ");

                    String transferStatus = "";

                    if (acceptOrReject == 1) {
                        transferStatus += "Approved";
                    } else if (acceptOrReject == 2) {
                        transferStatus += "Rejected";
                    } else {
                        consoleService.printInvalidSelection();
                    }
                    if (!transferStatus.isEmpty()) {
                        Transfer transfer = transfers.get(transferToUpdate);
                        if (transfer.getAmount().compareTo(currentUserAccount.getBalance()) <= 0 && transferStatus.equals("Approved")) {
                            transfersService.updateTransferStatus(transfer.getTransfer_id(), transferStatus);
                            System.out.println("Transaction completed! Transfer has been " + transferStatus + " successfully");
                        } else if (transferStatus.equals("Rejected")) {
                            transfersService.updateTransferStatus(transfer.getTransfer_id(), transferStatus);
                            System.out.println("Transaction completed! Transfer has been " + transferStatus + " successfully");
                        } else {
                            System.out.println("I am sorry but your funds are insufficient to accept this transfer");
                        }
                    }
                }
            } else if (menuSelection==2) {
                System.out.println("No requests will be updated now.");
            } else {
                consoleService.printInvalidSelection();

            }
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
        //ADD view by transfer Id

        List<Transfer> transfersList = transfersService.getTransfersByAccountId(currentUserAccount.getAccountId());
        List<Transfer> actualTransfers = new ArrayList<>();
        for (Transfer transfers : transfersList) {
//            String output = "";
            if (transfers.getAccount_from()==currentUserAccount.getAccountId()&& transfers.getTransfer_status_id()!=1) {
//                output += "Transfer ID: " + transfers.getTransfer_id();
//                output += " To: "
//                        + accountService.getUserById(accountService.getAccountById((transfers.getAccount_to())).getUserId()).getUsername();
                actualTransfers.add(transfers);
            } else if (transfers.getAccount_to()==currentUserAccount.getAccountId()){
//                output += "Transfer ID: " + transfers.getTransfer_id();
//                output += " From: " + accountService.getUserById(accountService.getAccountById((transfers.getAccount_from())).getUserId()).getUsername();
                actualTransfers.add(transfers);
            }else{
                continue;
            }
//            output += " Amount: $" + transfers.getAmount();
//            System.out.println(output);

        }
        TableGenerator tableGenerator = new TableGenerator();
        tableGenerator.transferTableGenerator(actualTransfers,currentUserAccount);
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
//            System.out.println(output);
            pendingRequests.put(i,transfers);
            i++;
        }
        TableGenerator tableGenerator = new TableGenerator();
        List<Transfer> newList = new ArrayList<>(pendingRequests.values());
        tableGenerator.transferTableGenerator(newList,currentUserAccount);
        if(pendingRequests.size()>0) {
            updateRequestsMenu(pendingRequests);
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
                currentUserAccount.setBalance(senderNewBalance);
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
