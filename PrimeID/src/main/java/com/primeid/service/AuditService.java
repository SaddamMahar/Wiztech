package com.primeid.service;

import com.primeid.dao.AuditDao;
import com.primeid.model.Audit;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Saddam Hussain
 */
@Service("auditService")
public class AuditService {

    @Autowired
    private AuditDao auditDao;

    @Transactional(readOnly = true)
    public List<Audit> loadAuditByAccountID(long accountID) {
        return auditDao.findByAccountID(accountID);
    }

    public void save(Audit audit) {
        auditDao.save(audit);
    }
}
