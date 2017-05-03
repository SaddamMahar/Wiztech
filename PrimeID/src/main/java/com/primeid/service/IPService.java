package com.primeid.service;

import com.primeid.dao.IPDao;
import com.primeid.model.IP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Saddam Hussain
 */
@Service
public class IPService {

    @Autowired
    IPDao ipDao;

    @Transactional
    public IP loadIPByIP(String ip) {

        IP ipObj = null;
        ipObj = ipDao.findByIP(ip);
        return ipObj;

    }
}
