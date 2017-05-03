package com.primeid.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Saddam Hussain
 */

@Entity
@Table(name="artifacttypes")
public class ArtifactType implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private long artifactTypeID;
    private String artifactTypeCode;
    private String artifactTypeDescription;
    
    private Set<Artifact> artifacts = new HashSet<Artifact>(0);
//    private Set<ArtifactJurisdiction> artifactJurisdiction = new HashSet<ArtifactJurisdiction>(0);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artifactTypeID", nullable = false)
    public long getArtifactTypeID() {
        return artifactTypeID;
    }

    public void setArtifactTypeID(long artifactTypeID) {
        this.artifactTypeID = artifactTypeID;
    }

    @Column(name = "artifactTypeCode")
    public String getArtifactTypeCode() {
        return artifactTypeCode;
    }

    public void setArtifactTypeCode(String artifactTypeCode) {
        this.artifactTypeCode = artifactTypeCode;
    }

    @Column(name = "artifactTypeDescription")
    public String getArtifactTypeDescription() {
        return artifactTypeDescription;
    }

    public void setArtifactTypeDescription(String artifactTypeDescription) {
        this.artifactTypeDescription = artifactTypeDescription;
    }

    @OneToMany(targetEntity = Artifact.class, mappedBy = "artifactTypes", fetch = FetchType.EAGER)
    public Set<Artifact> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(Set<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

//    @OneToMany(targetEntity = ArtifactJurisdiction.class, mappedBy = "artifactType")
//    public Set<ArtifactJurisdiction> getArtifactJurisdiction() {
//        return artifactJurisdiction;
//    }
//
//    public void setArtifactJurisdiction(Set<ArtifactJurisdiction> artifactJurisdiction) {
//        this.artifactJurisdiction = artifactJurisdiction;
//    }
    
}
