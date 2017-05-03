package com.primeid.dao;

import com.primeid.model.ArtifactType;

/**
 *
 * @author Saddam Hussain
 */
public interface ArtifactTypeDao {
    ArtifactType findByArtifactTypeID(long artifactTypeID);
    ArtifactType findByArtifactCode(String artifactCode);
}
