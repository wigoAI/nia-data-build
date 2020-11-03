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

import com.google.gson.*;
import org.moara.common.data.file.FileUtil;
import org.moara.nia.data.build.Area;
import org.moara.nia.data.build.mecab.MecabWordClassHighlight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JSON 파일 편집기
 * 데이터 정제 결과로 도출된 JSON 파일의 관리를 한다.
 *
 * @author 조승현
 */
public class JsonFileEditor extends JsonFileUtil{
    private static final Logger logger = LoggerFactory.getLogger(JsonFileEditor.class);
    protected final HashSet<String> dropData = new HashSet<>();

    /**
     * Constructor without dropData
     */
    public JsonFileEditor() { }

    /**
     * Constructor with dropData
     * @param dropData HashSet about want to drop
     */
    public JsonFileEditor(HashSet<String> dropData) {
        this.dropData.addAll(dropData);
    }

    /**
     *
     * 파일 경로에 있는 json 수정
     *
     * @param path String
     */
    public void editJsonFileByPath(String path) {
        List<File> fileList = FileUtil.getFileList(path, ".json");

        int count = 0;

        for(File file : fileList) {
            editJsonFile(file, path);
            count++;
            logger.debug("end length: " + count + "/" + fileList.size());
        }

    }

    /**
     * Json 파일 수정 메서드
     *
     * @param file File
     * @param outputPath String
     */
    public void editJsonFile(File file, String outputPath) {

        JsonObject newsJson = getJsonObjectByFile(file);
        JsonObject editJson = copyJsonObjectInfo(newsJson);
        JsonArray documents = newsJson.getAsJsonArray("documents");
        JsonArray editDocuments = getEditDocuments(documents);

        System.out.println(newsJson.get("name").getAsString());
        editJson.add("documents", editDocuments);

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        String dirPath = createDir(outputPath, "edit");
        FileUtil.fileOutput(gson.toJson(editJson), dirPath + file.getName() ,false);

    }


    /**
     * TODO 1. Need to refactoring with LawJsonFileEditor getEditDocuments
     *
     */
    protected JsonArray getEditDocuments(JsonArray documents) {
        JsonArray editDocuments = new JsonArray();



        int dropCount = 0;
        // Document 복사 & text 접근
        for (int i = 0; i < documents.size() ; i++) {
            JsonObject document = documents.get(i).getAsJsonObject();
            JsonObject editDocument = copyDocumentInfo(document);
            JsonArray text = document.get("text").getAsJsonArray();

            String documentId = document.get("id").getAsString();
            System.out.println("Edit " + documentId);
            if (dropData.contains(documentId)) {
                System.out.println("Drop data by dropList : " + documentId);
                dropCount++;
                continue;
            }

            JsonArray editText = getEditText(text);
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

    protected JsonArray getEditText(JsonArray text) {
        ///// getEditText 에 삽입할 것
        Pattern emailPattern = getPattern("[a-zA-Z0-9]@");
        String[] editPatterns = {"\\[사진.*\\]",
                "/[^a-zA-Z0-9]*기자.*",
                "<[^>]*[>.]",
                "^/[^a-zA-Z0-9]+/$",
                "\\([^\\(\\)]*[^포자전]기자[^차\\(\\)]*\\)"};

        List<Predicate<String>> dropCondition = new ArrayList<>();
        dropCondition.add(t -> t.length() < 6);
        dropCondition.add(t -> t.length() < 40 && emailPattern.matcher(t).find());

        List<Predicate<String>> frontDropCondition = new ArrayList<>();
        frontDropCondition.add(t -> !endsWithDa(t));
        //////
        JsonArray editText = new JsonArray();

        // text 접근
        int index = 0;
        for(int j = 0; j < text.size() ; j++) {
            JsonArray paragraph = text.get(j).getAsJsonArray();
            JsonArray editParagraph = new JsonArray();

            // 문단 수정
            for (int k = 0 ; k < paragraph.size() ; k++) {
                JsonObject sentence = paragraph.get(k).getAsJsonObject();
                String targetSentence = sentence.get("sentence").getAsString();
                boolean dropFlag = false;

                // 특정 단어 제거
                for (String pattern : editPatterns) {
                    targetSentence = editSentenceByPattern(sentence, targetSentence, pattern).trim();
                }

                // 전체 범위 dropCondition check
                for (Predicate<String> condition : dropCondition) {
                    if (condition.test(targetSentence)){ dropFlag = true; }
                }

                // 처음과 끝 문단 dropCondition check
                if ((j < 2 || j == text.size() - 1)) {
                    for (Predicate<String> condition : frontDropCondition) {
                        if (condition.test(targetSentence)) { dropFlag = true; }
                    }
                }

                if (dropFlag) {
                    System.out.println("Drop data : " + targetSentence);
                    continue;
                }

                sentence.addProperty("sentence", targetSentence);
                editParagraph.add(copySentence(index++, sentence));
            }

            if(editParagraph.size() != 0) {
                editText.add(editParagraph);
            }
        }

        return editText;
    }

    /**
     * Json mecab highlighting
     *
     * @param file JSON File
     * @param outputPath result file path
     */
    public void highlightJsonFile(File file, String outputPath) {
        JsonObject newsJson = getJsonObjectByFile(file);
        JsonObject highlightJson = copyJsonObjectInfo(newsJson);
        JsonArray documents = newsJson.getAsJsonArray("documents");
        JsonArray highlightDocuments = getHighlightDocuments(documents);

        System.out.println(newsJson.get("name").getAsString());
        highlightJson.add("documents", highlightDocuments);

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        String dirPath = createDir(outputPath, "highlight");
        FileUtil.fileOutput(gson.toJson(highlightJson), dirPath + file.getName() ,false);

    }

    private JsonArray getHighlightDocuments(JsonArray documents) {
        JsonArray highlightDocuments = new JsonArray();

        for (int i = 0; i < documents.size() ; i++) {
            JsonObject document = documents.get(i).getAsJsonObject();
            JsonObject highlightDocument = copyDocumentInfo(document);
            JsonArray text = document.get("text").getAsJsonArray();

            String[] outArray = {"M"};
            JsonArray highlightText = getHighlightText(text, outArray);


            highlightDocument.add("text", highlightText);
            highlightDocuments.add(highlightDocument);
        }

        return highlightDocuments;
    }

    private JsonArray getHighlightText(JsonArray text, String[] outArray) {
        JsonArray editText = new JsonArray();

        // text 접근
        int index = 0;
        for(int j = 0; j < text.size() ; j++) {
            JsonArray paragraph = text.get(j).getAsJsonArray();
            JsonArray editParagraph = new JsonArray();

            // 문단 수정
            for (int k = 0 ; k < paragraph.size() ; k++) {
                JsonObject sentence = paragraph.get(k).getAsJsonObject();
                String targetSentence = sentence.get("sentence").getAsString();
                String mecabResult = MecabWordClassHighlight.indexValue(targetSentence, outArray);
                sentence.addProperty("highlight_indices", mecabResult);

                editParagraph.add(copySentence(index++, sentence));
            }

            if(editParagraph.size() != 0) {
                editText.add(editParagraph);
            }
        }

        return editText;
    }

    private String editSentenceByPattern(JsonObject sentence, String targetSentence, String pattern) {
        Matcher matcher = Pattern.compile(pattern).matcher(targetSentence);

        if(matcher.find()) {
            String jsonValue = sentence.get("highlight_indices").getAsString();
            String[] highlightIndices =  jsonValue.split(";");

            if(jsonValue.length() != 0) {
                moveHighlight(sentence, matcher, highlightIndices);
            }

            targetSentence = targetSentence.replaceAll(pattern, "");
        }
        return targetSentence;
    }

    private void moveHighlight(JsonObject sentence, Matcher matcher, String[] highlightIndices) {
        int areaIndex = 0;
        boolean isIndexMoved = false;
        Area[] highlightAreas = new Area[highlightIndices.length];

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

    private boolean endsWithDa(String sentence) {
        if(sentence.charAt(sentence.length() - 1) == '.' && sentence.charAt(sentence.length() - 2) == '다') {
            return true;
        } else if(sentence.charAt(sentence.length() - 1) == '다') {
            return true;
        }

        return false;
    }



}
