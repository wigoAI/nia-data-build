package org.moara.nia.data.build.compare;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlindCompare extends CompareJson {

    @Override
    public void compare(JsonObject beforeJson, JsonObject afterJson) {
        JsonArray beforeDocuments = beforeJson.getAsJsonArray("documents");
        JsonArray afterDocuments = afterJson.getAsJsonArray("documents");
        String blindRegx = "\\*+";
        Pattern blindPattern = Pattern.compile(blindRegx);
        String jsonName = beforeJson.get("name").toString();
        String fileName = jsonName.substring(1, jsonName.length() - 1);
        HashMap<String, JsonArray> beforeTextHash = getHashMapByJsonObject(beforeDocuments);

        String resultPath = "D:\\moara\\data\\law\\compare\\blind\\";
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
                for(String beforeStr : beforeSentenceList) {
                    String afterStr = afterSentenceList.get(afterSentenceIndex++);

                    if(!beforeStr.equals(afterStr)) {
                        StringBuilder blindNames = new StringBuilder();
                        bw.write("before\t: " + beforeStr + "\n");
                        bw.write("after\t: " + afterStr + "\n");

                        Matcher blindMatcher = blindPattern.matcher(afterStr);
                        while(blindMatcher.find()) {
                            blindNames.append(beforeStr, blindMatcher.start(), blindMatcher.end()).append(", ");
                        }
                        bw.write("names\t: " + blindNames + "\n\n");

                    }
                }
                bw.write(" " + "\n\n");
            }

//                System.out.println();
            bw.write(" " + "\n");
        }catch (Exception e) {

        }
    }

}
