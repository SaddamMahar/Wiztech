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
@Table(name="jurisdictions")
public class Jurisdiction implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private Long jurisdictionID;
    private String jurisdictionCode;
    private String jurisdictionName;
    
    private Set<AccountJurisdiction> accountJurisdictions = new HashSet<AccountJurisdiction>(0);
    private Set<Case> caseTables = new HashSet<Case>(0);
//    private Set<ArtifactJurisdiction> ArtifactJurisdictions = new HashSet<ArtifactJurisdiction>(0);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="jurisdictionID")
    public Long getJurisdictionID() {
        return jurisdictionID;
    }

    public void setJurisdictionID(Long jurisdictionID) {
        this.jurisdictionID = jurisdictionID;
    }
    

    @Column(name="jurisdictionCode")
    public String getJurisdictionCode() {
        return jurisdictionCode;
    }

    public void setJurisdictionCode(String jurisdictionCode) {
        this.jurisdictionCode = jurisdictionCode;
    }

    @Column(name="jurisdictionName")
    public String getJurisdictionName() {
        return jurisdictionName;
    }

    public void setJurisdictionName(String jurisdictionName) {
        this.jurisdictionName = jurisdictionName;
    }

    @OneToMany(targetEntity = AccountJurisdiction.class, mappedBy = "jurisdictions", fetch = FetchType.EAGER)
    public Set<AccountJurisdiction> getAccountJurisdictionss() {
        return accountJurisdictions;
    }

    public void setAccountJurisdictionss(Set<AccountJurisdiction> accountJurisdictions) {
        this.accountJurisdictions = accountJurisdictions;
    }

    @OneToMany(targetEntity = AccountJurisdiction.class, mappedBy = "jurisdictions", fetch = FetchType.EAGER)
    public Set<Case> getcaseTables() {
        return caseTables;
    }

    public void setcaseTables(Set<Case> caseTables) {
        this.caseTables = caseTables;
    }

//    @OneToMany(targetEntity = ArtifactJurisdiction.class, mappedBy = "jurisdiction")
//    public Set<ArtifactJurisdiction> getArtifactJurisdictions() {
//        return ArtifactJurisdictions;
//    }
//
//    public void setArtifactJurisdictions(Set<ArtifactJurisdiction> ArtifactJurisdictions) {
//        this.ArtifactJurisdictions = ArtifactJurisdictions;
//    }
    
}
