/*
 * Copyright (C) 2020 Wigo Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

/**
 * JSON 파일 편집기
 * 데이터 정제 결과로 도출된 JSON 파일의 관리를 한다.
 *
 * TODO 1. 유동적이지 않은 구조 개선하기
 *
 * @author 조승현
 */
public class JsonFileEditor {

    /**
     *
     * Json 파일의 이름을 데이터 정제 이후 변경된 수에 따라서 변경
     *
     * @param fileList List<File>
     * @param outputPath String
     */
    public void fileNameChange(List<File> fileList, String outputPath) {
        for(File file : fileList) {
            JsonObject jsonObject = getJsonObjectByFile(file);
            JsonArray documents = jsonObject.getAsJsonArray("documents");

            String fileName = file.getName();
            fileName = fileName.substring(0,fileName.lastIndexOf('.'));

            String newFileName = getNewNameByJsonSize(documents, fileName);

            System.out.println("new : " + newFileName);

            File newFile = new File(outputPath + "\\new\\" + newFileName + ".json");

            try {
                FileUtils.copyFile(file, newFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private String getNewNameByJsonSize(JsonArray jsonArray, String oldName) {
        System.out.println("original : " + oldName);
        int jsonArraySize = jsonArray.size();
        StringBuilder newFileName = new StringBuilder();
        if(oldName.endsWith("_")) {
            String[] splitFileName = oldName.split("_");

            for(int i = 0 ; i < splitFileName.length - 1 ; i++) {
                newFileName.append(splitFileName[i]).append("_");
            }

            newFileName.append(jsonArraySize).append("건_");
        } else {
            newFileName = new StringBuilder(oldName + "_" + jsonArraySize + "건_");
        }

        return newFileName.toString();
    }

    /**
     * File 객체로부터 JsonObject 를 생성한다.
     *
     * @param file File
     * @return JsonObject
     */
    public JsonObject getJsonObjectByFile(File file) {
        JsonElement element = null;
        try {
            element = JsonParser.parseReader(new FileReader(file.getPath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return element.getAsJsonObject();
    }

    /**
     * Json 파일에서 데이터의 갯수를 얻는다.
     *
     * @param fileList List<File>
     * @return int
     */
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

    public String getFileNameWithoutFormat(File file) {
        String fileName = file.getName();
        return fileName.substring(0, fileName.lastIndexOf("."));
    }
}
