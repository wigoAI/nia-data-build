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

public class CompareJson implements CompareData {

    @Override
    public void compare(JsonObject beforeJson, JsonObject afterJson) {
        JsonArray beforeDocuments = beforeJson.getAsJsonArray("documents");
        JsonArray afterDocuments = afterJson.getAsJsonArray("documents");
        String jsonName = beforeJson.get("name").toString();
        String fileName = jsonName.substring(1, jsonName.length() - 1);
        HashMap<String, JsonArray> beforeTextHash = getHashMapByJsonObject(beforeDocuments);

        String resultPath = "D:\\moara\\data\\allData\\change\\";
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

//                        System.out.println("before  : " + str);
                        bw.write("before  : " + str + "\n");

                        if(editEscapeChar(str).equals(editEscapeChar(afterSentenceList.get(afterSentenceIndex)))) {
//                            System.out.println("after   : " + afterSentenceList.get(afterSentenceIndex++));
                            bw.write("after   : " + afterSentenceList.get(afterSentenceIndex++) + "\n");
                        } else if(editEscapeChar(str).contains(editEscapeChar(afterSentenceList.get(afterSentenceIndex)))) {

                            while(editEscapeChar(str).contains(editEscapeChar(afterSentenceList.get(afterSentenceIndex)))) {
//                                System.out.print("after : " + afterSentenceList.get(afterSentenceIndex++));
                            bw.write("after   : " + afterSentenceList.get(afterSentenceIndex++) + "\n");
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
//                                System.out.println("after : " + afterSentenceList.get(afterSentenceIndex++));
                                bw.write("after   : " + afterSentenceList.get(afterSentenceIndex++) + "\n");
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
                            bw.write("after   : " + afterSentenceList.get(afterSentenceIndex++) + "\n");
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
