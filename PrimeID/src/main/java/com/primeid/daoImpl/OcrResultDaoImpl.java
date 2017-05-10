package com.primeid.daoImpl;

import com.primeid.dao.AuditDao;
import com.primeid.dao.OcrResultDao;
import com.primeid.model.Audit;
import com.primeid.model.OcrResult;
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
@Repository("ocrResultDao")
public class OcrResultDaoImpl implements OcrResultDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional(readOnly = false)
    @Override
    public OcrResult save(OcrResult ocrResult) {
        sessionFactory.getCurrentSession().save(ocrResult);
        return ocrResult;
    }
}
