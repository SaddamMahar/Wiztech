package com.primeid.service;

import com.primeid.dao.ArtifactTypeDao;
import com.primeid.model.ArtifactType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Saddam Hussain
 */
@Service("artifactTypeService")
public class ArtifactTypeService {
    @Autowired
    private ArtifactTypeDao artifactTypeDao;

    @Transactional(readOnly = true)
    ArtifactType loadByArtifactTypeID(long artifactTypeID){
        return artifactTypeDao.findByArtifactTypeID(artifactTypeID);
    }

    @Transactional(readOnly = true)
    ArtifactType loadByArtifactCode(String artifactCode){
        return artifactTypeDao.findByArtifactCode(artifactCode);
    }
}
