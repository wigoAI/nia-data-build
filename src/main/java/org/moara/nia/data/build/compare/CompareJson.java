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
            if(!beforeTextHash.containsKey(afterDocument.get("id").toString())) {
                throw new NullPointerException();
            }

            JsonArray beforeText = beforeTextHash.get(afterDocument.get("id").toString());
            JsonArray afterText = afterDocument.getAsJsonArray("text");
            List<String> beforeSentenceList = new ArrayList<>();
            List<String> afterSentenceList = new ArrayList<>();
            // same text size

            addSentenceList(beforeText, beforeSentenceList);
            addSentenceList(afterText, afterSentenceList);
            int afterSentenceIndex = 0;

            for(int j = 0 ; j < beforeSentenceList.size() ; j++) {
                String beforeSentence = beforeSentenceList.get(j);
                String afterSentence = "";
                if(afterSentenceIndex < afterSentenceList.size())
                    afterSentence = afterSentenceList.get(afterSentenceIndex);

                if(editEscapeChar(beforeSentence).contains(afterSentence)) {
                    System.out.println("before  : " + beforeSentence);
                    System.out.println("after   : " + afterSentence);

                    afterSentenceIndex++;
                } else {
                    if(editEscapeChar(beforeSentence).length() > afterSentence.length()) {

                    } else {

                    }
                    System.out.println("before  : " + beforeSentence);
                    System.out.println("after   : ");
                }
            }
            System.out.println();
        }

    }

    private void addSentenceList(JsonArray text, List<String> sentenceList) {
        for(int i = 0 ; i < text.size() ; i++) {
            JsonArray beforeSentenceArray = (JsonArray) text.get(i);
            for (int j = 0 ; j < beforeSentenceArray.size() ; j++) {
                JsonObject sentence = (JsonObject) beforeSentenceArray.get(j);
                sentenceList.add(sentence.get("sentence").toString());
            }
        }
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

        value = value.replace("\\r","\n")
                .replace("\\n","\n")
                .replace("\\t","\t")
                .replace("　"," ")
                .replace("`", "'")
                .replace("‘", "'")
                .replace("’", "'")
                .replace("“", "\\\"")
                .replace("”", "\\\"");

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
