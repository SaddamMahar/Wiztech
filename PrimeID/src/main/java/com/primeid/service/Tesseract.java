//package com.primeid.service;
//
//import javax.validation.constraints.AssertTrue;
//import org.bytedeco.javacpp.*;
//
//import static org.bytedeco.javacpp.lept.*;
//import static org.bytedeco.javacpp.tesseract.*;
//
//public class Tesseract {
//    
//    public void givenTessBaseApi_whenImageOcrd_thenTextDisplayed() throws Exception {
//        BytePointer outText;
//
//        TessBaseAPI api = new TessBaseAPI();
//        // Initialize tesseract-ocr with English, without specifying tessdata path
//        if (api.Init(".", "ENG") != 0) {
//            System.err.println("Could not initialize tesseract.");
//            System.exit(1);
//        }
//
//        // Open input image with leptonica library
//        PIX image = pixRead("C:\\java\\tomcat\\bin\\uploads\\abmessungen2.png");
//        api.SetImage(image);
//        // Get OCR result
//        outText = api.GetUTF8Text();
//        String string = outText.getString();
////        AssertTrue(!string.isEmpty());
//        System.out.println("OCR output:\n" + string);
//
//        // Destroy used object and release memory
//        api.End();
//        outText.deallocate();
//        pixDestroy(image);
//    }
//}