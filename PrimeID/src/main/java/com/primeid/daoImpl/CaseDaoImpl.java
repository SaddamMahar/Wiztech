package com.primeid.daoImpl;

import com.primeid.dao.CaseDao;
import com.primeid.model.Case;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Saddam Hussain
 */
@Repository("caseRecordDao")
public class CaseDaoImpl implements CaseDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public Case findByAccountID(long accountID) {
        List list = sessionFactory.getCurrentSession()
                .createQuery("from Case where accountID=? ORDER BY created DESC")
                .setParameter(0, accountID)
                .list();
        if (list != null && list.size() > 0) {
            return (Case) list.get(0);
        }
        return null;
    }

    @Transactional
    public Case findByCaseID(long caseID) {
        List list = sessionFactory.getCurrentSession()
                .createQuery("from Case where caseID=?")
                .setParameter(0, caseID)
                .list();
        if (list != null && list.size() > 0) {
            return (Case) list.get(0);
        }
        return null;
    }

    @Transactional(readOnly = false)
    public Case save(Case caseRecord) {
        sessionFactory.getCurrentSession().save(caseRecord);
        return findByCaseID(caseRecord.getCaseID());
    }

    @Transactional(readOnly = false)
    public Case update(Case caseRecord) {
        sessionFactory.getCurrentSession().update(caseRecord);
        return findByAccountID(caseRecord.getAccounts().getAccountID());
    }
}
