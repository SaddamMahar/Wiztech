package com.primeid.service;

import com.primeid.dao.OcrResultDao;
import com.primeid.model.OcrResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Saddam Hussain
 */
@Service("ocrResultService")
public class OcrResultService {
    @Autowired
    private OcrResultDao ocrResultDao;
    public OcrResult save(OcrResult ocrResult) {
        return ocrResultDao.save(ocrResult);
    }
}
