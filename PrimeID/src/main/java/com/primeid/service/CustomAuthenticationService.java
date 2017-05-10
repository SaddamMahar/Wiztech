package com.primeid.service;

import com.google.gson.Gson;
import com.primeid.config.JwtTokenUtil;
import com.primeid.model.Account;
import com.primeid.model.Artifact;
import com.primeid.model.Audit;
import com.primeid.model.Case;
import com.primeid.model.IP;
import com.primeid.model.Jurisdiction;
import com.primeid.model.OcrMap;
import com.primeid.model.OcrOutput;
import com.primeid.model.OcrResult;
import com.primeid.model.SessionTable;
import com.primeid.model.UploadArtifactJson;
import com.primeid.utils.Constants;
import com.primeid.utils.UznFormat;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
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
    private ArtifactService artifactService;

    @Autowired
    private ArtifactTypeService artifactTypeService;

    @Autowired
    private OcrResultService ocrResultService;

    @Autowired
    private JurisdictionService jurisdictionService;

    public HashMap<String, String> ipAuthentication(String params, HttpServletRequest request, String headers) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        HashMap<String, String> response = new HashMap<String, String>();
        LocalDateTime ldt = LocalDateTime.now();
        Gson gson = new Gson();
        try {
            IP ipObj = ipService.loadIPByIP(request.getRemoteAddr());
            if (ipObj.isActive()) {
                return null;
            }

            Audit audit = new Audit();
            audit.setCreated(ldt.format(dtf));
            audit.setRequestURL(request.getRequestURI().toString());
            audit.setRequestParams(params.replaceAll("\\s", ""));
            audit.setHeaderParams(headers);
            audit.setIp(request.getRemoteAddr());
            audit.setAccounts(accountService.loadAccountByAccountCode(gson.fromJson(params, Account.class).getAccountCode()));
            response = updateAccountCodeResponse("402", Constants.Errors.TWO.toString(), false);
            audit.setResponse(gson.toJson(response));
            auditService.save(audit);
            return response;
        } catch (Exception e) {
            Audit transactionAudit = new Audit();
            transactionAudit.setCreated(ldt.format(dtf));
            transactionAudit.setRequestURL(request.getRequestURI().toString());
            transactionAudit.setRequestParams(params.replaceAll("\\s", ""));
            transactionAudit.setHeaderParams(headers);
            transactionAudit.setIp(request.getRemoteAddr());

            response = updateAccountCodeResponse("402", Constants.Errors.TWO.toString(), false);
            transactionAudit.setResponse(gson.toJson(response));
            auditService.save(transactionAudit);
            return response;
        }
    }

    public HashMap<String, String> userAuthentication(String json, HttpServletRequest request) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        HashMap<String, String> response = new HashMap<String, String>();
        Gson gson = new Gson();
        LocalDateTime ldt = LocalDateTime.now();
        ldt = ldt.plusHours(EXPIRY_DURATION);

        Audit transactionAudit = new Audit();
        transactionAudit.setCreated(ldt.format(dtf));
        transactionAudit.setRequestURL(request.getRequestURI().toString());
        transactionAudit.setRequestParams(json.replaceAll("\\s", ""));
        transactionAudit.setIp(request.getRemoteAddr());

        try {

            Account account = gson.fromJson(json, Account.class);
            if (account.getAccountCode() != null && account.getAccountKey() != null) {
                Account accountDetail = accountService.loadAccountByAccountCode(account.getAccountCode());
                if (accountDetail != null) {
                    transactionAudit.setAccounts(accountDetail);
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
                                response = updateAccountCodeResponse(tokenObj.getToken(), ldt.format(dtf), true);
                                transactionAudit.setResponse(gson.toJson(response));
                                auditService.save(transactionAudit);
                                return response;
                            }
                            tokenObj = sessionTableService.loadSessionTableByAccountID(accountDetail.getAccountID());
                            tokenObj.setExpiry(ldt.format(dtf));
                            tokenObj.setLastConnection(LocalDateTime.now().format(dtf));
                            sessionTableService.update(tokenObj);
                            response = updateAccountCodeResponse(tokenObj.getToken(), ldt.format(dtf), true);
                            transactionAudit.setResponse(gson.toJson(response));
                            auditService.save(transactionAudit);
                            return response;
                        } else {
                            response = updateAccountCodeResponse("405", Constants.Errors.FIVE.toString(), false);
                            transactionAudit.setResponse(gson.toJson(response));
                            auditService.save(transactionAudit);
                            return response;
                        }
                    } else {
                        response = updateAccountCodeResponse("404", Constants.Errors.FOUR.toString(), false);
                        transactionAudit.setResponse(gson.toJson(response));
                        auditService.save(transactionAudit);
                        return response;
                    }
                }
                response = updateAccountCodeResponse("404", Constants.Errors.FOUR.toString(), false);
                transactionAudit.setResponse(gson.toJson(response));
                auditService.save(transactionAudit);
                return response;
            } else {
                response = updateAccountCodeResponse("403", Constants.Errors.THREE.toString(), false);
                transactionAudit.setResponse(gson.toJson(response));
                auditService.save(transactionAudit);
                return response;
            }

        } catch (Exception e) {
            response = updateAccountCodeResponse("403", Constants.Errors.THREE.toString(), false);
            transactionAudit.setResponse(gson.toJson(response));
            auditService.save(transactionAudit);
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

                        response = updateTokenResponse("406", Constants.Errors.SIX.toString(), false);
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
                        response = updateTokenResponse(Long.toString(caseRecord.getCaseID()), ldtNow.format(dtf), true);
                        transactionAudit.setResponse(gson.toJson(response));
                        auditService.save(transactionAudit);
                        return response;
                    } else {
                        response = updateTokenResponse("407", Constants.Errors.SEVEN.toString(), false);
                        transactionAudit.setResponse(gson.toJson(response));
                        auditService.save(transactionAudit);
                        return response;
                    }

                } else {
                    response = updateAccountCodeResponse("413", Constants.Errors.THIRTEEN.toString(), false);
                    transactionAudit.setResponse(gson.toJson(response));
                    auditService.save(transactionAudit);
                    return response;
                }
            } catch (Exception e) {
                response = updateAccountCodeResponse("406", Constants.Errors.SIX.toString(), false);
                transactionAudit.setResponse(gson.toJson(response));
                auditService.save(transactionAudit);
                return response;
            }

        } else {
            response = updateAccountCodeResponse("403", Constants.Errors.THREE.toString(), false);
            transactionAudit.setResponse(gson.toJson(response));
            auditService.save(transactionAudit);
            return response;
        }
    }

    public HashMap<String, String> uploadArtifact(String token, String caseID, boolean hasFile, HttpServletRequest request, MultipartFile file, String json) {
        HashMap<String, String> response = new HashMap<String, String>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldtNow = LocalDateTime.now();
        Gson gson = new Gson();
        Audit audit = new Audit();
        response = ipAuthentication("", request, gson.toJson("token:" + token + ", caseID:" + caseID + " , hasFile : " + hasFile));
        SessionTable tokenTable = new SessionTable();

        audit.setCreated(ldtNow.format(dtf));
        audit.setHeaderParams("token : " + token + " , caseID: " + caseID + " , hasFile : " + hasFile);
        audit.setIp(request.getRemoteAddr());
        audit.setRequestURL(request.getRequestURI().toString());

        if (response != null) {
            audit.setResponse(gson.toJson(response));
            auditService.save(audit);
            return response;
        }
        if (json == null) {
            response = updateTokenResponse("403", Constants.Errors.THREE.toString(), false);
            audit.setResponse(gson.toJson(response));
            auditService.save(audit);
            return response;
        }
        response = validateToken(token);
        if (response != null) {
            audit.setResponse(gson.toJson(response));
            auditService.save(audit);
            return response;
        }
        String requestToken = gson.fromJson(token, String.class);
        tokenTable = sessionTableService.loadSessionTableByTokenExpiry(requestToken);

        audit.setAccounts(tokenTable.getAccounts());
        response = validateCaseID(caseID, tokenTable.getAccounts().getAccountID());
        if (response != null) {
            audit.setResponse(gson.toJson(response));
            auditService.save(audit);
            return response;
        }
        Case caseTable = caseService.loadCaseByAccountID(tokenTable.getAccounts().getAccountID());

        Artifact artifact = new Artifact();

        response = uploadFile(file, json, artifact, hasFile, caseTable, tokenTable);
        audit.setResponse(gson.toJson(response));
        auditService.save(audit);
        return response;
    }

    public HashMap<String, String> validateToken(String token) {
        Gson gson = new Gson();
        HashMap<String, String> response = new HashMap<String, String>();

        String requestToken = gson.fromJson(token, String.class);
        if (requestToken == null) {
            response = updateTokenResponse("403", Constants.Errors.THREE.toString(), false);
            return response;
        }
        if (sessionTableService.loadSessionTableByToken(requestToken) == null
                || (!sessionTableService.loadSessionTableByToken(requestToken).getToken().equals(requestToken))) {

            response = updateTokenResponse("406", Constants.Errors.SIX.toString(), false);
            return response;
        } else if (sessionTableService.loadSessionTableByTokenExpiry(requestToken) == null) {
            response = updateTokenResponse("407", Constants.Errors.SEVEN.toString(), false);
            return response;
        }
        return null;
    }

    public HashMap<String, String> validateCaseID(String caseID, long accountID) {
        HashMap<String, String> response = new HashMap<String, String>();
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

    public HashMap<String, String> uploadFile(MultipartFile file, String json, Artifact artifact, boolean hasFile, Case caseTable, SessionTable tokenTable) {
        HashMap<String, String> response = new HashMap<String, String>();
        Gson gson = new Gson();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldtNow = LocalDateTime.now();
        try {
            UploadArtifactJson artifactJson = gson.fromJson(json, UploadArtifactJson.class);
            artifact.setMeta(gson.toJson(artifactJson.getMeta()));
            artifact.setOcr_map(gson.toJson(artifactJson.getOcr_map()));
            ldtNow = ldtNow.plusHours(EXPIRY_DURATION);
            tokenTable.setExpiry(ldtNow.format(dtf));
            sessionTableService.update(tokenTable);
            String extension = artifactJson.getMeta().getFormat();
            artifact.setArtifactTypes(artifactTypeService.loadByArtifactCode(extension));
            if(artifactJson.getType()!= null){
                try {
                    if(artifactTypeService.loadByArtifactTypeID(Long.parseLong(artifactJson.getType())) == null){
                        return response = updateTokenResponse("417", Constants.Errors.SEVENTEEN.toString(), false);
                    }
                } catch (Exception e) {
                    return response = updateTokenResponse("417", Constants.Errors.SEVENTEEN.toString(), false);
                }
            }
            if (hasFile) {
                if (artifactJson == null || artifactJson.getType() == null || artifactJson.getMeta().getName() == null ||
                        artifactJson.getMeta().getSize() == null) {

                    return response = updateTokenResponse("403", Constants.Errors.THREE.toString(), false);
                }
                if (file.isEmpty()) {
                    return response = updateTokenResponse("403", Constants.Errors.THREE.toString(), false);
                }else if (!file.getOriginalFilename().split("\\.")[0].equalsIgnoreCase(artifactJson.getMeta().getName())) {
                    return response = updateTokenResponse("416", Constants.Errors.SIXTEEN.toString(), false);
                } else if (!file.getOriginalFilename().split("\\.")[1].equalsIgnoreCase(artifactJson.getMeta().getFormat())) {
                    return response = updateTokenResponse("410", Constants.Errors.TEN.toString(), false);
                } else if (file.getSize() != Long.parseLong(artifactJson.getMeta().getSize())) {
                    return response = updateTokenResponse("411", Constants.Errors.ELEVEN.toString(), false);
                }
                
                String path = Paths.get("").toAbsolutePath() + "\\uploads\\" + file.getOriginalFilename();
                artifact.setRepositoryRef(path);
                File nfile = new File(path);
                nfile.getParentFile().mkdirs();
                nfile.createNewFile();

                OutputStream outputStream = new FileOutputStream(path);

                outputStream.write(file.getBytes());
                outputStream.flush();
                outputStream.close();

                artifact.setCases(caseTable);
                artifact.setCreated(ldtNow.now().format(dtf));
                artifactService.save(artifact);
                return response = updateArtifactResponse(artifact.getArtifactID() + "", tokenTable.getExpiry(), true);

            } else if (validateHTTP_URI(artifactJson.getLocation())) {
                artifact.setRepositoryRef(artifactJson.getLocation());
            } else {
                return response = updateTokenResponse("414", Constants.Errors.FOURTEEN.toString(), false);
            }

            artifact.setCases(caseTable);
            artifact.setCreated(ldtNow.now().format(dtf));
            artifactService.save(artifact);

        } catch (Exception e) {
            e.printStackTrace();
            return response = updateTokenResponse("403", Constants.Errors.THREE.toString(), false);
        }
        return response = updateArtifactResponse(artifact.getArtifactID() + "", tokenTable.getExpiry(), true);
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

    public HashMap<String, String> OCR(String json, HttpServletRequest request) {
        HashMap<String, String> response = new HashMap<String, String>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldtNow = LocalDateTime.now();
        Gson gson = new Gson();
        Audit audit = new Audit();
        String[] headerKeys = {"token", "caseID", "hasFile"};
        String[] headerValues = getHeaders(request);
        String headerParams = "";
        int count = 0;
        for (String value : headerValues) {
            if (value != null) {
                if (count == 0) {
                    headerParams = headerKeys[count] + " : " + value;
                } else {
                    headerParams += ", " + headerKeys[count] + " : " + value;
                }
                count++;
            }

        }

        SessionTable tokenTable = new SessionTable();

        audit.setCreated(ldtNow.format(dtf));
        audit.setHeaderParams(headerParams);
        audit.setIp(request.getRemoteAddr());
        audit.setRequestURL(request.getRequestURI().toString());
        Arrays.stream(getHeaders(request)).forEach(i -> System.out.println(i));

        response = ipAuthentication(json, request, headerParams);
        if (response != null) {
            audit.setResponse(gson.toJson(response));
            auditService.save(audit);
            return response;
        }
        response = validateToken(headerValues[0]);
        if (response != null) {
            audit.setResponse(gson.toJson(response));
            auditService.save(audit);
            return response;
        }
        if (json == null) {
            response = updateTokenResponse("403", Constants.Errors.THREE.toString(), false);
            audit.setResponse(gson.toJson(response));
            auditService.save(audit);
            return response;
        }
        tokenTable = sessionTableService.loadSessionTableByTokenExpiry(headerValues[0]);
        ldtNow = ldtNow.plusHours(EXPIRY_DURATION);
        tokenTable.setExpiry(ldtNow.format(dtf));
        sessionTableService.update(tokenTable);

        Map<String, Object> map = new HashMap<String, Object>();
        map = (Map<String, Object>) gson.fromJson(json, map.getClass());
        Artifact artifact = artifactService.loadArtifactByArtifactID(Long.parseLong(map.get("artifactKey").toString()));
        if(null == map.get("artifactKey") || artifact.getArtifactID() == 0){
            response = updateTokenResponse("412", Constants.Errors.TWELIVE.toString(), false);
            audit.setResponse(gson.toJson(response));
            auditService.save(audit);
            return response;
        }
        Case caseTable = caseService.loadCaseByCaseID(artifact.getCases().getCaseID());
        if (!(caseTable.getAccounts().getAccountID() == tokenTable.getAccounts().getAccountID())) {
            response = updateTokenResponse("403", Constants.Errors.THREE.toString(), false);
            audit.setResponse(gson.toJson(response));
            auditService.save(audit);
            return response;
        } else {
            OcrMap[] ocrMaps = gson.fromJson(artifact.getOcr_map(), OcrMap[].class);
            UznFormat uzn = new UznFormat();
            String path = artifact.getRepositoryRef();
            String conversionPath = Paths.get("").toAbsolutePath() +"\\ocr_conversions\\";
            System.out.println("path : " + path);
            File imageFile  = new File(path);
            String[] ex = imageFile.getName().split("\\.");
            String fileNameExtension = imageFile.getName(); //with type
            String fileName = imageFile.getName().replace(ex[ex.length-1], ""); //without type
            System.out.println("fileName : " + fileName);
            System.out.println("conversionPath : " + conversionPath);
            BufferedImage img = null;
            BufferedImage blackWhite = null;            
            try {
                OcrResult ocr = new OcrResult();
                img = ImageIO.read(imageFile);
                if(uzn.createUZNFile(ocrMaps,conversionPath,fileName)){
                    blackWhite = uzn.convertingImage(img, conversionPath, fileNameExtension);
                    File file = new File (conversionPath+fileName + "uzn");
                    List<OcrOutput> ocr_results = uzn.tess4jText(blackWhite , file);
                    ocr.setArtifacts(artifact);
                    ocr.setConnectionData(gson.toJson(file));
                    ocr.setResponseData(gson.toJson(ocr_results));
                    ocr.setCreated(ldtNow.format(dtf));
                    ocr = ocrResultService.save(ocr);
                    response = new HashMap<>();
                    response.put("ocrResultsID", ocr.getOcrResultID()+"");
                    response.put("ocr_results", gson.toJson(ocr_results));
                    audit.setResponse(gson.toJson(response));
                    auditService.save(audit);                    
                    return response;
                } else {
                    
                }
                
            } catch (IOException e) {
                response = updateTokenResponse("415", Constants.Errors.FOURTEEN.toString(), false);
                audit.setResponse(gson.toJson(response));
                auditService.save(audit);
                return response;
            }

        }

        return null;
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
}
