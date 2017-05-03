package com.primeid.service;

import com.primeid.dao.JurisdictionDao;
import com.primeid.model.Jurisdiction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Saddam Hussain
 */
@Service("jurisdictionService")
public class JurisdictionService {

    @Autowired
    private JurisdictionDao jurisdictionDao;

    @Transactional(readOnly = true)
    public Jurisdiction loadJurisdictionByJurisdictionCode(String jurisdictionCode) {

        Jurisdiction userObj = null;
        try {
            userObj = jurisdictionDao.findByJurisdictionCode(jurisdictionCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (Jurisdiction) userObj;

    }

    @Transactional(readOnly = true)
    public Jurisdiction loadJurisdictionByJurisdictionID(long jurisdictionID) {

        Jurisdiction userObj = null;
        try {
            userObj = jurisdictionDao.findByJurisdictionID(jurisdictionID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (Jurisdiction) userObj;

    }

    @Transactional(readOnly = true)
    public Jurisdiction loadJurisdictionByJurisdictionName(String jurisdictionName) {

        Jurisdiction userObj = null;
        try {
            userObj = jurisdictionDao.findByJurisdictionName(jurisdictionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (Jurisdiction) userObj;

    }
}
