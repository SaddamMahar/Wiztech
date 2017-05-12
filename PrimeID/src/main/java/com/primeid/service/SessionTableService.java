package com.primeid.service;

import com.primeid.dao.SessionTableDao;
import com.primeid.model.SessionTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Saddam Hussain
 */
@Service("sessionTableService")
public class SessionTableService {

    @Autowired
    private SessionTableDao sessionTableDao;

//    @Transactional(readOnly = true)
    public SessionTable loadSessionTableByToken(String token) {
        SessionTable sessionTable = null;
        sessionTable = sessionTableDao.findByToken(token);
        System.out.println(sessionTable.getAccounts().getAccountID());
        return sessionTable;
    }

    @Transactional(readOnly = true)
    public SessionTable loadSessionTableByTokenExpiry(String token) {
        SessionTable sessionTable = null;
        sessionTable = sessionTableDao.findByTokenExpiry(token);
        return sessionTable;
    }

    public SessionTable loadSessionTableByAccountID(long accountID) {
        SessionTable sessionObj = null;
        System.out.println("accountID : "+accountID);
        sessionObj = sessionTableDao.findByAccountID(accountID);
        return sessionObj;
    }

    public SessionTable save(SessionTable sessionTable) {
        SessionTable sessionObj = null;
        sessionObj = sessionTableDao.save(sessionTable);
        return sessionObj;
    }

    public SessionTable update(SessionTable sessionTable) {
        SessionTable sessionObj = null;
        sessionObj = sessionTableDao.update(sessionTable);
        return sessionObj;
    }
}
