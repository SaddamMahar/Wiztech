package com.primeid.utils;

import com.primeid.model.OcrMap;
import com.primeid.model.OcrOutput;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
        //uzn file location
        String uznLocation = conversionPath + fileName + "uzn";
        File imageFile = new File(uznLocation);
        try {
            imageFile.createNewFile();
            FileWriter fw = new FileWriter(imageFile.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            for (OcrMap ocr : ocrMaps) {
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

    public BufferedImage convertingImage(BufferedImage oFile, String conversionPath, String fileName, String format) throws Throwable {
        try {
            BufferedImage blackAndWhiteImg = changeBrightness(oFile);
            ImageIO.write(blackAndWhiteImg, format, new File(conversionPath + fileName+format));
            return blackAndWhiteImg;
        } catch (IOException ex) {
            return null;
        }
    }

    public BufferedImage changeBrightness(BufferedImage src) throws IOException, Throwable {
        BufferedImage image;
        float scaleFactor = 2.3f;
        RescaleOp opt;
        Graphics2D gr;
        if (src.getWidth() < 2000) {
            src = getScaledImage(src, 2000, 1266);
            image = new BufferedImage(2000, 1266, BufferedImage.TYPE_BYTE_GRAY);
            gr = (Graphics2D) image.getGraphics();
            gr.drawImage(src, 0, 0, null);
            gr.dispose();
            opt = new RescaleOp(scaleFactor, 0, null);
            image = opt.filter(image, null);
            return image;
        } else {
            src = getScaledImage(src, src.getWidth(), src.getHeight());
            image = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            gr = (Graphics2D) image.getGraphics();
            gr.drawImage(src, 0, 0, null);
            gr.dispose();
            opt = new RescaleOp(scaleFactor, 0, null);
            image = opt.filter(image, null);
            return image;
        }
    }

    public String getText(BufferedImage bi) {
        ITesseract instance = new Tesseract(); // JNA Direct Mapping
//        File tessData = LoadLibs.extractTessResources("tessData");
        instance.setDatapath(Paths.get("").toAbsolutePath().toString());
        instance.setLanguage("eng");
        try {
            String result = instance.doOCR(bi);
            return result;
        } catch (TesseractException e) {
            return null;
        }
    }

    public static List<OcrOutput> tess4jText(BufferedImage bi, File uznFile) {
        List<OcrOutput> ocr_results = new ArrayList<>();
        Tesseract tesseract = Tesseract.getInstance();  // JNA Interface Mapping
//        File tessData = LoadLibs.extractTessResources("tessData");
        tesseract.setDatapath(Paths.get("").toAbsolutePath().toString());
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
                ocr_Output.setResult(ocrResult);
                ocr_results.add(ocr_Output);
                
                line = in.readLine();
            }
        } catch (IOException | TesseractException e) {
            return null;
        }
        return ocr_results;
    }

    public BufferedImage getScaledImage(BufferedImage image, int width, int height) throws IOException {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        double scaleX = (double) width / imageWidth;
        double scaleY = (double) height / imageHeight;
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
        AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BICUBIC);
        return bilinearScaleOp.filter(
                image,
                new BufferedImage(width, height, image.getType()));
    }
}
