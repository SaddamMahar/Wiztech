package com.primeid.service;

import com.primeid.dao.CaseDao;
import com.primeid.model.Case;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Saddam Hussain
 */
@Service("caseService")
public class CaseService {

    @Autowired
    private CaseDao caseDao;

    @Transactional(readOnly = true)
    public Case loadCaseByAccountID(long accountID) {
        Case caseRecord = null;
        caseRecord = caseDao.findByAccountID(accountID);
        return caseRecord;
    }
    @Transactional(readOnly = true)
    public Case loadCaseByCaseID(long caseID) {
        Case caseRecord = null;
        caseRecord = caseDao.findByCaseID(caseID);
        return caseRecord;
    }

    public Case save(Case caseObj) {
        return caseDao.save(caseObj);
    }

    public void update(Case caseObj) {
        caseDao.update(caseObj);
    }
}
