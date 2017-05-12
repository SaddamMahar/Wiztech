package com.primeid.service;

import com.google.gson.Gson;
import com.primeid.config.JwtTokenUtil;
import com.primeid.model.Account;
import com.primeid.model.Artifact;
import com.primeid.model.Audit;
import com.primeid.model.Case;
import com.primeid.model.IP;
import com.primeid.model.Jurisdiction;
import com.primeid.model.SessionTable;
import com.primeid.model.UploadArtifactJson;
import com.primeid.utils.Constants;
import com.primeid.utils.CustomFunctions;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Saddam Hussain
 */
@Service("customAuthenticationService")
public class CustomAuthenticationService {

    private static final long EXPIRY_DURATION = 1;
    @Autowired
    private IPService ipService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private SessionTableService sessionTableService;

    @Autowired
    private CaseService caseService;

    @Autowired
    private AuditService auditService;

    @Autowired
    private ArtifactTypeService artifactTypeService;

    @Autowired
    private ArtifactService artifactService;

    @Autowired
    private JurisdictionService jurisdictionService;

    @Autowired
    private CustomFunctions customFunctions;

    Audit audit = new Audit();

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime ldt = LocalDateTime.now();
    HashMap<String, String> response = new HashMap<String, String>();
    Gson gson = new Gson();

    public HashMap<String, String> ipAuthentication(String params, HttpServletRequest request, String headers) {
        try {
            IP ipObj = ipService.loadIPByIP(request.getRemoteAddr());
            if (ipObj.isActive()) {
                return null;
            }

            setAudits(params.replaceAll("\\s", ""), headers, request);

            audit.setAccounts(accountService.loadAccountByAccountCode(gson.fromJson(params, Account.class).getAccountCode()));
            response = customFunctions.updateAccountCodeResponse("402", Constants.Errors.TWO.toString(), false);
            audit.setResponse(gson.toJson(response));
            auditService.save(audit);
            return response;
        } catch (Exception e) {
            setAudits(params.replaceAll("\\s", ""), headers, request);

            response = customFunctions.updateAccountCodeResponse("402", Constants.Errors.TWO.toString(), false);
            audit.setResponse(gson.toJson(response));
            auditService.save(audit);
            return response;
        }
    }

    public HashMap<String, String> userAuthentication(String json, HttpServletRequest request) {
        ldt = ldt.plusHours(EXPIRY_DURATION);
        setAudits(json.replaceAll("\\s", ""), "", request);
        try {
            Account account = gson.fromJson(json, Account.class);
            if (account.getAccountCode() != null && account.getAccountKey() != null) {
                Account accountDetail = accountService.loadAccountByAccountCode(account.getAccountCode());
                if (accountDetail != null) {
                    audit.setAccounts(accountDetail);
                    if (accountDetail.getAccountKey().equals(account.getAccountKey())) {
                        if (accountDetail.isAccessProduction()) {
                            SessionTable tokenObj = new SessionTable();
                            String token = null;

                            if (sessionTableService.loadSessionTableByAccountID(accountDetail.getAccountID()) == null) {
                                token = JwtTokenUtil.generateToken(accountDetail);
                                tokenObj.setAccounts(accountDetail);
                                tokenObj.setToken(token);
                                tokenObj.setExpiry(ldt.format(dtf));
                                tokenObj.setLastConnection(LocalDateTime.now().format(dtf));
                                sessionTableService.save(tokenObj);
                                response = customFunctions.updateAccountCodeResponse(tokenObj.getToken(), ldt.format(dtf), true);
                                audit.setResponse(gson.toJson(response));
                                auditService.save(audit);
                                return response;
                            }
                            tokenObj = sessionTableService.loadSessionTableByAccountID(accountDetail.getAccountID());
                            tokenObj.setExpiry(ldt.format(dtf));
                            tokenObj.setLastConnection(LocalDateTime.now().format(dtf));
                            sessionTableService.update(tokenObj);
                            response = customFunctions.updateAccountCodeResponse(tokenObj.getToken(), ldt.format(dtf), true);
                            audit.setResponse(gson.toJson(response));
                            auditService.save(audit);
                            return response;
                        } else {
                            response = customFunctions.updateAccountCodeResponse("405", Constants.Errors.FIVE.toString(), false);
                            audit.setResponse(gson.toJson(response));
                            auditService.save(audit);
                            return response;
                        }
                    } else {
                        response = customFunctions.updateAccountCodeResponse("404", Constants.Errors.FOUR.toString(), false);
                        audit.setResponse(gson.toJson(response));
                        auditService.save(audit);
                        return response;
                    }
                }
                response = customFunctions.updateAccountCodeResponse("404", Constants.Errors.FOUR.toString(), false);
                audit.setResponse(gson.toJson(response));
                auditService.save(audit);
                return response;
            } else {
                response = customFunctions.updateAccountCodeResponse("403", Constants.Errors.THREE.toString(), false);
                audit.setResponse(gson.toJson(response));
                auditService.save(audit);
                return response;
            }

        } catch (Exception e) {
            response = customFunctions.updateAccountCodeResponse("403", Constants.Errors.THREE.toString(), false);
            audit.setResponse(gson.toJson(response));
            auditService.save(audit);
            return response;
        }
    }

    public HashMap<String, String> tokenAuthentication(String tokenCode, String json, String jurisdictionID, HttpServletRequest request) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldtNow = LocalDateTime.now();
        HashMap<String, String> response = new HashMap<String, String>();
        Audit transactionAudit = new Audit();
        Gson gson = new Gson();
        transactionAudit.setCreated(ldtNow.format(dtf));
        transactionAudit.setRequestURL(request.getRequestURI().toString());
        transactionAudit.setHeaderParams("token : " + tokenCode);
        transactionAudit.setRequestParams(json.replaceAll("\\s", ""));

        String ip = request.getRemoteAddr();
        transactionAudit.setIp(ip);
        if (!jurisdictionID.isEmpty()) {

            Jurisdiction jurisdiction = jurisdictionService.loadJurisdictionByJurisdictionID(Long.parseLong(jurisdictionID));
            try {
                String requestToken = gson.fromJson(tokenCode, String.class);

                if (jurisdiction != null) {

                    if (sessionTableService.loadSessionTableByTokenExpiry(requestToken) == null
                            || (!sessionTableService.loadSessionTableByTokenExpiry(requestToken).getToken().equals(requestToken))) {

                        response = customFunctions.updateTokenResponse("406", Constants.Errors.SIX.toString(), false);
                        transactionAudit.setResponse(gson.toJson(response));
                        auditService.save(transactionAudit);
                        return response;
                    }

                    SessionTable token = sessionTableService.loadSessionTableByToken(requestToken);
                    if (token != null) {

                        Case caseRecord = new Case();
                        caseRecord.setAccounts(token.getAccounts());
                        caseRecord.setJurisdictions(jurisdiction);
                        caseRecord.setCreated(LocalDateTime.now().format(dtf));
                        caseRecord = caseService.save(caseRecord);
                        ldtNow = ldtNow.plusHours(EXPIRY_DURATION);
                        token.setExpiry(ldtNow.format(dtf));
                        token.setLastConnection(LocalDateTime.now().format(dtf));
                        sessionTableService.update(token);
                        response = customFunctions.updateTokenResponse(Long.toString(caseRecord.getCaseID()), ldtNow.format(dtf), true);
                        transactionAudit.setResponse(gson.toJson(response));
                        auditService.save(transactionAudit);
                        return response;
                    } else {
                        response = customFunctions.updateTokenResponse("407", Constants.Errors.SEVEN.toString(), false);
                        transactionAudit.setResponse(gson.toJson(response));
                        auditService.save(transactionAudit);
                        return response;
                    }

                } else {
                    response = customFunctions.updateAccountCodeResponse("413", Constants.Errors.THIRTEEN.toString(), false);
                    transactionAudit.setResponse(gson.toJson(response));
                    auditService.save(transactionAudit);
                    return response;
                }
            } catch (Exception e) {
                response = customFunctions.updateAccountCodeResponse("406", Constants.Errors.SIX.toString(), false);
                transactionAudit.setResponse(gson.toJson(response));
                auditService.save(transactionAudit);
                return response;
            }

        } else {
            response = customFunctions.updateAccountCodeResponse("403", Constants.Errors.THREE.toString(), false);
            transactionAudit.setResponse(gson.toJson(response));
            auditService.save(transactionAudit);
            return response;
        }
    }

    public void setAudits(String params, String headers, HttpServletRequest request) {
        audit.setRequestURL(request.getRequestURI().toString());
        audit.setCreated(ldt.format(dtf));
        audit.setRequestParams(params.replaceAll("\\s", ""));
        audit.setHeaderParams(headers);
        audit.setIp(request.getRemoteAddr());
    }

}
