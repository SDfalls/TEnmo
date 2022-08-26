package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import java.util.List;

public interface TransferDao {

   public int createTransfer(Transfer transfer);

   public List<Transfer> getAllTransfers();

   public List<Transfer> getAllTransfersByAccountId(int accountId);


    void updateStatus(int transferId, int transferStatus);
}
