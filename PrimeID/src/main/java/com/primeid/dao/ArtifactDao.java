package com.primeid.dao;

import com.primeid.model.Artifact;
import java.util.List;

/**
 *
 * @author Saddam Hussain
 */
public interface ArtifactDao {
    Artifact findByArtifactID (long artifactID);
    List<Artifact> findByArtifactTypeID (long artifactTypeID);
    List<Artifact> findByArtifactCaseID (long caseID);
    Artifact save (Artifact artifact);
}
