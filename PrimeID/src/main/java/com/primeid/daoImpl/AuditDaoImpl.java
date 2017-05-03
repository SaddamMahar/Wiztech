package com.primeid.daoImpl;

import com.primeid.dao.AuditDao;
import com.primeid.model.Audit;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Saddam Hussain
 */
@Repository("auditDao")
public class AuditDaoImpl implements AuditDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    @Override
    public List<Audit> findByAccountID(long accountCode) {

        List<Audit> audits = new ArrayList<Audit>();

        audits = sessionFactory.getCurrentSession()
                .createQuery("from Audit where accountID=?")
                .setParameter(0, accountCode)
                .list();

        if (audits.size() > 0) {
            return audits;
        } else {
            return null;
        }

    }

    @Transactional(readOnly = false)
    @Override
    public void save(Audit audit) {
        sessionFactory.getCurrentSession().save(audit);
    }
}
