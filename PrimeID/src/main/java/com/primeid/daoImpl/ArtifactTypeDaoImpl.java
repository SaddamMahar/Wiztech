package com.primeid.daoImpl;

import com.primeid.dao.ArtifactTypeDao;
import com.primeid.model.ArtifactType;
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
@Repository("artifactTypeDao")
public class ArtifactTypeDaoImpl implements ArtifactTypeDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    @Override
    public ArtifactType findByArtifactTypeID(long artifactTypeID) {

        List<ArtifactType> audits = new ArrayList<ArtifactType>();

        audits = sessionFactory.getCurrentSession()
                .createQuery("from ArtifactType where artifactTypeID=?")
                .setParameter(0, artifactTypeID)
                .list();

        if (audits.size() > 0) {
            return audits.get(0);
        } else {
            return null;
        }

    }
    @Transactional
    @Override
    public ArtifactType findByArtifactCode(String artifactTypeCode) {

        List<ArtifactType> audits = new ArrayList<ArtifactType>();

            audits = sessionFactory.getCurrentSession()
                .createQuery("from ArtifactType where artifactTypeCode=?")
                .setParameter(0, artifactTypeCode)
                .list();
        if (audits.size() > 0) {
            return audits.get(0);
        } else {
            return null;
        }

    }

}
