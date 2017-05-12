package com.primeid.service;

import com.google.gson.Gson;
import com.primeid.model.Artifact;
import com.primeid.model.Audit;
import com.primeid.model.Case;
import com.primeid.model.OcrMap;
import com.primeid.model.OcrOutput;
import com.primeid.model.OcrResult;
import com.primeid.model.SessionTable;
import com.primeid.utils.Constants;
import com.primeid.utils.CustomFunctions;
import com.primeid.utils.UznFormat;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Saddam Hussain
 */
public class OCRService {
        
    @Autowired
    private CustomAuthenticationService customAuthenticationService;
    
    @Autowired
    private OcrResultService ocrResultService;
    
    @Autowired
    private AuditService auditService;
    
    @Autowired
    private SessionTableService sessionTableService;
    
    @Autowired
    private CaseService caseService;
    
    @Autowired
    private ArtifactService artifactService;
    
    @Autowired
    private CustomFunctions customFunctions;
    
    private static final long EXPIRY_DURATION = 1;
    
    public HashMap<String, String> OCR(String json, HttpServletRequest request) {
        HashMap<String, String> response = new HashMap<String, String>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldtNow = LocalDateTime.now();
        Gson gson = new Gson();
        Audit audit = new Audit();
        String[] headerKeys = {"token", "caseID", "hasFile"};
        String[] headerValues = customFunctions.getHeaders(request);
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
//        Arrays.stream(getHeaders(request)).forEach(i -> System.out.println(i));

        response = customAuthenticationService.ipAuthentication(json, request, headerParams);
        if (response != null) {
            audit.setResponse(gson.toJson(response));
            auditService.save(audit);
            return response;
        }
        response = customFunctions.validateToken(headerValues[0]);
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
        tokenTable = sessionTableService.loadSessionTableByTokenExpiry(headerValues[0]);
        ldtNow = ldtNow.plusHours(EXPIRY_DURATION);
        tokenTable.setExpiry(ldtNow.format(dtf));
        sessionTableService.update(tokenTable);

        Map<String, Object> map = new HashMap<String, Object>();
        map = (Map<String, Object>) gson.fromJson(json, map.getClass());
        Artifact artifact = artifactService.loadArtifactByArtifactID(Long.parseLong(map.get("artifactKey").toString()));
        if(null == map.get("artifactKey") || artifact.getArtifactID() == 0){
            response = customFunctions.updateTokenResponse("412", Constants.Errors.TWELIVE.toString(), false);
            audit.setResponse(gson.toJson(response));
            auditService.save(audit);
            return response;
        }
        Case caseTable = caseService.loadCaseByCaseID(artifact.getCases().getCaseID());
        if (!(caseTable.getAccounts().getAccountID() == tokenTable.getAccounts().getAccountID())) {
            response = customFunctions.updateTokenResponse("403", Constants.Errors.THREE.toString(), false);
            audit.setResponse(gson.toJson(response));
            auditService.save(audit);
            return response;
        } else {
            OcrMap[] ocrMaps = gson.fromJson(artifact.getOcr_map(), OcrMap[].class);
            UznFormat uzn = new UznFormat();
            String path = artifact.getRepositoryRef();
            String conversionPath = Paths.get("").toAbsolutePath() +"\\ocr_conversions\\";            
            File imageFile  = new File(path);
            String[] ex = imageFile.getName().split("\\.");
            String fileFormat = ex[ex.length-1]; //without type
            String fileName = imageFile.getName().replace(fileFormat, "");
            BufferedImage img = null;
            BufferedImage blackWhite = null;            
            try {
                OcrResult ocr = new OcrResult();
                img = ImageIO.read(imageFile);
                if(uzn.createUZNFile(ocrMaps,conversionPath,fileName)){
                    try {
                        blackWhite = uzn.convertingImage(img, conversionPath, fileName, fileFormat);
                    } catch (Throwable ex1) {
                        response = customFunctions.updateTokenResponse("419", Constants.Errors.NINETEEN.toString(), false);
                        audit.setResponse(gson.toJson(response));
                        auditService.save(audit);
                        return response;
                    }
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
                    file.delete();
                    new File(conversionPath+fileName+fileFormat).delete();
                    return response;                    
                } else {
                    response = customFunctions.updateTokenResponse("418", Constants.Errors.EIGHTEEN.toString(), false);
                    audit.setResponse(gson.toJson(response));
                    auditService.save(audit);
                    return response;
                }                
            } catch (IOException e) {
                response = customFunctions.updateTokenResponse("414", Constants.Errors.FOURTEEN.toString(), false);
                audit.setResponse(gson.toJson(response));
                auditService.save(audit);
                return response;
            }
        }
    }


}
