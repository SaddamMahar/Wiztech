package com.primeid.model;


import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Saddam Hussain
 */

@Entity
@Table(name="account_jurisdictions")
public class AccountJurisdiction implements Serializable{

    @Id

    @ManyToOne
    @JoinColumn(name = "accountID", referencedColumnName = "accountID")
    private Account accounts;
    @ManyToOne
    @JoinColumn(name = "jurisdictionID",referencedColumnName = "jurisdictionID")
    private Jurisdiction jurisdictions;

    
    public Account getAccounts() {
        return accounts;
    }

    public void setAccounts(Account accounts) {
        this.accounts = accounts;
    }

    public Jurisdiction getJurisdiction() {
        return jurisdictions;
    }

    public void setJurisdiction(Jurisdiction jurisdiction) {
        this.jurisdictions = jurisdiction;
    }
}
