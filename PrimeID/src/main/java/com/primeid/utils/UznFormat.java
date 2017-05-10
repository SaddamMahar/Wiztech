package com.primeid.utils;

import com.google.gson.Gson;
import com.primeid.model.OcrMap;
import com.primeid.model.OcrOutput;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

/**
 *
 * @author Saddam Hussain
 */
public class UznFormat {

    public boolean createUZNFile(OcrMap[] ocrMaps, String conversionPath, String fileName) throws IOException {
        String newPath = conversionPath+ fileName+"uzn";
        System.out.println("new path : " + newPath);
        File imageFile = new File(newPath);
        Gson gson = new Gson();

        try {
            imageFile.createNewFile();
            FileWriter fw = new FileWriter(imageFile.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            for (OcrMap ocr : ocrMaps) {
                System.out.println(" OcrMap : " + gson.toJson(ocr));
                bw.write(ocr.getX() + "\t" + ocr.getY() + "\t");
                bw.write(ocr.getW() + "\t" + ocr.getH() + "\t");
                bw.write(ocr.getTitle() + "\n");
            }
            bw.close();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public BufferedImage convertingImage(BufferedImage oFile,String conversionPath, String fileName) {
        System.out.println("conversionPath+fileName :: "+conversionPath);
//        File file = new File(path);
        BufferedImage orginalImage;
        try {
            BufferedImage blackAndWhiteImg;
//            orginalImage = ImageIO.read(oFile);
            blackAndWhiteImg = changeBrightness(oFile);
            ImageIO.write(blackAndWhiteImg, "png", new File(conversionPath+fileName));
            
            return blackAndWhiteImg;
        } catch (IOException ex) {
            Logger.getLogger(UznFormat.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public BufferedImage changeBrightness(BufferedImage src) {
        BufferedImage image;
        BufferedImage newImage;
        // Brighten the image by 20%
        float scaleFactor = 2.2f;
        RescaleOp opt;
        Graphics gr;
        if (src.getWidth() < 2000) {
            image = new BufferedImage(2000, 1000, BufferedImage.TYPE_BYTE_GRAY);
            gr = image.getGraphics();
            gr.drawImage(src, 0, 0, 2000, 1000, null);
            gr.dispose();
            opt = new RescaleOp(scaleFactor, 0, null);
            image = opt.filter(image, null);
            return image;

        }
        image = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        gr = image.getGraphics();
        gr.drawImage(src, 0, 0, null);
        gr.dispose();
        opt = new RescaleOp(scaleFactor, 0, null);
        image = opt.filter(image, null);
        return image;
    }

    public String getText(BufferedImage bi) {
        ITesseract instance = new Tesseract(); // JNA Direct Mapping
//        File tessData = LoadLibs.extractTessResources("tessData");
        instance.setDatapath("C:\\Users\\Administrator\\Downloads");
        instance.setLanguage("eng");
        try {
            String result = instance.doOCR(bi);
            System.out.println(":::" + result);
            return result;
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
    
    public static List<OcrOutput> tess4jText(BufferedImage bi , File uznFile){
        List<OcrOutput> ocr_results = new ArrayList<>();
        Tesseract tesseract = Tesseract.getInstance();  // JNA Interface Mapping
//        File tessData = LoadLibs.extractTessResources("tessData");
        tesseract.setDatapath("C:\\Users\\Administrator\\Downloads");
        tesseract.setLanguage("eng");
        tesseract.setPageSegMode(ITessAPI.TessPageSegMode.PSM_SINGLE_COLUMN);
        
 
        try (BufferedReader in = new BufferedReader(new FileReader(uznFile))) {
            String line = in.readLine();
            while (line != null) {   
                line = line.trim();
                OcrOutput ocr_Output = new OcrOutput();
                String[] splitArray = line.split("\\s+");
 
                int x = Integer.parseInt(splitArray[0]);
                int y = Integer.parseInt(splitArray[1]);
                int width = Integer.parseInt(splitArray[2]);
                int height = Integer.parseInt(splitArray[3]);
                String tag = splitArray[4];
                ocr_Output.setRegion_name(tag);
 
                Rectangle rect = new Rectangle(x, y, width, height);
                String ocrResult = tesseract.doOCR(bi, rect);
                ocr_Output.setRegion_name(ocrResult);
                System.out.println("OCR result for " + tag + " = " + ocrResult+ "\n\n");
                ocr_results.add(ocr_Output);
                line = in.readLine();
            }
        } catch (IOException | TesseractException e) {
            return null;
        }
        return ocr_results;
    }
}
