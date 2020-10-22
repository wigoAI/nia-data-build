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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.moara.common.data.file.FileUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * CompareData 구현체
 *
 * Json 형태의 데이터를 비교한다.
 *
 * TODO 1. 급하게 만든 compare 메서드 작은 단위로 분해하기
 *      2. editEscapeChar 메서드 개선하기
 *          - preprocessor 에 있는 메서드와 기능이 같다.
 * @author 조승현
 *
 */
public class CompareJson implements CompareData {

    @Override
    public void compare(JsonObject beforeJson, JsonObject afterJson) {
        JsonArray beforeDocuments = beforeJson.getAsJsonArray("documents");
        JsonArray afterDocuments = afterJson.getAsJsonArray("documents");
        String jsonName = beforeJson.get("name").toString();
        String fileName = jsonName.substring(1, jsonName.length() - 1);
        HashMap<String, JsonArray> beforeTextHash = getHashMapByJsonObject(beforeDocuments);

        String resultPath = "D:\\moara\\data\\allData\\change\\edit\\compare\\";
        try ( BufferedWriter bw = new BufferedWriter(new FileWriter(resultPath + fileName + ".txt"))) {


            for(int i = 0 ; i < afterDocuments.size() ; i++) {
                JsonObject afterDocument = (JsonObject) afterDocuments.get(i);
//                System.out.println(afterDocument.get("id"));

                bw.write(afterDocument.get("id").toString() + "\n");
                bw.write(afterDocument.get("title").toString() + "\n");

                JsonArray beforeText = beforeTextHash.get(afterDocument.get("id").toString());
                JsonArray afterText = afterDocument.getAsJsonArray("text");

                    List<String> beforeSentenceList = new ArrayList<>();
                    List<String> afterSentenceList = new ArrayList<>();

                    for(int j = 0 ; j < beforeText.size() ; j++) {
                        JsonArray beforeParagraph = (JsonArray) beforeText.get(j);
                        beforeSentenceList.addAll(getSentenceList(beforeParagraph));
                    }
                    for(int j = 0 ; j < afterText.size() ; j++) {
                        JsonArray afterParagraph = (JsonArray) afterText.get(j);
                        afterSentenceList.addAll(getSentenceList(afterParagraph));
                    }

                    int afterSentenceIndex = 0;
                    int notSplitStack = 0;
                    for(String str : beforeSentenceList) {

//                        System.out.println("before\t: " + str);
                        bw.write("before\t: " + str + "\n");

                        if(editEscapeChar(str).equals(editEscapeChar(afterSentenceList.get(afterSentenceIndex)))) {
//                            System.out.println("after	: " + afterSentenceList.get(afterSentenceIndex++));
                            bw.write("after\t: " + afterSentenceList.get(afterSentenceIndex++) + "\n");
                        } else if(editEscapeChar(str).contains(editEscapeChar(afterSentenceList.get(afterSentenceIndex)))) {

                            while(editEscapeChar(str).contains(editEscapeChar(afterSentenceList.get(afterSentenceIndex)))) {
//                                System.out.print("after\t: " + afterSentenceList.get(afterSentenceIndex++));
                            bw.write("after\t: " + afterSentenceList.get(afterSentenceIndex++) + "\n");
                                if(afterSentenceList.size() == afterSentenceIndex)
                                    break;
                            }
                        } else if(editEscapeChar(afterSentenceList.get(afterSentenceIndex)).contains(editEscapeChar(str))) {
    //                        System.out.println("after don't split");
                            notSplitStack++;
                        } else if(notSplitStack > 0) {

                            for(int k = 0 ; k < notSplitStack ; k++) {
                                if(afterSentenceIndex == afterSentenceList.size())
                                    break;
//                                System.out.println("after\t: " + afterSentenceList.get(afterSentenceIndex++));
                                bw.write("after\t: " + afterSentenceList.get(afterSentenceIndex++) + "\n");
                            }

                            notSplitStack = 0;
                        }

                        if(afterSentenceIndex == afterSentenceList.size())
                            afterSentenceIndex--;

//                        System.out.println();
                        bw.write(" " + "\n");
                    }
                    if(notSplitStack > 0) {
                        for(int k = 0 ; k < notSplitStack ; k++) {
                            if(afterSentenceIndex == afterSentenceList.size())
                                break;
                            bw.write("after\t: " + afterSentenceList.get(afterSentenceIndex++) + "\n");
                        }
                    }



                }

//                System.out.println();
                bw.write(" " + "\n");
            //}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private List<String> getSentenceList(JsonArray jsonArray) {
        List<String> sentenceList = new ArrayList<>();
        for (int i = 0 ; i < jsonArray.size() ; i++) {
            JsonObject sentenceJson = (JsonObject) jsonArray.get(i);
            String sentence = sentenceJson.get("sentence").toString();
            sentence = sentence.substring(1,sentence.length() - 1);
            sentenceList.add(sentence);
        }

        return sentenceList;
    }

    private HashMap<String, JsonArray> getHashMapByJsonObject(JsonArray beforeDocuments) {
        HashMap<String, JsonArray> beforeTextHash = new HashMap<>();
        for(int i = 0 ; i < beforeDocuments.size() ; i++) {
            JsonObject document = (JsonObject) beforeDocuments.get(i);
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
//        System.out.println(value);
        return value;
    }





}
