package com.primeid.service;

import com.primeid.dao.AccountJurisdictionDao;
import com.primeid.model.AccountJurisdiction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Saddam Hussain
 */
@Service("accountJurisdictionService")
public class AccountJurisdictionService {

    @Autowired
    private AccountJurisdictionDao accountJurisdictionDao;

    @Transactional(readOnly = true)
    public AccountJurisdiction loadAccountJurisdictionByAccountCode(String accountID) {
        AccountJurisdiction userObj = null;
        try {
            userObj = accountJurisdictionDao.findByAccountID(accountID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (AccountJurisdiction) userObj;
    }

    @Transactional(readOnly = true)
    public AccountJurisdiction loadAccountJurisdictionByJurisdictionID(String jurisdictionID) {
        AccountJurisdiction userObj = null;
        try {
            userObj = accountJurisdictionDao.findByJurisdictionID(jurisdictionID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (AccountJurisdiction) userObj;
    }
}
