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
@Table(name="accounts")
public class Account implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private long accountID;
    private String accountCode;
    private String accountKey;
    private String accountCompany;
    private String accountContact;
    private String created;
    private String lastUpdated;
    private boolean accessStage;
    private boolean accessProduction;
    
    
    private Set<IP> ips = new HashSet<IP>(0);
    private Set<Audit> audits = new HashSet<Audit>(0);
    private Set<SessionTable> SessionTables = new HashSet<SessionTable>(0);
    private Set<Case> caseTables = new HashSet<Case>(0);
    private Set<AccountJurisdiction> accountJurisdictions = new HashSet<AccountJurisdiction>(0);

    
    @OneToMany(targetEntity = IP.class, mappedBy = "accounts", fetch = FetchType.EAGER)
    public Set<IP> getIps() {
        return ips;
    }

    public void setIps(Set<IP> ips) {
        this.ips = ips;
    }
    @OneToMany(targetEntity = Audit.class, mappedBy = "accounts", fetch = FetchType.EAGER)
    public Set<Audit> getAudits() {
        return audits;
    }

    public void setAudits(Set<Audit> audits) {
        this.audits = audits;
    }
    
    @OneToMany(targetEntity = SessionTable.class, mappedBy = "accounts", fetch = FetchType.EAGER)
    public Set<SessionTable> getSessionTables() {
        return SessionTables;
    }

    public void setSessionTables(Set<SessionTable> SessionTables) {
        this.SessionTables = SessionTables;
    }

    @OneToMany(targetEntity = Case.class, mappedBy = "accounts", fetch = FetchType.EAGER)
    public Set<Case> getCaseTables() {
        return caseTables;
    }

    public void setCaseTables(Set<Case> caseTables) {
        this.caseTables = caseTables;
    }

    @OneToMany(targetEntity = AccountJurisdiction.class, mappedBy = "accounts", fetch = FetchType.EAGER)
    public Set<AccountJurisdiction> getAccountJurisdictionss() {
        return accountJurisdictions;
    }

    public void setAccountJurisdictionss(Set<AccountJurisdiction> accountJurisdictions) {
        this.accountJurisdictions = accountJurisdictions;
    }

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accountID", nullable = false)
    public long getAccountID() {
        return accountID;
    }

    public void setAccountID(long accountID) {
        this.accountID = accountID;
    }

    @Column (name ="accountCode")
    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    @Column (name ="accountKey")
    public String getAccountKey() {
        return accountKey;
    }

    public void setAccountKey(String accountKey) {
        this.accountKey = accountKey;
    }

    @Column (name ="accountCompany")
    public String getAccountCompany() {
        return accountCompany;
    }

    public void setAccountCompany(String accountCompany) {
        this.accountCompany = accountCompany;
    }

    @Column (name ="accountContact")
    public String getAccountContact() {
        return accountContact;
    }

    public void setAccountContact(String accountContact) {
        this.accountContact = accountContact;
    }

    @Column (name ="created")
    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    
    @Column (name ="lastUpdate")
    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Column (name ="accessStage")
    public boolean isAccessStage() {
        return accessStage;
    }

    public void setAccessStage(boolean accessStage) {
        this.accessStage = accessStage;
    }

    @Column (name ="accessProduction")
    public boolean isAccessProduction() {
        return accessProduction;
    }

    public void setAccessProduction(boolean accessProduction) {
        this.accessProduction = accessProduction;
    }

    
}
