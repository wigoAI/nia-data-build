package com.wigoai.nlp.example;

import com.google.gson.JsonObject;
import org.junit.Test;
import org.moara.common.data.file.FileUtil;
import org.moara.nia.data.build.compare.BlindCompare;
import org.moara.nia.data.build.compare.CompareJson;
import org.moara.nia.data.build.preprocess.ExcelCounter;
import org.moara.nia.data.build.preprocess.file.JsonFileEditor;

import java.io.File;
import java.util.List;

public class CompareDataTest {

    @Test
    public void compareDataTest() {

        JsonFileEditor jsonFileEditor = new JsonFileEditor();
//        String beforePath = "D:\\moara\\data\\allData\\change\\before\\";
//        String afterPath = "D:\\moara\\data\\allData\\change\\after\\";
        String beforePath = "D:\\moara\\data\\allData\\change\\edit\\before\\";
        String afterPath = "D:\\moara\\data\\allData\\change\\edit\\after\\";

//        List<File> beforeFileList =  FileUtil.getFileList(beforePath, ".json");
        List<File> afterFileList = FileUtil.getFileList(afterPath, ".json");
        for (File file : afterFileList) {
            System.out.println(file.getName());
            JsonObject beforeJson = jsonFileEditor.getJsonObjectByFile(new File(beforePath + file.getName()));
            JsonObject afterJson = jsonFileEditor.getJsonObjectByFile(file);
            CompareJson compareData = new CompareJson(beforeJson, afterJson);
            compareData.compare();

        }

    }

    @Test
    public void changeFileTest() {
        JsonFileEditor jsonFileEditor = new JsonFileEditor();

        for(int i = 3 ; i < 6 ; i++) {
            String afterPath = "D:\\moara\\data\\allData\\NIA_" + (i + 1) + "차_excel\\edit\\";

            jsonFileEditor.fileNameChangeByJsonSize(FileUtil.getFileList(afterPath,".json"), afterPath);
        }
//        String afterPath = "D:\\moara\\data\\law\\json\\edit\\";
//
//        jsonFileEditor.fileNameChange(FileUtil.getFileList(afterPath,".json"), afterPath);

    }

    @Test
    public void countJsonTest() {
        JsonFileEditor jsonFileEditor = new JsonFileEditor();
        int total = 0;
        for(int i = 3 ; i < 6 ; i++) {
            String afterPath = "D:\\moara\\data\\allData\\NIA_" + (i + 1) + "차_excel\\edit\\new\\";
            System.out.println((i + 1) + "차");
            total += jsonFileEditor.countJson(FileUtil.getFileList(afterPath,".json"));
        }

//        String afterPath = "D:\\moara\\data\\law\\json\\edit\\new\\";
//
//        total += jsonFileEditor.jsonCounter(FileUtil.getFileList(afterPath,".json"));

        System.out.println("All total : " + total);
    }

    @Test
    public void countCategoryTest() {
        JsonFileEditor jsonFileEditor = new JsonFileEditor();
        int total = 0;

        String afterPath = "D:\\moara\\data\\allData\\change\\";

        total += jsonFileEditor.countJson(FileUtil.getFileList(afterPath,".json"), "category", "스포츠");

        System.out.println("All total : " + total);
    }

    @Test
    public void blindCompareTest() {
        JsonFileEditor jsonFileEditor = new JsonFileEditor();

        String beforePath = "D:\\moara\\data\\law\\compare\\blind\\before\\";
        String afterPath = "D:\\moara\\data\\law\\compare\\blind\\after\\";

        List<File> afterFileList = FileUtil.getFileList(afterPath, ".json");
        for (File file : afterFileList) {
            System.out.println(file.getName());
            JsonObject beforeJson = jsonFileEditor.getJsonObjectByFile(new File(beforePath + file.getName()));
            JsonObject afterJson = jsonFileEditor.getJsonObjectByFile(file);

            BlindCompare blindCompare = new BlindCompare(beforeJson, afterJson);
            System.out.println(blindCompare.compare());


        }

    }

    @Test
    public void countExcelDataTest() {
        ExcelCounter excelCounter = new ExcelCounter();

        for (int i = 4 ; i <= 6 ; i++) {
            String path = "D:\\moara\\data\\allData\\NIA_" + i + "차_excel\\";
            for (String id : excelCounter.countByPath(path)) {
                System.out.println(id);
            }
        }
    }


}