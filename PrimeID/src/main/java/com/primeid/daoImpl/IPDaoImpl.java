package com.primeid.daoImpl;

import com.primeid.dao.IPDao;
import com.primeid.model.IP;
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
@Repository("ipDao")
public class IPDaoImpl implements IPDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public IP findByIP(String ip) {

        List<IP> ips = new ArrayList<IP>();
        try {
            ips = sessionFactory.getCurrentSession()
                    .createQuery("from IP where ip=?")
                    .setParameter(0, ip)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ips.size() > 0) {
            return ips.get(0);
        } else {
            return null;
        }

    }
}
