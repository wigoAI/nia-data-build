package com.wigoai.nlp.example;

import com.google.gson.JsonObject;
import org.junit.Test;
import org.moara.common.data.file.FileUtil;
import org.moara.nia.data.build.compare.CompareJson;
import org.moara.nia.data.build.preprocess.file.JsonFileEditor;

import java.io.File;
import java.util.List;

public class CompareDataTest {

    @Test
    public void compareDataTest() {
        CompareJson compareData = new CompareJson();
        JsonFileEditor jsonFileEditor = new JsonFileEditor();
        String beforePath = "D:\\moara\\data\\allData\\change\\before\\";
        String afterPath = "D:\\moara\\data\\allData\\change\\after\\";

//        List<File> beforeFileList =  FileUtil.getFileList(beforePath, ".json");
        List<File> afterFileList = FileUtil.getFileList(afterPath, ".json");
        for (File file : afterFileList) {
            System.out.println(file.getName());
            JsonObject beforeJson = jsonFileEditor.getJsonObjectByFile(new File(beforePath + file.getName()));
            JsonObject afterJson = jsonFileEditor.getJsonObjectByFile(file);
            compareData.compare(beforeJson, afterJson);

        }

    }

    @Test
    public void changeFileTest() {
        JsonFileEditor jsonFileEditor = new JsonFileEditor();

        for(int i = 0 ; i < 1 ; i++) {
            String afterPath = "D:\\moara\\data\\allData\\NIA_" + (i + 1) + "차_excel\\json\\";

            jsonFileEditor.fileNameChange(FileUtil.getFileList(afterPath,".json"), afterPath);
        }
    }

    @Test
    public void countJsonTest() {
        JsonFileEditor jsonFileEditor = new JsonFileEditor();
        int total = 0;
//        for(int i = 0 ; i < 4 ; i++) {
//            String afterPath = "D:\\moara\\data\\allData\\NIA_" + (i + 1) + "차_excel\\json\\new\\";
//            System.out.println((i + 1) + "차");
//            total += outputFile.jsonCounter(FileUtil.getFileList(afterPath,".json"));
//        }


        for(int i = 0 ; i < 4 ; i++) {
            String afterPath = "D:\\moara\\data\\nia_json\\NIA_" + (i + 1) + "차\\";
            System.out.println((i + 1) + "차");
            total += jsonFileEditor.jsonCounter(FileUtil.getFileList(afterPath,".json"));
        }

        System.out.println("All total : " + total);
    }




}