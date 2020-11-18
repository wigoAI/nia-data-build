package com.wigoai.nlp.test;

import org.junit.Test;
import org.moara.common.data.file.FileUtil;
import org.moara.nia.data.build.preprocess.*;
import org.moara.nia.data.build.preprocess.fileUtils.excel.ExcelCounter;
import org.moara.nia.data.build.preprocess.fileUtils.json.*;

import java.io.File;
import java.util.HashSet;
import java.util.List;

public class CreateJsonTest {

//    『』

    @Test
    public void testCreateJson() {
//        String dirPath ="D:\\moara\\data\\allData\\";
//        DataPreprocessor dataPreprocessor = new DataPreprocessorImpl();
//        for(int i = 4; i < 6 ; i++) {
//            dataPreprocessor.makeByPath(dirPath + "NIA_" + (i + 1) + "차_excel\\");
//        }
        String dirPath ="D:\\moara\\data\\기고문_2차\\";
        DataPreprocessor dataPreprocessor = new DataPreprocessorImpl();

        dataPreprocessor.makeByPath(dirPath);

    }

    @Test
    public void testCreateXmlJson() {
        String dirPath = "D:\\moara\\data\\law\\Data\\2019\\";
        String outputPath = "D:\\moara\\data\\law\\test";

        XmlPreprocessor xmlPreprocessor = new XmlPreprocessor(outputPath);
        xmlPreprocessor.makeByPath(dirPath);

    }

    @Test
    public void testCreatXmlJsonByFileList() {
        String dirPath = "D:\\moara\\data\\law\\data_1947-2020\\";
        String outputPath = "D:\\moara\\data\\law\\test";
        XmlPreprocessor xmlPreprocessor = new XmlPreprocessor(outputPath);

        for(int i = 1947 ; i <= 2020 ; i++) {
            String path = dirPath + i + "\\";

            xmlPreprocessor.makeByPath(path);
        }

        for (String str : xmlPreprocessor.getSplitStrSet()) {
            System.out.println(str);
        }


    }

    @Test
    public void testJsonEdit() {
        JsonFileEditor jsonFileEditor = new JsonFileEditor();

//        for(int i = 0 ; i < 6 ; i++) {
//            String path = "D:\\moara\\data\\allData\\NIA_" + (i + 1) + "차_excel\\json\\";
//            jsonFileEditor.editJsonFileByPath(path);
//        }
        String path = "D:\\moara\\data\\기고문_2차\\json\\";
        jsonFileEditor.editJsonFileByPath(path);

    }

    @Test
    public void testJsonHighlight() {
        JsonFileEditor jsonFileEditor = new JsonFileHighlighter();

        String path = "D:\\moara\\data\\기고문_2차\\json\\edit\\new\\";
        List<File> fileList = FileUtil.getFileList(path, ".json");

        for(File file : fileList) {
            jsonFileEditor.editJsonFile(file, path);
        }
    }

    @Test
    public void testLawJsonEdit() {
        LawJsonFileEditor lawJsonFileEditor = new LawJsonFileEditor();

        String path = "D:\\moara\\data\\law\\json5\\";
        lawJsonFileEditor.editJsonFileByPath(path);

    }

    @Test
    public void testJsonClassify() {
        JsonFileClassifier jsonFileClassifier = new JsonFileClassifier();

        String path = "D:\\moara\\data\\allData\\기고문\\json\\new\\edit\\new\\";
        jsonFileClassifier.classifyJsonFileByPath(path);

    }

    @Test
    public void testJsonByIndexClassify() {
        JsonFileClassifier jsonFileClassifier = new JsonFileClassifier();
        String path = "D:\\moara\\data\\law\\json5\\edit\\highlight\\";

//        for(int i = 0 ; i < 7 ; i++) {
//            jsonFileClassifier.classifyJsonFileByIndex(path, i, i);
//        }
        jsonFileClassifier.classifyJsonFileByIndex(path, 13, 15);
        jsonFileClassifier.classifyJsonFileByIndex(path, 16, 19);
        jsonFileClassifier.classifyJsonFileByIndex(path, 20, 25);
        jsonFileClassifier.classifyJsonFileByIndex(path, 26, 29);
        for (int i = 30 ; i < 230 ; i += 10) {
            jsonFileClassifier.classifyJsonFileByIndex(path, i, i + 9);

        }

    }

    @Test
    public void testDocumentsDrop() {

        ExcelCounter excelCounter = new ExcelCounter();

        for(int i = 3 ; i < 6 ; i++) {
            String path = "D:\\moara\\data\\allData\\NIA_" + (i + 1) + "차_excel\\";
            HashSet<String> dropData = excelCounter.countByPath(path);
            JsonFileEditor jsonFileEditor = new JsonFileEditor(dropData);
            jsonFileEditor.editJsonFileByPath(path);
        }
    }

    @Test
    public void testDocumentCut() {
        JsonFileEditor jsonFileEditor = new JsonFileCutter();
        String path = "D:\\moara\\data\\잡지\\json\\";

        jsonFileEditor.editJsonFileByPath(path);


    }
}
