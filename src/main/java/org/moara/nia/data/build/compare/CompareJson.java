package org.moara.nia.data.build.compare;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.moara.common.data.file.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * TODO 1. 두개의 데이터를 document 단위로 비교하기
 *
 */
public class CompareJson implements CompareData {

    @Override
    public void compare(JsonObject beforeJson, JsonObject afterJson) {
        JsonArray beforeDocuments = beforeJson.getAsJsonArray("documents");
        JsonArray afterDocuments = afterJson.getAsJsonArray("documents");

        HashMap<String, JsonArray> beforeTextHash = getHashMapByJsonObject(beforeDocuments);


        for(int i = 0 ; i < afterDocuments.size() ; i++) {
            JsonObject afterDocument = (JsonObject) afterDocuments.get(i);
            System.out.println(afterDocument.get("id"));

            JsonArray beforeText = beforeTextHash.get(afterDocument.get("id").toString());
            JsonArray afterText = afterDocument.getAsJsonArray("text");

            if(beforeText.size() == afterText.size()) {
                for(int j = 0 ; j < beforeText.size() ; j++) {
                    JsonArray beforeParagraph = (JsonArray) beforeText.get(j);
                    JsonArray afterParagraph = (JsonArray) afterText.get(j);

                    for(String str : getSentenceList(beforeParagraph)) {
                        System.out.println("before  : " + str);
                    }
                    for(String str : getSentenceList(afterParagraph)) {
                        System.out.println("after   : " + str);
                    }

                    System.out.println();
                }
            } else {
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

                    System.out.println("before  : " + str);
                    if(editEscapeChar(str).equals(editEscapeChar(afterSentenceList.get(afterSentenceIndex)))) {
                        System.out.println("after   : " + afterSentenceList.get(afterSentenceIndex++));
                    } else if(editEscapeChar(str).contains(editEscapeChar(afterSentenceList.get(afterSentenceIndex)))) {

                        while(editEscapeChar(str).contains(editEscapeChar(afterSentenceList.get(afterSentenceIndex)))) {
                            System.out.print("after : " + afterSentenceList.get(afterSentenceIndex++));
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
                            System.out.println("after : " + afterSentenceList.get(afterSentenceIndex++));

                        }

                        notSplitStack = 0;
                    }

                    if(afterSentenceIndex == afterSentenceList.size())
                        afterSentenceIndex--;

                    System.out.println();
                }



            }

            System.out.println();
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

    public JsonObject getJsonObjectByFile(File file) {

        JsonElement element = null;
        try {
            element = JsonParser.parseReader(new FileReader(file.getPath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return element.getAsJsonObject();
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

    /* 메모리 부족
    private List<JsonObject> getJsonObjectListByFileList(List<File> fileList) {
        List<JsonObject> jsonObjectList = new ArrayList<>();

        for(File file : fileList) {
            System.out.println(file.getPath());
            jsonObjectList.add(getJsonObjectByFile(file));
        }
        return jsonObjectList;
    }
    */





}
