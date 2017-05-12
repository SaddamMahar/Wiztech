package com.primeid.service;

import com.google.gson.Gson;
import com.primeid.model.Artifact;
import com.primeid.model.Audit;
import com.primeid.model.Case;
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
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author hashim
 */

@Service("uploadService")
public class UploadService {
    
    private static final long EXPIRY_DURATION = 1;
    @Autowired
    CustomAuthenticationService customAuthenticationService;
    @Autowired
    private AuditService auditService;
    @Autowired
    private SessionTableService sessionTableService;
    @Autowired
    private CaseService caseService;
    @Autowired
    private ArtifactService artifactService;
    @Autowired
    private ArtifactTypeService artifactTypeService;
    @Autowired
    private CustomFunctions customFunctions;
    
    Audit audit= new Audit();
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime ldt = LocalDateTime.now();
    HashMap<String, String> response = new HashMap<String, String>();
    
    public HashMap<String, String> uploadArtifact(String token, String caseID, boolean hasFile, HttpServletRequest request, MultipartFile file, String json) {

        Gson gson = new Gson();
        response = customAuthenticationService.ipAuthentication("", request, gson.toJson("token:" + token + ", caseID:" + caseID + " , hasFile : " + hasFile));
        SessionTable tokenTable = new SessionTable();
        String headers="token : " + token + " , caseID: " + caseID + " , hasFile : " + hasFile;
//        setAudits("", headers, request);
        if (response != null) {
            audit.setResponse(gson.toJson(response));
            auditService.save(audit);
            return response;
        }
        if (json == null) {
            response = customFunctions.updateTokenResponse("403", Constants.Errors.THREE.toString(), false);
            audit.setResponse(gson.toJson(response));
            auditService.save(audit);
            return response;
        }
        response = customFunctions.validateToken(token);
        if (response != null) {
            audit.setResponse(gson.toJson(response));
            auditService.save(audit);
            return response;
        }
        String requestToken = gson.fromJson(token, String.class);
        tokenTable = sessionTableService.loadSessionTableByTokenExpiry(requestToken);

        audit.setAccounts(tokenTable.getAccounts());
        response = customFunctions.validateCaseID(caseID, tokenTable.getAccounts().getAccountID());
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
    
    public HashMap<String, String> uploadFile(MultipartFile file, String json, Artifact artifact, boolean hasFile, Case caseTable, SessionTable tokenTable) {
        Gson gson = new Gson();
        try {
            
            UploadArtifactJson artifactJson = gson.fromJson(json, UploadArtifactJson.class);
            artifact.setMeta(gson.toJson(artifactJson.getMeta()));
            artifact.setOcr_map(gson.toJson(artifactJson.getOcr_map()));
            ldt = ldt.plusHours(EXPIRY_DURATION);
            tokenTable.setExpiry(ldt.format(dtf));
            sessionTableService.update(tokenTable);
            String extension = artifactJson.getMeta().getFormat();
            artifact.setArtifactTypes(artifactTypeService.loadByArtifactCode(extension));
            if(artifactJson.getType()!= null){
                try {
                    if(artifactTypeService.loadByArtifactTypeID(Long.parseLong(artifactJson.getType())) == null){
                        return response = customFunctions.updateTokenResponse("417", Constants.Errors.SEVENTEEN.toString(), false);
                    }
                } catch (Exception e) {
                    return response = customFunctions.updateTokenResponse("417", Constants.Errors.SEVENTEEN.toString(), false);
                }
            }
            if (hasFile) {
                if (artifactJson == null || artifactJson.getType() == null || artifactJson.getMeta().getName() == null ||
                        artifactJson.getMeta().getSize() == null) {

                    return response = customFunctions.updateTokenResponse("403", Constants.Errors.THREE.toString(), false);
                }
                if (file.isEmpty()) {
                    return response = customFunctions.updateTokenResponse("403", Constants.Errors.THREE.toString(), false);
                }else if (!file.getOriginalFilename().split("\\.")[0].equalsIgnoreCase(artifactJson.getMeta().getName())) {
                    return response = customFunctions.updateTokenResponse("416", Constants.Errors.SIXTEEN.toString(), false);
                } else if (!file.getOriginalFilename().split("\\.")[1].equalsIgnoreCase(artifactJson.getMeta().getFormat())) {
                    return response = customFunctions.updateTokenResponse("410", Constants.Errors.TEN.toString(), false);
                } else if (file.getSize() != Long.parseLong(artifactJson.getMeta().getSize())) {
                    return response = customFunctions.updateTokenResponse("411", Constants.Errors.ELEVEN.toString(), false);
                }
                
                String path = Paths.get("").toAbsolutePath() + "\\ocr_conversions\\" + file.getOriginalFilename();
                artifact.setRepositoryRef(path);
                File nfile = new File(path);
                nfile.getParentFile().mkdirs();
                nfile.createNewFile();
                OutputStream outputStream = new FileOutputStream(path);
                outputStream.write(file.getBytes());
                outputStream.flush();
                outputStream.close();
                artifact.setCases(caseTable);
                artifact.setCreated(ldt.now().format(dtf));
                artifactService.save(artifact);
                return response = customFunctions.updateArtifactResponse(artifact.getArtifactID() + "", tokenTable.getExpiry(), true);

            } else if (customFunctions.validateHTTP_URI(artifactJson.getLocation())) {
                artifact.setRepositoryRef(artifactJson.getLocation());
            } else {
                return response = customFunctions.updateTokenResponse("414", Constants.Errors.FOURTEEN.toString(), false);
            }

            artifact.setCases(caseTable);
            artifact.setCreated(ldt.now().format(dtf));
            artifactService.save(artifact);

        } catch (Exception e) {
            e.printStackTrace();
            return response = customFunctions.updateTokenResponse("403", Constants.Errors.THREE.toString(), false);
        }
        return response = customFunctions.updateArtifactResponse(artifact.getArtifactID() + "", tokenTable.getExpiry(), true);
    }
}
