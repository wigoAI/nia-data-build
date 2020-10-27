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
import org.moara.nia.data.build.Area;
import org.moara.nia.data.build.preprocess.DataPreprocessorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JSON 파일 편집기
 * 데이터 정제 결과로 도출된 JSON 파일의 관리를 한다.
 *
 * TODO 1. 유동적이지 않은 구조 개선하기
 *      2. JAVADOC 추가하기
 *      3. 문장 길이에 변화가 생길 때 mecab 인덱스 변화줄 것
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
     *
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

    /**
     *
     * Json 파일에서 조건에 맞는 데이터의 갯수를 얻는다.
     *
     * @param fileList List<File>
     * @return int
     */
    public int jsonCounter(List<File> fileList, String target, String value) {
        int total = 0;
        for(File file : fileList) {
            JsonObject jsonObject = getJsonObjectByFile(file);
            JsonArray documents = jsonObject.getAsJsonArray("documents");
            int documentsSize = 0;
            for(int i = 0 ; i < documents.size() ; i++) {
                JsonObject document = documents.get(i).getAsJsonObject();
                if(document.get(target).getAsString().equals(value)) {
                    documentsSize++;
                }
            }
            System.out.println(file.getName() + " : " + documentsSize);
            total += documentsSize;
        }
        System.out.println("total : " + total);

        return total;
    }

    /**
     *
     * 파일 경로에 있는 json 수정
     *
     * @param path String
     */
    public void editJsonFileByPath(String path, HashSet<String> dropData) {
        List<File> fileList = FileUtil.getFileList(path, ".json");

        int count = 0;

        for(File file : fileList) {
            editJsonFile(file, path, dropData);
            count++;
            logger.debug("end length: " + count + "/" + fileList.size());
        }

    }

    /**
     *
     * 파일 경로에 있는 json 수정
     *
     * @param path String
     */
    public void editLawJsonFileByPath(String path) {
        List<File> fileList = FileUtil.getFileList(path, ".json");

        int count = 0;

        for(File file : fileList) {
            editLawJsonFile(file, path);
            count++;
            logger.debug("end length: " + count + "/" + fileList.size());
        }

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
        createDir(outputPath, "classify");
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

    /**
     * Edit magazine json file method (Delete)
     * @param file File
     * @param outputPath String
     */


    /**
     * Json 파일 수정 메서드
     * TODO 1. 완벽하게 모듈화 할 것
     *      2. 생성된 json 을 수정하는 기능은 이후에 많이 쓰일것으로 예상됨
     *
     * @param file File
     * @param outputPath String
     */
    public void editJsonFile(File file, String outputPath, HashSet<String> dropData) {
        createDir(outputPath, "edit");

        JsonObject newsJson = getJsonObjectByFile(file);
        JsonObject editJson = copyJsonObjectInfo(newsJson);
        JsonArray documents = newsJson.getAsJsonArray("documents");
        JsonArray editDocuments = getEditDocuments(documents, dropData);

        System.out.println(newsJson.get("name").getAsString());
        editJson.add("documents", editDocuments);

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        FileUtil.fileOutput(gson.toJson(editJson), outputPath + "edit\\" + file.getName() ,false);

    }

    /**
     * 판례 데이터 Json 파일 수정 메서드
     *
     * @param file File
     * @param outputPath String
     */
    public void editLawJsonFile(File file, String outputPath) {
        createDir(outputPath, "edit");

        JsonObject newsJson = getJsonObjectByFile(file);
        JsonObject editJson = copyJsonObjectInfo(newsJson);
        JsonArray documents = newsJson.getAsJsonArray("documents");
        JsonArray editDocuments = getEditLawDocuments(documents);

        System.out.println(newsJson.get("name").getAsString());
        editJson.add("documents", editDocuments);

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        FileUtil.fileOutput(gson.toJson(editJson), outputPath + "edit\\" + file.getName() ,false);

    }

    private JsonArray getEditLawDocuments(JsonArray documents) {
        Pattern dropDataPattern = Pattern.compile(".*[^\\\\.\\]다\\)례예]$");
        JsonArray editDocuments = new JsonArray();

        int dropCount = 0;
        // Document 복사 & text 접근
        for (int i = 0; i < documents.size() ; i++) {
            JsonObject document = documents.get(i).getAsJsonObject();
            JsonObject editDocument = copyDocumentInfo(document);
            boolean dropDocumentFlag = false;
            JsonArray text = document.get("text").getAsJsonArray();
            JsonArray editText = new JsonArray();


            // text 접근
            int index = 0;
            for(int j = 0 ; j < text.size() ; j++) {

                JsonArray paragraph = text.get(j).getAsJsonArray();
                JsonArray editParagraph = new JsonArray();

                // 문단 수정
                for (int k = 0 ; k < paragraph.size() ; k++) {
                    JsonObject sentence = paragraph.get(k).getAsJsonObject();
                    JsonObject editSentence;
                    String targetSentence = sentence.get("sentence").getAsString();
                    Matcher dropDataMatcher = dropDataPattern.matcher(targetSentence);

                    if(j != text.size() - 1 && k != paragraph.size() && dropDataMatcher.find()) {
                        System.out.println(document.get("id").getAsString() + " Drop data : " + targetSentence);
                        dropDocumentFlag = true;
                        break;
                    }

                    sentence.addProperty("sentence", targetSentence);
                    editSentence = copySentence(index++, sentence);
                    editParagraph.add(editSentence);
                }
                if(dropDocumentFlag) {
                    break;
                }
                if(editParagraph.size() != 0) {
                    editText.add(editParagraph);
                }

            }

            if(editText.size() == 0 || dropDocumentFlag) {
                System.out.println("drop data : " + document.get("id").getAsString() );
                dropCount++;
                continue;
            }

            editDocument.add("text", editText);
            editDocuments.add(editDocument);

        }
        System.out.print("drop " + dropCount +" data in ");

        return editDocuments;
    }


    private JsonArray getEditDocuments(JsonArray documents, HashSet<String> dropData) {
        JsonArray editDocuments = new JsonArray();
        Pattern emailPattern = getPattern("[a-zA-Z0-9]@");
        String[] editPatterns = {"\\[사진.*\\]",
                "/[^a-zA-Z0-9]*기자.*",
                "<[^>]*[>.]",
                "^/[^a-zA-Z0-9]+/$",
                "\\([^\\(\\)]*[^포자전]기자[^차\\(\\)]*\\)"};

        int dropCount = 0;
        // Document 복사 & text 접근
        for (int i = 0; i < documents.size() ; i++) {
            JsonObject document = documents.get(i).getAsJsonObject();
            JsonObject editDocument = copyDocumentInfo(document);
            JsonArray text = document.get("text").getAsJsonArray();
            JsonArray editText = new JsonArray();
            String documentId = document.get("id").getAsString();

            System.out.println("Edit " + documentId);
            if (dropData.contains(documentId)) {
                System.out.println("Drop data by dropList : " + documentId);
                dropCount++;
                continue;
            }
            // text 접근
            int index = 0;
            for(int j = 0 ; j < text.size() ; j++) {

                JsonArray paragraph = text.get(j).getAsJsonArray();
                JsonArray editParagraph = new JsonArray();

                // 문단 수정
                for (int k = 0 ; k < paragraph.size() ; k++) {
                    JsonObject sentence = paragraph.get(k).getAsJsonObject();
                    JsonObject editSentence;
                    String targetSentence = sentence.get("sentence").getAsString();
                    Matcher emailMatcher = emailPattern.matcher(targetSentence);

                    // 제거 대상
                    for (String pattern : editPatterns) {
//                        System.out.println("targetString : " + targetSentence);
                        targetSentence = editSentence(sentence, targetSentence, pattern).trim();
                    }

                    if(targetSentence.length() < 6) { // 의미 없는 짧은 데이터
                        System.out.println("Drop short text : " + targetSentence);
                        continue;
                    } else if( targetSentence.length() < 40 && emailMatcher.find()) { // email 포함된 단순 기자 정보 데이터
                        System.out.println("Drop email : " + targetSentence);
                        continue;

                    } else if(!endsWithDa(targetSentence) && (j < 2 || j == text.size() - 1)) { // '다'로 끝나는 문장

                        System.out.println("Drop headline or explain text : " + targetSentence);
                        continue;
                    }

                    sentence.addProperty("sentence", targetSentence);
                    editSentence = copySentence(index++, sentence);
                    editParagraph.add(editSentence);
                }

                if(editParagraph.size() != 0) {
                    editText.add(editParagraph);
                }
            }

            if(editText.size() == 0) {
                System.out.println("Drop data : " + documentId );
                dropCount++;
                continue;
            }

            editDocument.add("text", editText);
            editDocuments.add(editDocument);
        }
        System.out.print("drop " + dropCount +" data in ");

        return editDocuments;
    }

    private String editSentence(JsonObject sentence, String targetSentence, String pattern) {

        Matcher matcher = Pattern.compile(pattern).matcher(targetSentence);

        if(matcher.find()) {
            String jsonValue = sentence.get("highlight_indices").getAsString();
            String[] highlightIndices =  jsonValue.split(";");
            Area[] highlightAreas = new Area[highlightIndices.length];
            boolean isIndexMoved = false;

            if(jsonValue.length() != 0) {
                int areaIndex = 0;

                for (String highlight : highlightIndices) {
                    int highlightStartIndex = Integer.parseInt(highlight.split(",")[0]);
                    int highlightEndIndex = Integer.parseInt(highlight.split(",")[1]);

                    if(matcher.end() <= highlightStartIndex) { // 제거대상 뒤에 하이라이트
                        highlightStartIndex -= matcher.group().length();
                        highlightEndIndex -= matcher.group().length();
                        isIndexMoved = true;
                    }

                    highlightAreas[areaIndex++] = new Area(highlightStartIndex, highlightEndIndex);
                }

            }

            targetSentence = targetSentence.replaceAll(pattern, "");
            if(isIndexMoved) {
                StringBuilder highlightIndicesStr = new StringBuilder();
                int areaCount = 0;
                for (Area area : highlightAreas) {
                    highlightIndicesStr.append(area.getStart()).append(",").append(area.getEnd());
                    if (++areaCount < highlightAreas.length) {
                        highlightIndicesStr.append(";");
                    }

                }
                sentence.addProperty("highlight_indices", highlightIndicesStr.toString());
            }
        }
        return targetSentence;
    }

    private boolean endsWithDa(String sentence) {
        if(sentence.charAt(sentence.length() - 1) == '.' && sentence.charAt(sentence.length() - 2) == '다') {
            return true;
        } else if(sentence.charAt(sentence.length() - 1) == '다') {
            return true;
        }


        return false;
    }

    private void createDir(String outputPath, String dirName) {
        dirName += "\\";
        File outputDir = new File(outputPath + dirName);

        if (!outputDir.exists()) {
            outputDir.mkdir();
            System.out.println("create dir : " + outputPath + dirName);
        }
    }

    private JsonObject copySentence(int index, JsonObject targetSentence) {
        JsonObject editSentence = new JsonObject();
        editSentence.addProperty("index", index);
        editSentence.add("sentence", targetSentence.get("sentence"));
        editSentence.add("highlight_indices", targetSentence.get("highlight_indices"));
        return editSentence;
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

    private Pattern getPattern(String regxStr) {
        return Pattern.compile(regxStr);
    }

}
