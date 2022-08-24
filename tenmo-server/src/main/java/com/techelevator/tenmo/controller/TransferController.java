package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RequestMapping("/transfer")
@RestController

public class TransferController {

    private TransferDao TransferDao;


    public TransferController(TransferDao transfer){this.TransferDao = transfer;}

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public void createTransfer(@RequestBody Transfer transfer){TransferDao.send(transfer);}


    @RequestMapping(method = RequestMethod.GET)
    public List<Transfer> listAllTransfers(){
        return TransferDao.getAllTransfers();
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public List<Transfer>ListAllTransfersByID(@PathVariable int id){
        return TransferDao.getAllTransfersByAccountId(id);
    }





}
