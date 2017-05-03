package com.primeid.dao;

import com.primeid.model.SessionTable;

/**
 *
 * @author Saddam Hussain
 */

public interface SessionTableDao {
    SessionTable findByToken (String token);
    SessionTable findByTokenExpiry (String token);
    SessionTable findByAccountID (long accountID);
    SessionTable save (SessionTable session);
    SessionTable update (SessionTable session);
}
