package com.wigoai.nlp.example;

import org.junit.Assert;
import org.junit.Test;
import org.moara.nia.data.build.preprocess.*;

public class CreateJsonTest {

//    『』

    @Test
    public void createJsonTestByTestWorks() {
        String dirPath ="D:\\moara\\data\\allData\\";
        DataPreprocessor dataPreprocessor = new DataPreprocessorImpl();
        for(int i = 0; i < 6 ; i++) {
            dataPreprocessor.makeByPath(dirPath + "NIA_" + (i + 1) + "차_excel\\");
        }


    }

    @Test
    public void createTestJsonTest() {
        String dirPath ="D:\\moara\\data\\allData\\test\\";

        DataPreprocessor dataPreprocessor = new DataPreprocessorImpl();

        dataPreprocessor.makeByPath(dirPath);
    }


}
