package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;

@RequestMapping("/transfer")
@RestController
public class TransferController {

    @Autowired
    private TransferDao transferDao;

    @Autowired
    private AccountDao accountDao;


    public TransferController(TransferDao transfer){this.transferDao = transfer;}

//    @ResponseStatus(HttpStatus.CREATED)
//    @RequestMapping(path = "/createTransfer", method = RequestMethod.POST)
//    public int createTransfer(@RequestParam int accountFrom, int accountTo, BigDecimal amount, int transferId, int statusId ){
//        Transfer transfer = new Transfer();
//        transfer.setAccount_from(accountFrom);
//        transfer.setAccount_to(accountTo);
//        transfer.setAmount(amount);
//        transfer.setTransfer_type_id(transferId);
//        transfer.setTransfer_status_id(statusId);
//        return transferDao.createTransfer(transfer);
//    }
@ResponseStatus(HttpStatus.CREATED)
@RequestMapping(path = "/createTransfer", method = RequestMethod.POST)
public int createTransfer(@RequestBody Transfer transfer ){
    return transferDao.createTransfer(transfer);
}

    @RequestMapping(method = RequestMethod.GET)
    public List<Transfer> listAllTransfers(){
        return transferDao.getAllTransfers();
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public List<Transfer>ListAllTransfersByID(@PathVariable int id){
        return transferDao.getAllTransfersByAccountId(id);
    }
    @RequestMapping(method = RequestMethod.PUT)
    public void updateStatus(@RequestParam int transferId, int transferStatus) {
        transferDao.updateStatus(transferId,transferStatus);
    }
    @RequestMapping(path = "updateBalance", method = RequestMethod.PUT)
    public void updateBalance(@RequestParam BigDecimal newBalance, int accountId) {
        accountDao.updateBalance(newBalance,accountId);
    }



    @RequestMapping(path = "substractFromBalance", method = RequestMethod.PUT)
    public BigDecimal substractBalance(@RequestParam BigDecimal amountToSubstract, int accountId) {
        return accountDao.subtractFromBalance(amountToSubstract,accountId);
    }




}
