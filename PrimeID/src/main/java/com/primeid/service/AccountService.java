package com.primeid.service;

import com.primeid.model.Account;
import com.primeid.dao.AccountDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Saddam Hussain
 */
@Service("accountService")
public class AccountService {

    @Autowired
    private AccountDao accountDao;

    @Transactional(readOnly = true)
    public Account loadAccountByAccountCode(String accountCode) {

        Account userObj = null;
        try {
            userObj = accountDao.findByAccountCode(accountCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (Account) userObj;

    }
}
