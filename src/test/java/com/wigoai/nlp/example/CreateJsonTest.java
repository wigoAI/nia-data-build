package com.wigoai.nlp.example;

import org.junit.Assert;
import org.junit.Test;
import org.moara.nia.data.build.preprocess.*;

public class CreateJsonTest {

//    『』

    @Test
    public void createJsonTestByTestWorks() {
//        String dirPath ="D:\\moara\\data\\allData\\";
//        DataPreprocessor dataPreprocessor = new DataPreprocessorImpl();
//        for(int i = 4; i < 6 ; i++) {
//            dataPreprocessor.makeByPath(dirPath + "NIA_" + (i + 1) + "차_excel\\");
//        }

        String dirPath ="D:\\moara\\data\\allData\\기고문\\";
        DataPreprocessor dataPreprocessor = new DataPreprocessorImpl();

        dataPreprocessor.makeByPath(dirPath);

    }

    @Test
    public void createTestJsonTest() {
        String dirPath ="D:\\moara\\data\\allData\\test\\기고test\\";

        DataPreprocessor dataPreprocessor = new DataPreprocessorImpl();

        dataPreprocessor.makeByPath(dirPath);
    }

    @Test
    public void createXmlJsonTest() {
        String dirPath = "D:\\moara\\data\\law\\Data\\test\\";
        XmlPreprocessor xmlPreprocessor = new XmlPreprocessor();

        xmlPreprocessor.makeByPath(dirPath);

    }
}
