package com.wigoai.nlp.example;

import com.google.gson.JsonObject;
import org.junit.Test;
import org.moara.common.data.file.FileUtil;
import org.moara.nia.data.build.compare.CompareData;
import org.moara.nia.data.build.compare.CompareJson;
import org.moara.nia.data.build.preprocess.file.OutputFile;

import java.io.File;

public class CompareDataTest {

    @Test
    public void compareDataTest() {
        CompareJson compareData = new CompareJson();
        String beforePath = "D:\\moara\\data\\allData\\change\\before\\강원일보_20200720_161736_6072건_.json";
        String afterPath = "D:\\moara\\data\\allData\\change\\after\\a.json";
        JsonObject beforeJson = compareData.getJsonObjectByFile(new File(beforePath));
        JsonObject afterJson = compareData.getJsonObjectByFile(new File(afterPath));



        compareData.compare(beforeJson, afterJson);

    }

    @Test
    public void changeFileTest() {
        OutputFile outputFile = new OutputFile();
        String afterPath = "D:\\moara\\data\\allData\\change\\after";
        outputFile.fileNameChange(FileUtil.getFileList(afterPath,".json"), afterPath);
    }




}