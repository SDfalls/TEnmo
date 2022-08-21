package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class AccountController {

    @Autowired
    private AccountDao accountDao;
    @Autowired
    private UserDao userDao;


    public AccountController(AccountDao accountDAO, UserDao userDAO) {
        this.accountDao = accountDAO;
        this.userDao = userDAO;
    }

    @RequestMapping(path = "balance/{id}", method = RequestMethod.GET)
    public BigDecimal getBalance(@PathVariable int id) {
        BigDecimal balance = accountDao.getBalance(id);
        return balance;
    }

    @RequestMapping(path = "listusers", method = RequestMethod.GET)
    public List<User> listUsers() {
        List<User> users = userDao.findAll();
        return users;
    }
}
