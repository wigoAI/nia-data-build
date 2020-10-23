package com.wigoai.nlp.example;

import org.junit.Assert;
import org.junit.Test;
import org.moara.nia.data.build.preprocess.*;
import org.moara.nia.data.build.preprocess.file.JsonFileEditor;

import java.io.File;

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
        String dirPath = "D:\\moara\\data\\law\\Data\\2019\\";
        XmlPreprocessor xmlPreprocessor = new XmlPreprocessor();
        xmlPreprocessor.makeByPath(dirPath);

    }

    @Test
    public void creatXmlJsonByFileListTest() {
        String dirPath = "D:\\moara\\data\\law\\Data\\";
        XmlPreprocessor xmlPreprocessor = new XmlPreprocessor();

        for(int i = 2019 ; i > 1999 ; i--) {
            String path = dirPath + i + "\\";

            xmlPreprocessor.makeByPath(path);
        }
    }

    @Test
    public void editJsonTest() {
        JsonFileEditor jsonFileEditor = new JsonFileEditor();

//        for(int i = 0 ; i < 6 ; i++) {
//            String path = "D:\\moara\\data\\allData\\NIA_" + (i + 1) + "차_excel\\json\\new\\";
//            jsonFileEditor.editJsonFileByPath(path);
//        }


        String path = "D:\\moara\\data\\allData\\잡지\\json\\";
        jsonFileEditor.editJsonFileByPath(path);


    }

    @Test
    public void classifyJsonTest() {
        JsonFileEditor jsonFileEditor = new JsonFileEditor();

        String path = "D:\\moara\\data\\allData\\기고문\\json\\new\\edit\\new\\";
        jsonFileEditor.classifyJsonFileByPath(path);
    }
}
