package com.wigoai.nlp.example;

import org.junit.Test;
import org.moara.nia.data.build.preprocess.*;
import org.moara.nia.data.build.preprocess.file.JsonFileClassifier;
import org.moara.nia.data.build.preprocess.file.JsonFileEditor;
import org.moara.nia.data.build.preprocess.file.LawJsonFileEditor;

import java.util.HashSet;

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
        String dirPath = "D:\\moara\\data\\law\\data_1947-2020\\";
        XmlPreprocessor xmlPreprocessor = new XmlPreprocessor();

        for(int i = 1947 ; i <= 2020 ; i++) {
            String path = dirPath + i + "\\";

            xmlPreprocessor.makeByPath(path);
        }
    }

    @Test
    public void editJsonTest() {
        JsonFileEditor jsonFileEditor = new JsonFileEditor();

        for(int i = 0 ; i < 6 ; i++) {
            String path = "D:\\moara\\data\\allData\\NIA_" + (i + 1) + "차_excel\\json\\";
            jsonFileEditor.editJsonFileByPath(path);
        }

//
//        String path = "D:\\moara\\data\\allData\\test\\jsonDelTest\\";
//        jsonFileEditor.editJsonFileByPath(path);


    }

    @Test
    public void editLawJsonTest() {
        LawJsonFileEditor lawJsonFileEditor = new LawJsonFileEditor();


        String path = "D:\\moara\\data\\law\\json3\\";
        lawJsonFileEditor.editJsonFileByPath(path);


    }

    @Test
    public void classifyJsonTest() {
        JsonFileClassifier jsonFileClassifier = new JsonFileClassifier();

        String path = "D:\\moara\\data\\allData\\기고문\\json\\new\\edit\\new\\";
        jsonFileClassifier.classifyJsonFileByPath(path);

    }

    @Test
    public void classifyJsonByIndexTest() {
        JsonFileClassifier jsonFileClassifier = new JsonFileClassifier();
        String path = "D:\\moara\\data\\law\\json3\\edit\\";

        jsonFileClassifier.classifyJsonFileByIndex(path, 11, 119);
    }

    @Test
    public void dropDocumentsTest() {

        ExcelCounter excelCounter = new ExcelCounter();

        for(int i = 3 ; i < 6 ; i++) {
            String path = "D:\\moara\\data\\allData\\NIA_" + (i + 1) + "차_excel\\";
            HashSet<String> dropData = excelCounter.countByPath(path);
            JsonFileEditor jsonFileEditor = new JsonFileEditor(dropData);
            jsonFileEditor.editJsonFileByPath(path);
        }
    }
}
