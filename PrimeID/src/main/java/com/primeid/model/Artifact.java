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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Saddam Hussain
 */

@Entity
@Table(name="artifacts")
public class Artifact implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private long artifactID;
    private String meta;
    private String repositoryRef;
    private String created;
    private String ocr_map;
    
    private Case cases;
    private ArtifactType artifactTypes;
    
    private Set<OcrResult> ocrResults = new HashSet<OcrResult>(0);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artifactID", nullable = false)
    public long getArtifactID() {
        return artifactID;
    }

    public void setArtifactID(long artifactID) {
        this.artifactID = artifactID;
    }

    @Column(name = "meta")
    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    @Column(name = "repositoryRef")
    public String getRepositoryRef() {
        return repositoryRef;
    }

    public void setRepositoryRef(String repositoryRef) {
        this.repositoryRef = repositoryRef;
    }

    @Column(name = "created")
    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caseID",referencedColumnName = "caseID")
    public Case getCases() {
        return cases;
    }

    public void setCases(Case cases) {
        this.cases = cases;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artifactTypeID",referencedColumnName = "artifactTypeID")
    public ArtifactType getArtifactTypes() {
        return artifactTypes;
    }

    public void setArtifactTypes(ArtifactType artifactTypes) {
        this.artifactTypes = artifactTypes;
    }

    @OneToMany(targetEntity = OcrResult.class, mappedBy = "artifacts", fetch = FetchType.EAGER)
    public Set<OcrResult> getOcrResults() {
        return ocrResults;
    }

    public void setOcrResults(Set<OcrResult> ocrResults) {
        this.ocrResults = ocrResults;
    }

    public String getOcr_map() {
        return ocr_map;
    }

    public void setOcr_map(String ocr_map) {
        this.ocr_map = ocr_map;
    }
    
}
