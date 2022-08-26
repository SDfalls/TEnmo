package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.ArrayList;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;


@Component
public class JdbcTransferDao implements TransferDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void createTransfer(Transfer transfer) {
        String sql = "INSERT INTO transfer (transfer_type_id,"
                + " transfer_status_id, account_from, account_to , amount)"
                + " VALUES (?, ?, ?, ?, ?)";
      jdbcTemplate.update(sql, transfer.getTransfer_type_id(), transfer.getAccount_from(), transfer.getAccount_to(), transfer.getAmount(), transfer.getTransfer_status_id());

    }

    @Override
    public List<Transfer> getAllTransfers() {

        String sql = "SELECT * FROM transfer;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);

        List<Transfer> transfers = new ArrayList<>();
        while(results.next()){
            Transfer transfer = mapRowToTransfer(results);
            transfers.add(transfer);
        }

        return transfers;
    }

    @Override
    public List<Transfer> getAllTransfersByAccountId(int accountId) {

        String sql = "SELECT * FROM transfer WHERE (account_from = ? OR account_to = ?)";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, accountId);

        List<Transfer> transfers = new ArrayList<>();
        while(results.next()){
            Transfer transfer = mapRowToTransfer(results);
            transfers.add(transfer);
        }

        return transfers;
    }


private Transfer mapRowToTransfer(SqlRowSet rowSet){
        Transfer transfer = new Transfer();
        transfer.setTransfer_id(rowSet.getInt("transfer_id"));
        transfer.setAccount_from(rowSet.getInt("account_from"));
        transfer.setAccount_to(rowSet.getInt("account_to"));
        transfer.setTransfer_status_id(rowSet.getInt("transfer_status_id"));
        transfer.setTransfer_type_id(rowSet.getInt("transfer_type_id"));
        transfer.setAmount(rowSet.getBigDecimal("amount"));
        return transfer;
}


}
