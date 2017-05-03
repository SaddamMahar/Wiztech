package com.primeid.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Saddam Hussain
 */
public class UploadArtifactJson {
    private String type;
    private String location;
    private Meta meta;
    private List<OcrMap> ocr_map = new ArrayList<>();

    public List<OcrMap> getOcr_map() {
        return ocr_map;
    }

    public void setOcr_map(List<OcrMap> ocr_map) {
        this.ocr_map = ocr_map;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
    
}
