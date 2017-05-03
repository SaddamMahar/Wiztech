package com.primeid.model;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Saddam Hussain
 */

@Entity
@Table(name="ocrResults")
public class OcrResult implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private long ocrResultID;
    private String connectionData;
    private String responseData;
    private String created;

    private Artifact artifacts;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ocrResultID")
    public long getOcrResultID() {
        return ocrResultID;
    }

    public void setOcrResultID(long ocrResultID) {
        this.ocrResultID = ocrResultID;
    }

    @Column(name="connectionData")
    public String getConnectionData() {
        return connectionData;
    }

    public void setConnectionData(String connectionData) {
        this.connectionData = connectionData;
    }

    @Column(name="responseData")
    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    @Column(name="created")
    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artifactID",referencedColumnName = "artifactID")
    public Artifact getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(Artifact artifacts) {
        this.artifacts = artifacts;
    }
    
}
