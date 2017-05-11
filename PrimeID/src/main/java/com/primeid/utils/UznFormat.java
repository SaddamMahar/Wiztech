package com.primeid.utils;

import com.google.gson.Gson;
import com.primeid.model.OcrMap;
import com.primeid.model.OcrOutput;
import java.awt.Color;
import java.awt.Graphics;
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
import java.util.ArrayList;
import java.util.Arrays;
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
        String newPath = conversionPath + fileName + "uzn";
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

    public BufferedImage convertingImage(BufferedImage oFile, String conversionPath, String fileName) throws Throwable {

//        File file = new File(path);
        BufferedImage orginalImage;
        try {
            BufferedImage blackAndWhiteImg;
//            orginalImage = ImageIO.read(oFile);
            blackAndWhiteImg = changeBrightness(oFile);
            ImageIO.write(blackAndWhiteImg, "png", new File(conversionPath + fileName));

            return blackAndWhiteImg;
        } catch (IOException ex) {
            Logger.getLogger(UznFormat.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public BufferedImage changeBrightness(BufferedImage src) throws IOException, Throwable {
        if (src.getWidth() < 2000) {
            src = getScaledImage(src, 2000, 1266);
        } else {
            src = getScaledImage(src, src.getWidth(), src.getHeight());
        }
        BufferedImage image;
        // Brighten the image by 20%
        float scaleFactor = 2.3f;
        RescaleOp opt;
        Graphics2D gr;
        if (src.getWidth() == 2000) {
            image = new BufferedImage(2000, 1266, BufferedImage.TYPE_BYTE_GRAY);
            gr = (Graphics2D) image.getGraphics();
            gr.drawImage(src, 0, 0, null);
            gr.dispose();
            opt = new RescaleOp(scaleFactor, 0, null);
            image = opt.filter(image, null);

            return medianFilter(image);
        }
        image = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        gr = (Graphics2D) image.getGraphics();
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

    public static List<OcrOutput> tess4jText(BufferedImage bi, File uznFile) {
        List<OcrOutput> ocr_results = new ArrayList<>();
        Tesseract tesseract = Tesseract.getInstance();  // JNA Interface Mapping
//        File tessData = LoadLibs.extractTessResources("tessData");
        tesseract.setDatapath("C:\\Users\\Administrator\\Downloads\\tessdata");
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
                System.out.println("OCR result for " + tag + " = " + ocrResult + "\n\n");
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

    public BufferedImage medianFilter(BufferedImage img) throws Throwable {
        Color[] pixel = new Color[9];
        int[] R = new int[9];
        int[] B = new int[9];
        int[] G = new int[9];
        File output = new File("D:\\2017\\Novigi\\ocr\\images\\conversion\\output.jpg");
        for (int i = 1; i < img.getWidth() - 1; i++) {
            for (int j = 1; j < img.getHeight() - 1; j++) {
                pixel[0] = new Color(img.getRGB(i - 1, j - 1));
                pixel[1] = new Color(img.getRGB(i - 1, j));
                pixel[2] = new Color(img.getRGB(i - 1, j + 1));
                pixel[3] = new Color(img.getRGB(i, j + 1));
                pixel[4] = new Color(img.getRGB(i + 1, j + 1));
                pixel[5] = new Color(img.getRGB(i + 1, j));
                pixel[6] = new Color(img.getRGB(i + 1, j - 1));
                pixel[7] = new Color(img.getRGB(i, j - 1));
                pixel[8] = new Color(img.getRGB(i, j));
                for (int k = 0; k < 9; k++) {
                    R[k] = pixel[k].getRed();
                    B[k] = pixel[k].getBlue();
                    G[k] = pixel[k].getGreen();
                }
                Arrays.sort(R);
                Arrays.sort(G);
                Arrays.sort(B);
                img.setRGB(i, j, new Color(R[4], B[4], G[4]).getRGB());
            }
        }
        return img;
    }
}
