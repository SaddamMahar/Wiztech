package com.primeid.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name="audit")
public class Audit implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private long auditID;
    
    private Account accounts;
    
    private String ip;
    
    private String requestURL;
    
    private String requestParams;
    
    private String headerParams;
   
    private String response;
    
    private String created;

     
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auditID", nullable = false)
    public long getAuditID() {
        return auditID;
    }

    public void setAuditID(long auditID) {
        this.auditID = auditID;
    }

    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "accountID", referencedColumnName = "accountID")
    public Account getAccounts() {
        return accounts;
    }

    public void setAccounts(Account accounts) {
        this.accounts = accounts;
    }

    @Column (name ="ip")
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Column (name ="requestURL")    
    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    @Column (name ="requestParams")    
    public String getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

    @Column (name ="headerParams")    
    public String getHeaderParams() {
        return headerParams;
    }

    public void setHeaderParams(String headerParams) {
        this.headerParams = headerParams;
    }

    @Column (name ="response")
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    
    @Column (name ="created")
    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

}
