package com.primeid.daoImpl;

import com.primeid.dao.AccountJurisdictionDao;
import com.primeid.model.AccountJurisdiction;
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
@Repository("accountJurisdictionDao")
public class AccountJurisdictionDaoImpl implements AccountJurisdictionDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public AccountJurisdiction findByAccountID(String accountID) {

        List<AccountJurisdiction> users = new ArrayList<AccountJurisdiction>();

        try {
            users = sessionFactory.getCurrentSession()
                    .createQuery("from AccountJurisdiction where accountID=?")
                    .setParameter(0, accountID)
                    .list();

            if (users.size() > 0) {
                return users.get(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;

    }

    public AccountJurisdiction findByJurisdictionID(String jurisdictionID) {

        List<AccountJurisdiction> users = new ArrayList<AccountJurisdiction>();

        try {
            users = sessionFactory.getCurrentSession()
                    .createQuery("from AccountJurisdiction where jurisdictionID=?")
                    .setParameter(0, jurisdictionID)
                    .list();

            if (users.size() > 0) {
                return users.get(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;

    }
}
