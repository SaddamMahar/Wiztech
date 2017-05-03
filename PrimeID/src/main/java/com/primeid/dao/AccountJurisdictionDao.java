package com.primeid.dao;

import com.primeid.model.AccountJurisdiction;

/**
 *
 * @author Saddam Hussain
 */
public interface AccountJurisdictionDao {
    AccountJurisdiction findByAccountID (String accountID);
    AccountJurisdiction findByJurisdictionID (String jurisdictionID);
}
