package com.primeid.dao;

import com.primeid.model.Jurisdiction;

/**
 *
 * @author Saddam Hussain
 */
public interface JurisdictionDao {
    Jurisdiction findByJurisdictionID (long id);
    Jurisdiction findByJurisdictionCode (String code);
    Jurisdiction findByJurisdictionName (String name);
}
