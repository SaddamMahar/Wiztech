package com.primeid.daoImpl;

import com.primeid.dao.JurisdictionDao;
import com.primeid.model.Jurisdiction;
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
@Repository("jurisdictionDao")
public class JurisdictionDaoImpl implements JurisdictionDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public Jurisdiction findByJurisdictionID(long jurisdictionID) {

        List<Jurisdiction> jurisdictions = new ArrayList<Jurisdiction>();

        try {
            jurisdictions = sessionFactory.getCurrentSession()
                    .createQuery("from Jurisdiction where jurisdictionID=?")
                    .setParameter(0, jurisdictionID).list();
            if (jurisdictions.size() > 0) {
                return jurisdictions.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;

    }

    @Override
    public Jurisdiction findByJurisdictionCode(String jurisdictionCode) {
        List<Jurisdiction> jurisdictions = new ArrayList<Jurisdiction>();

        try {
            jurisdictions = sessionFactory.getCurrentSession()
                    .createQuery("from Jurisdiction where jurisdictionCode=?")
                    .setParameter(0, jurisdictionCode)
                    .list();

            if (jurisdictions.size() > 0) {
                return jurisdictions.get(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    public Jurisdiction findByJurisdictionName(String jurisdictionName) {
        List<Jurisdiction> jurisdictions = new ArrayList<Jurisdiction>();

        try {
            jurisdictions = sessionFactory.getCurrentSession()
                    .createQuery("from Jurisdiction where jurisdictionName=?")
                    .setParameter(0, jurisdictionName)
                    .list();

            if (jurisdictions.size() > 0) {
                return jurisdictions.get(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

}
