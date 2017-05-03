package com.primeid.dao;

import com.primeid.model.Audit;
import java.util.List;

/**
 *
 * @author Saddam Hussain
 */
public interface AuditDao {
    List<Audit> findByAccountID (long accountID);
    public void save (Audit audit);
}
