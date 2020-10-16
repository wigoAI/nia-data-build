package org.moara.nia.data.build.preprocess.file;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class JsonFileEditor {
    public void fileNameChange(List<File> fileList, String path) {
        for(File file : fileList) {
            JsonObject jsonObject = getJsonObjectByFile(file);
            JsonArray documents = jsonObject.getAsJsonArray("documents");

            String fileName = file.getName();
            fileName = fileName.substring(0,fileName.lastIndexOf('.'));

            System.out.println("original : " + fileName);
            int documentsSize = documents.size();
            String newFileName = "";
            if(fileName.endsWith("_")) {
                String[] splitFileName = fileName.split("_");

                for(int i = 0 ; i < splitFileName.length - 1 ; i++) {
                    newFileName += splitFileName[i] + "_";
                }

                newFileName += documentsSize +"건_";
            } else {
                newFileName = fileName + "_" + documentsSize + "건_";
            }

            System.out.println("new : " + newFileName);

            File newFile = new File(path + "\\new\\" + newFileName + ".json");

            try {
                FileUtils.copyFile(file, newFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public JsonObject getJsonObjectByFile(File file) {
        JsonElement element = null;
        try {
            element = JsonParser.parseReader(new FileReader(file.getPath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return element.getAsJsonObject();
    }


    public int jsonCounter(List<File> fileList) {
        int total = 0;
        for(File file : fileList) {
            JsonObject jsonObject = getJsonObjectByFile(file);
            JsonArray documents = jsonObject.getAsJsonArray("documents");

            int documentsSize = documents.size();
            System.out.println(file.getName() + " : " + documentsSize);
            total += documentsSize;
        }
        System.out.println("total : " + total);
        return total;
    }
}
