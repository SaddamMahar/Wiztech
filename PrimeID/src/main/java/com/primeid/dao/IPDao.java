package com.primeid.dao;

import com.primeid.model.IP;

/**
 *
 * @author Saddam Hussain
 */
public interface IPDao {
    IP findByIP(String ip);
}
