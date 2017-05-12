package com.primeid.utils;

import com.google.gson.Gson;
import com.primeid.model.Audit;
import com.primeid.model.Case;
import com.primeid.service.CaseService;
import com.primeid.service.SessionTableService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Saddam Hussain
 */
public class CustomFunctions {
    @Autowired
    private CaseService caseService;
    
    @Autowired
    private SessionTableService sessionTableService;
    
    Audit audit= new Audit();
    
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime ldt = LocalDateTime.now();
    HashMap<String, String> response = new HashMap<String, String>();
    
    public HashMap<String, String> validateCaseID(String caseID, long accountID) {
        try {
            if (caseID == null) {
                response = updateTokenResponse("403", Constants.Errors.THREE.toString(), false);
                return response;
            }
            long caseid = Long.parseLong(caseID);
            if (caseService.loadCaseByCaseID(caseid) != null) {
                Case caseTabel = caseService.loadCaseByCaseID(caseid);
                if (caseTabel.getAccounts().getAccountID() == accountID) {
                    return null;
                }
            }
        } catch (Exception e) {
            response = updateTokenResponse("408", Constants.Errors.EIGHT.toString(), false);
            return response;
        }

        response = updateTokenResponse("408", Constants.Errors.EIGHT.toString(), false);
        return response;
    }
    public HashMap<String, String> updateTokenResponse(String status, String value, boolean check) {
        HashMap<String, String> response = new LinkedHashMap<>();

        if (check) {
            response.put("caseID", status);
            response.put("expiry", value);
        } else {
            response.put("status", status);
            response.put("details", value);
        }

        return response;
    }
    public HashMap<String, String> updateAccountCodeResponse(String status, String value, boolean check) {
        HashMap<String, String> response = new LinkedHashMap<>();

        if (check) {
            response.put("token", status);
            response.put("expiry", value);
        } else {
            response.put("status", status);
            response.put("details", value);
        }

        return response;
    }
    public HashMap<String, String> updateArtifactResponse(String status, String value, boolean check) {
        HashMap<String, String> response = new LinkedHashMap<>();

        if (check) {
            response.put("artifactKey", status);
            response.put("expiry", value);
        } else {
            response.put("status", status);
            response.put("details", value);
        }

        return response;
    }
    
    public static boolean validateHTTP_URI(String uri) {
//        String[] schemes = {"http", "https"}; // DEFAULT schemes = "http", "https", "ftp"
        UrlValidator urlValidator = new UrlValidator();
        if (urlValidator.isValid(uri)) {
            return true;
        } else {
            return false;
        }
    }
    
    public String[] getHeaders(HttpServletRequest request) {
        
        String[] headers = new String[5];
        if (request.getHeader("token") != null) {
            headers[0] = request.getHeader("token");
        }
        if (request.getHeader("caseID") != null) {
            headers[1] = request.getHeader("caseID");
        }
        if (request.getHeader("hasFile") != null) {
            headers[2] = request.getHeader("hasFile");
        }
        return headers;
    }
    
    public HashMap<String, String> validateToken(String token) {
        Gson gson = new Gson();
        HashMap<String, String> response = new HashMap<String, String>();

        String requestToken = gson.fromJson(token, String.class);
        if (requestToken == null) {
            response = updateTokenResponse("403", Constants.Errors.THREE.toString(), false);
            return response;
        }
        if(sessionTableService.loadSessionTableByToken(requestToken) == null
                || (!sessionTableService.loadSessionTableByToken(requestToken).getToken().equals(requestToken))) {

            response = updateTokenResponse("406", Constants.Errors.SIX.toString(), false);
            return response;
        } else if (sessionTableService.loadSessionTableByTokenExpiry(requestToken) == null) {
            response = updateTokenResponse("407", Constants.Errors.SEVEN.toString(), false);
            return response;
        }
        return null;
    }
    
}
