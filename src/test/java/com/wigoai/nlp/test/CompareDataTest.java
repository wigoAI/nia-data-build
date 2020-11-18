package com.wigoai.nlp.test;

import com.google.gson.JsonObject;
import org.junit.Test;
import org.moara.common.data.file.FileUtil;
import org.moara.nia.data.build.preprocess.fileUtils.json.CompareJson;
import org.moara.nia.data.build.preprocess.fileUtils.excel.ExcelCounter;
import org.moara.nia.data.build.preprocess.fileUtils.json.JsonFileClassifier;
import org.moara.nia.data.build.preprocess.fileUtils.json.JsonFileEditor;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CompareDataTest {

    @Test
    public void testCompareData() {

        JsonFileEditor jsonFileEditor = new JsonFileEditor();
//        String beforePath = "D:\\moara\\data\\allData\\change\\before\\";
//        String afterPath = "D:\\moara\\data\\allData\\change\\after\\";
        String basePath = "D:\\moara\\data\\allData\\NIA_6차_excel\\";
        String beforePath = basePath + "json\\";
        String afterPath =  basePath + "edit\\";

        List<File> afterFileList = FileUtil.getFileList(afterPath, ".json");
        for (File file : afterFileList) {
            System.out.println(file.getName());
            JsonObject beforeJson = jsonFileEditor.getJsonObjectByFile(new File(beforePath + file.getName()));
            JsonObject afterJson = jsonFileEditor.getJsonObjectByFile(file);
            CompareJson compareData = new CompareJson(beforeJson, afterJson, basePath);
            try {
                compareData.compare();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    @Test
    public void testChangeFile() {
        JsonFileClassifier jsonFileClassifier = new JsonFileClassifier();

//        for(int i = 3 ; i < 6 ; i++) {
//            String afterPath = "D:\\moara\\data\\allData\\NIA_" + (i + 1) + "차_excel\\edit\\";
//
//            jsonFileEditor.fileNameChangeByJsonSize(FileUtil.getFileList(afterPath,".json"), afterPath);
//        }
        String afterPath = "D:\\moara\\data\\기고문_2차\\json\\edit\\";

        jsonFileClassifier.changeFileNameByJsonSize(FileUtil.getFileList(afterPath,".json"), afterPath);

    }

    @Test
    public void testCountJson() {
        JsonFileClassifier jsonFileClassifier = new JsonFileClassifier();
        int total = 0;
//        for(int i = 3 ; i < 6 ; i++) {
//            String afterPath = "D:\\moara\\data\\allData\\NIA_" + (i + 1) + "차_excel\\edit\\new\\";
//            System.out.println((i + 1) + "차");
//            total += jsonFileEditor.countJson(FileUtil.getFileList(afterPath,".json"));
//        }

        String afterPath = "D:\\moara\\data\\기고문_2차\\json\\edit\\";

        total += jsonFileClassifier.countJson(FileUtil.getFileList(afterPath,".json"));

        System.out.println("All total : " + total);
    }

    @Test
    public void testCountCategory() {
        JsonFileClassifier jsonFileClassifier = new JsonFileClassifier();
        int total = 0;

        String afterPath = "D:\\moara\\data\\allData\\change\\";

        total += jsonFileClassifier.countJson(FileUtil.getFileList(afterPath,".json"), "category", "스포츠");

        System.out.println("All total : " + total);
    }

    @Test
    public void testCountExcelData() {
        ExcelCounter excelCounter = new ExcelCounter();

        for (int i = 4 ; i <= 6 ; i++) {
            String path = "D:\\moara\\data\\allData\\NIA_" + i + "차_excel\\";
            for (String id : excelCounter.countByPath(path)) {
                System.out.println(id);
            }
        }
    }

    @Test
    public void testStatisticJson() {
        JsonFileClassifier jsonFileEditor = new JsonFileClassifier();
        String path = "D:\\moara\\data\\law\\json5\\edit\\highlight\\";

        int from = 0;
        int totalFrom = jsonFileEditor.countJsonIndex(FileUtil.getFileList(path,".json"), from);
        System.out.println("total from " + from + " : " + totalFrom);

    }



}