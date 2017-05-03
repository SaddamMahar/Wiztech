package com.primeid.dao;

import com.primeid.model.OcrResult;

/**
 *
 * @author Saddam Hussain
 */
public interface OcrResultDao {
    OcrResult findByIP(String ip);
}
