package com.primeid.daoImpl;

import com.primeid.dao.SessionTableDao;
import com.primeid.model.SessionTable;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Saddam Hussain
 */

@Repository("sessionTableDao")
public class SessionTableDaoImpl implements SessionTableDao{

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public SessionTable findByToken(String token) {

        List list = sessionFactory.getCurrentSession()
                .createQuery("from SessionTable where token=? ORDER BY expiry DESC")
                .setParameter(0, token).list();
        if(list != null && list.size() > 0){
            return (SessionTable) list.get(0);
        }
        return null;
    }

    @Transactional
    public SessionTable findByTokenExpiry(String token) {

        List list = sessionFactory.getCurrentSession()
                .createQuery("from SessionTable where token=? and expiry > NOW()")
                .setParameter(0, token).list();
        if(list != null && list.size() > 0){
            return (SessionTable) list.get(0);
        }
        return null;
    }
    
    @Transactional
    public SessionTable findByAccountID(long accountID) {
        List list = sessionFactory.getCurrentSession()
                .createQuery("from SessionTable where accountID=? and expiry > NOW()")
                .setParameter(0, accountID)
                .list();
        
        if(list != null && list.size() > 0){
            return (SessionTable) list.get(0);
        }
        return null;
    }
    
    @Transactional(readOnly = false)
    public SessionTable save(SessionTable sessionTable) {
        sessionFactory.getCurrentSession().save(sessionTable);
        return findByToken(sessionTable.getToken());
    }
    
    @Transactional(readOnly = false)
    public SessionTable update(SessionTable sessionTable) {
        sessionFactory.getCurrentSession().update(sessionTable);
        return findByToken(sessionTable.getToken());
    }

}