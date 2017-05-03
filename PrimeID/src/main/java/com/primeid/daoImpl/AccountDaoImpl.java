package com.primeid.daoImpl;

import com.primeid.model.Account;
import com.primeid.dao.AccountDao;
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
@Repository("accountDao")
public class AccountDaoImpl implements AccountDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public Account findByAccountCode(String accountCode) {

        List<Account> users = new ArrayList<Account>();

        try {
            users = sessionFactory.getCurrentSession()
                    .createQuery("from Account where accountCode=?")
                    .setParameter(0, accountCode)
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
