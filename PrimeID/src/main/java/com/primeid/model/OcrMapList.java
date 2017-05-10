/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.primeid.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Saddam Hussain
 */
public class OcrMapList {
    private List<OcrMap> ocr_map = new ArrayList<>();

    public List<OcrMap> getOcr_map() {
        return ocr_map;
    }

    public void setOcr_map(List<OcrMap> ocr_map) {
        this.ocr_map = ocr_map;
    }
    
}
