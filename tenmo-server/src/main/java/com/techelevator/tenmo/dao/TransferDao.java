package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import java.util.List;

public interface TransferDao {

   public void send(Transfer transfer);

   public List<Transfer> getAllTransfers();

   public List<Transfer> getAllTransfersByAccountId(int accountId);






}
