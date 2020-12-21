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
package org.moara.nia.data.build.preprocess.fileUtils.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.seomse.commons.utils.FileUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * JSON 파일 분류기
 *
 * @author wjrmffldrhrl
 */
public class JsonFileClassifier extends JsonFileUtil{
    private static final Logger logger = LoggerFactory.getLogger(JsonFileClassifier.class);
    /**
     *
     * Json 파일에서 데이터의 갯수를 얻는다.
     *
     * @param fileList List<File>
     * @return int
     */
    public int countJson(List<File> fileList) {
        int total = 0;

        int index = 0;
        for(JsonArray documents : getDocumentsArrayByFileList(fileList)) {
            int documentsSize = documents.size();
            logger.debug(fileList.get(index++).getName() + " : " + documentsSize);
            total += documentsSize;
        }
        logger.debug("total : " + total);

        return total;
    }

    /**
     *
     * Json 파일에서 조건에 맞는 데이터의 갯수를 얻는다.
     *
     * @param fileList List<File>
     * @param target String
     * @param value String
     * @return int
     */
    public int countJson(List<File> fileList, String target, String value) {
        int total = 0;
        int index = 0;
        for (JsonArray documents : getDocumentsArrayByFileList(fileList)) {
            int documentsSize = 0;
            for(int i = 0 ; i < documents.size() ; i++) {
                JsonObject document = documents.get(i).getAsJsonObject();
                if(document.get(target).getAsString().equals(value)) {
                    documentsSize++;
                }
            }
            logger.debug(documents.get(index++).getAsJsonObject().get("id") + " : " + documentsSize);
            total += documentsSize;
        }

        logger.debug("total : " + total);

        return total;
    }

    /**
     * 문항 수 카운트 및 문자 평균
     *
     * @param fileList List
     * @param from count from to last index
     */
    public int countJsonIndex(List<File> fileList, int from) {
        int[] indexCount = new int[500];
        float[] charCountAverage = new float[500];

        for(JsonArray documents : getDocumentsArrayByFileList(fileList)) {
            for(int i = 0 ; i < documents.size() ; i++) {
                JsonObject document = documents.get(i).getAsJsonObject();
                JsonArray text = document.get("text").getAsJsonArray();
                int charCount = document.get("char_count").getAsInt();

                int lastIndex = getLastIndex(text);

                charCountAverage[lastIndex] += charCount;
                indexCount[lastIndex]++;
            }
        }

        int indexNumber = 0;
        int totalFromIndex = 0;
        for (int count : indexCount) {
            if(count != 0) {
                logger.debug("Index " + indexNumber + " : " + count);
                logger.debug("average : " + charCountAverage[indexNumber] / count);


                if(indexNumber >= from) {
                    totalFromIndex += count;
                }
            }
            indexNumber++;
        }

        return totalFromIndex;
    }

    /**
     * 파일 경로에 있는 json 분류
     *
     * @param path String
     */
    public void classifyJsonFileByPath(String path) {
        List<File> fileList = FileUtil.getFileList(path, ".json");

        int count = 0;
        String[] sizeArray = {"small", "medium", "large"};
        for (String size : sizeArray) {
            for(File file : fileList) {
                try {
                    classifyJsonFileBySize(file, path, size);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                count++;
                logger.debug("end length: " + count + "/" + fileList.size());
            }
        }
    }

    /**
     * 문서 크기에 따른 파일 분류 메서드
     * @param file File
     * @param outputPath String
     * @param size String
     * @throws Exception If size data empty
     */
    public void classifyJsonFileBySize(File file, String outputPath, String size) throws Exception {


        JsonObject newsJson = getJsonObjectByFile(file);
        JsonObject classifyJson = copyJsonObjectInfo(newsJson);
        JsonArray documents = newsJson.getAsJsonArray("documents");
        JsonArray editDocuments = new JsonArray();

        int documentCount = 0;
        for (int i = 0 ; i < documents.size() ; i++) {
            JsonObject document = documents.get(i).getAsJsonObject();

            if(document.get("size").getAsString().equals(size)) {
                documentCount++;
                JsonObject editDocument = copyDocumentInfo(document);
                editDocument.add("text", document.get("text"));
                editDocuments.add(editDocument);
            }


        }

        if(editDocuments.size() == 0) {
            throw new Exception("No Data");
        }

        classifyJson.add("documents", editDocuments);
        logger.debug("get " + documentCount +" data in " + newsJson.get("name").getAsString());

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        String dirPath = createDir(outputPath, "classify");
        FileUtil.fileOutput(gson.toJson(classifyJson), dirPath + size + "_" + file.getName() ,false);

    }

    /**
     * Index 크기에 따른 파일 분류
     * @param path 현재 파일 경로
     * @param from 인덱스 크기 범위 시작
     * @param to 인덱스 크기 범위 끝
     */
    public void classifyJsonFileByIndex(String path, int from, int to) {
        List<File> fileList = FileUtil.getFileList(path, ".json");
        JsonArray classifyDocuments = new JsonArray();
        int size = 0;

        for(File file : fileList) {
            JsonObject targetJson = getJsonObjectByFile(file);
            JsonArray documents = targetJson.getAsJsonArray("documents");

            for(int i = 0 ; i < documents.size() ; i++) {
                JsonObject document = documents.get(i).getAsJsonObject();
                int lastIndex = getLastIndex(document.get("text").getAsJsonArray());

                if(lastIndex >= from && lastIndex <= to) {
                    classifyDocuments.add(document);
                    size++;
                }
            }

        }

        if (size == 0) { return; }

        String deliveryDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String name = "sentence_";
        if(from == to) {
            name += (from + 1) + "_";
        } else {
            name += (from + 1) + "-" + (to + 1) + "_";
        }
        name += (size + "건_");

        JsonObject classifyJson = new JsonObject();
        classifyJson.addProperty("name", name);
        classifyJson.addProperty("delivery_date", deliveryDate);
        classifyJson.add("documents", classifyDocuments);

        createJsonFile(path + "classify_by_index", name, classifyJson);

    }

    /**
     * Json 파일의 이름을 데이터 정제 이후 변경된 수에 따라서 변경
     * TODO 1. copyFile()?
     * @param fileList List<File>
     * @param outputPath String
     */
    public void changeFileNameByJsonSize(List<File> fileList, String outputPath) {
        for(File file : fileList) {
            JsonObject jsonObject = getJsonObjectByFile(file);
            JsonArray documents = jsonObject.getAsJsonArray("documents");

            String fileName = file.getName();
            fileName = fileName.substring(0,fileName.lastIndexOf('.'));

            String newFileName = getNewNameByJsonSize(documents, fileName);

            logger.debug("new : " + newFileName);

            File newFile = new File(outputPath + "\\new\\" + newFileName + ".json");

            try {
                FileUtils.copyFile(file, newFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private String getNewNameByJsonSize(JsonArray jsonArray, String oldName) {
        logger.debug("original : " + oldName);
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

    private int getLastIndex(JsonArray text) {
        int lastIndex = 0;
        for (int j = 0; j < text.size() ; j++) {
            JsonArray sentences = text.get(j).getAsJsonArray();

            for (int k = 0 ; k < sentences.size() ; k++) {
                JsonObject sentence = sentences.get(k).getAsJsonObject();
                lastIndex = sentence.get("index").getAsInt();

            }
        }
        return lastIndex;
    }


    public static void main(String[] args) {
        JsonFileClassifier jsonFileClassifier = new JsonFileClassifier();

        String path = "D:\\moara\\data\\allData\\기고문\\json\\new\\edit\\new\\";
        jsonFileClassifier.classifyJsonFileByPath(path);


//
//        JsonFileClassifier jsonFileClassifier = new JsonFileClassifier();
//        String path = "D:\\moara\\data\\law\\json5\\edit\\highlight\\";
//
////        for(int i = 0 ; i < 7 ; i++) {
////            jsonFileClassifier.classifyJsonFileByIndex(path, i, i);
////        }
//        jsonFileClassifier.classifyJsonFileByIndex(path, 13, 15);
//        jsonFileClassifier.classifyJsonFileByIndex(path, 16, 19);
//        jsonFileClassifier.classifyJsonFileByIndex(path, 20, 25);
//        jsonFileClassifier.classifyJsonFileByIndex(path, 26, 29);
//        for (int i = 30 ; i < 230 ; i += 10) {
//            jsonFileClassifier.classifyJsonFileByIndex(path, i, i + 9);
//
//        }

    }

}
