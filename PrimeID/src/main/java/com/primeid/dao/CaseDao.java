/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.primeid.dao;

import com.primeid.model.Case;


/**
 *
 * @author Saddam Hussain
 */

public interface CaseDao {
    Case findByAccountID (long accountID);
    Case findByCaseID (long caseID);
    public Case save (Case caseRecord);
    public Case update (Case caseRecord);
}
