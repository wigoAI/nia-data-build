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

import com.google.gson.*;
import org.apache.commons.io.FileUtils;
import org.moara.common.data.file.FileUtil;
import org.moara.nia.data.build.preprocess.DataPreprocessorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JSON 파일 편집기
 * 데이터 정제 결과로 도출된 JSON 파일의 관리를 한다.
 *
 * TODO 1. 유동적이지 않은 구조 개선하기
 *
 * @author 조승현
 */
public class JsonFileEditor {
    private static final Logger logger = LoggerFactory.getLogger(DataPreprocessorImpl.class);
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

    public void editJsonFileByPath(String path) {
        List<File> fileList = FileUtil.getFileList(path, ".json");

        int count = 0;

        for(File file : fileList) {
            editJsonFile(file, path);
            count++;
            logger.debug("end length: " + count + "/" + fileList.size());
        }

    }
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

    public void classifyJsonFileBySize(File file, String outputPath, String size) throws Exception {
        File outputDir = new File(outputPath + "classify\\");

        if(!outputDir.exists()) {
            outputDir.mkdir();
            System.out.println("create dir : " + outputPath + "classify\\");
        }
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
        System.out.println("get " + documentCount +" data in " + newsJson.get("name").getAsString());

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        FileUtil.fileOutput(gson.toJson(classifyJson), outputPath + "classify\\" + size + "_" + file.getName() ,false);

    }

    public void editJsonFile(File file, String outputPath) {
        File outputDir = new File(outputPath + "edit\\");

        if(!outputDir.exists()) {
            outputDir.mkdir();
            System.out.println("create dir : " + outputPath + "edit\\");
        }

        String regxStr = "[a-zA-Z0-9]@";
        Pattern pattern = Pattern.compile(regxStr);

        JsonObject newsJson = getJsonObjectByFile(file);
        JsonObject editJson = copyJsonObjectInfo(newsJson);


        JsonArray documents = newsJson.getAsJsonArray("documents");
        JsonArray editDocuments = new JsonArray();
        int dropCount = 0;
        for (int i = 0 ; i < documents.size() ; i++) {
            JsonObject document = documents.get(i).getAsJsonObject();
            JsonObject editDocument = copyDocumentInfo(document);


            JsonArray text = document.get("text").getAsJsonArray();
            JsonArray editText = new JsonArray();
            int index = 0;
            for(int j = 0 ; j < text.size() ; j++) {
                JsonArray paragraph = text.get(j).getAsJsonArray();
                JsonArray editParagraph = new JsonArray();
                for (int k = 0 ; k < paragraph.size() ; k++) {
                    JsonObject sentence = paragraph.get(k).getAsJsonObject();
                    JsonObject editSentence = new JsonObject();
                    String targetSentence = sentence.get("sentence").getAsString();
                    Matcher matcher = pattern.matcher(targetSentence);
                    if( targetSentence.length() < 40 && matcher.find()) {
                        System.out.println(targetSentence);
                        continue;
                    }


                    editSentence.addProperty("index", index++);
                    editSentence.add("sentence", sentence.get("sentence"));
                    editSentence.add("highlight_indices", sentence.get("highlight_indices"));
                    editParagraph.add(editSentence);
                }

                if(editParagraph.size() != 0) {
                    editText.add(editParagraph);
                }

            }
            if(editText.size() == 0) {
                System.out.println("drop data : " + document.get("id").getAsString() );
                dropCount++;
                continue;
            }

            editDocument.add("text", editText);
            editDocuments.add(editDocument);
        }

        editJson.add("documents", editDocuments);
        System.out.println("drop " + dropCount +" data in " + newsJson.get("name").getAsString());

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        FileUtil.fileOutput(gson.toJson(editJson), outputPath + "edit\\" + file.getName() ,false);

    }

    private JsonObject copyJsonObjectInfo(JsonObject targetJsonObject) {
        JsonObject copiedJsonObject = new JsonObject();
        copiedJsonObject.add("name", targetJsonObject.get("name"));
        copiedJsonObject.add("delivery_date", targetJsonObject.get("delivery_date"));

        return copiedJsonObject;
    }

    private JsonObject copyDocumentInfo(JsonObject targetJsonObject) {
        JsonObject copiedJsonObject = new JsonObject();
        copiedJsonObject.add("id", targetJsonObject.get("id"));
        copiedJsonObject.add("category", targetJsonObject.get("category"));
        copiedJsonObject.add("media_type", targetJsonObject.get("media_type"));
        copiedJsonObject.add("media_sub_type", targetJsonObject.get("media_sub_type"));
        copiedJsonObject.add("media_name", targetJsonObject.get("media_name"));
        copiedJsonObject.add("size", targetJsonObject.get("size"));
        copiedJsonObject.add("char_count", targetJsonObject.get("char_count"));
        copiedJsonObject.add("publish_date", targetJsonObject.get("publish_date"));
        copiedJsonObject.add("title", targetJsonObject.get("title"));

        return copiedJsonObject;
    }

}
