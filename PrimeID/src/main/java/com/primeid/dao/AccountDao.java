package com.primeid.dao;

import com.primeid.model.Account;

/**
 *
 * @author Saddam Hussain
 */
public interface AccountDao {
    Account findByAccountCode (String accountCode);
}
