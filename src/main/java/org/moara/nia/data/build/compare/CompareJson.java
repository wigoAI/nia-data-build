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

package org.moara.nia.data.build.compare;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * CompareData 구현체
 * Json 형태의 데이터를 비교한다.
 *
 * TODO 1. editEscapeChar 메서드 개선하기
 *          - preprocessor 에 있는 메서드와 기능이 같다.
 *
 * @author 조승현
 */
public class CompareJson implements CompareData {
    protected JsonObject afterJson;
    protected JsonObject beforeJson;
    protected JsonArray beforeDocuments;
    protected JsonArray afterDocuments;
    protected HashMap<String, JsonArray> beforeTextHash;

    protected List<String> beforeSentenceList = new ArrayList<>();
    protected List<String> afterSentenceList = new ArrayList<>();

    protected String resultPath;
    protected String fileName;

    public CompareJson(JsonObject beforeJson, JsonObject afterJson, String resultPath) {
        this.afterJson = afterJson;
        this.beforeJson = beforeJson;
        this.beforeDocuments = beforeJson.getAsJsonArray("documents");
        this.afterDocuments = afterJson.getAsJsonArray("documents");
        this.resultPath = resultPath;
        this.fileName = beforeJson.get("name").getAsString();

        beforeTextHash = getHashMapByJsonObject(beforeDocuments);
    }


    @Override
    public void compare() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(resultPath + fileName + ".txt"));

        for(int i = 0 ; i < afterDocuments.size() ; i++) {
            initCompare(bw, i);

            int afterSentenceIndex = 0;
            int notSplitStack = 0;
            for(String beforeSentence : beforeSentenceList) {
                String editBeforeSentence = editEscapeChar(beforeSentence);
                bw.write("before\t: " + beforeSentence + "\n");

                if(editBeforeSentence.equals(editEscapeChar(afterSentenceList.get(afterSentenceIndex)))) {
                    bw.write("after\t: " + afterSentenceList.get(afterSentenceIndex++) + "\n");
                } else if(editEscapeChar(beforeSentence).contains(editEscapeChar(afterSentenceList.get(afterSentenceIndex)))) {

                    while(editBeforeSentence.contains(editEscapeChar(afterSentenceList.get(afterSentenceIndex)))) {
                        bw.write("after\t: " + afterSentenceList.get(afterSentenceIndex++) + "\n");
                        if(afterSentenceList.size() == afterSentenceIndex) { break; }
                    }
                } else if(editEscapeChar(afterSentenceList.get(afterSentenceIndex)).contains(editBeforeSentence)) {
                    notSplitStack++;
                } else if(notSplitStack > 0) {

                    for(int k = 0 ; k < notSplitStack ; k++) {
                        if(afterSentenceIndex == afterSentenceList.size())
                            break;
                        bw.write("after\t: " + afterSentenceList.get(afterSentenceIndex++) + "\n");
                    }

                    notSplitStack = 0;
                }

                if(afterSentenceIndex == afterSentenceList.size()) { afterSentenceIndex--; }

                bw.write(" \n");
            }

            if(notSplitStack > 0) {
                for(int k = 0 ; k < notSplitStack ; k++) {
                    if(afterSentenceIndex == afterSentenceList.size()) { break; }
                    bw.write("after\t: " + afterSentenceList.get(afterSentenceIndex++) + "\n");
                }
            }
        }

        bw.write(" \n");

    }

    protected void initCompare(BufferedWriter bw, int index) {
        JsonObject afterDocument = afterDocuments.get(index).getAsJsonObject();

        try {
            bw.write("id : " + afterDocument.get("id").getAsString() + "\n");
            bw.write("title  : " + afterDocument.get("title").getAsString() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("id : " + afterDocument.get("id").getAsString() + "\n");
        System.out.println("title  : " + afterDocument.get("title").getAsString() + "\n");
        JsonArray beforeText = beforeTextHash.get(afterDocument.get("id").toString());
        JsonArray afterText = afterDocument.getAsJsonArray("text");

        beforeSentenceList.clear();
        for(int j = 0 ; j < beforeText.size() ; j++) {
            JsonArray beforeParagraph = (JsonArray) beforeText.get(j);
            beforeSentenceList.addAll(getSentenceList(beforeParagraph));
        }

        afterSentenceList.clear();
        for(int j = 0 ; j < afterText.size() ; j++) {
            JsonArray afterParagraph = (JsonArray) afterText.get(j);
            afterSentenceList.addAll(getSentenceList(afterParagraph));
        }
    }


    protected List<String> getSentenceList(JsonArray jsonArray) {
        List<String> sentenceList = new ArrayList<>();
        for (int i = 0 ; i < jsonArray.size() ; i++) {
            JsonObject sentenceJson = (JsonObject) jsonArray.get(i);
            String sentence = sentenceJson.get("sentence").toString();
            sentence = sentence.substring(1,sentence.length() - 1);
            sentenceList.add(sentence);
        }

        return sentenceList;
    }

    protected HashMap<String, JsonArray> getHashMapByJsonObject(JsonArray beforeDocuments) {
        HashMap<String, JsonArray> beforeTextHash = new HashMap<>();
        for(int i = 0 ; i < beforeDocuments.size() ; i++) {
            JsonObject document = beforeDocuments.get(i).getAsJsonObject();
            beforeTextHash.put(document.get("id").toString(), document.getAsJsonArray("text"));
        }

        return beforeTextHash;
    }



    private String editEscapeChar(String value) {

        value = value.replace("`", "")
                .replace(" ", "")
                .replace(" ", "")
                .replace("‘", "")
                .replace("’", "")
                .replace("`", "")
                .replace("“", "")
                .replace("'", "")
                .replace("”", "")
                .replace("\\\"", "")
                .replace("\"", "").trim();
//        bw.write(value);
        return value;
    }





}
