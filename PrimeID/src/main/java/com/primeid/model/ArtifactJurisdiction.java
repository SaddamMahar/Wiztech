//package com.primeid.model;
//import java.io.Serializable;
//import javax.persistence.Entity;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.Table;
//
///**
// *
// * @author Saddam Hussain
// */
//
//@Entity
//@Table(name="artifact_jurisdictions")
//public class ArtifactJurisdiction implements Serializable{
//    private static final long serialVersionUID = 1L;
//    
//    private ArtifactType artifactType;
//    private Jurisdiction jurisdiction;
//
//    @Id
//    
//    @ManyToOne
//    @JoinColumn(name = "artifactTypeID", referencedColumnName = "artifactTypeID")
//    public ArtifactType getArtifactTypeID() {
//        return artifactType;
//    }
//
//    public void setArtifactTypeID(ArtifactType artifactType) {
//        this.artifactType = artifactType;
//    }
//
//    @ManyToOne
//    @JoinColumn(name = "jurisdictionID", referencedColumnName = "jurisdictionID")
//    public Jurisdiction getJurisdictionID() {
//        return jurisdiction;
//    }
//
//    public void setJurisdictionID(Jurisdiction jurisdiction) {
//        this.jurisdiction = jurisdiction;
//    }
//
//}
