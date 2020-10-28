package org.moara.nia.data.build.compare;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlindCompare extends CompareJson {

    private final Pattern blindPattern = Pattern.compile("\\*+");

    public BlindCompare(JsonObject beforeJson, JsonObject afterJson, String resultPath) {
        super(beforeJson, afterJson, resultPath);
    }

    @Override
    public void compare() throws IOException {

        BufferedWriter bw = new BufferedWriter(new FileWriter(resultPath + fileName + ".txt"));
        for(int i = 0 ; i < afterDocuments.size() ; i++) {
            initCompare(bw, i);

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
            bw.write("\n\n");
        }

        bw.write("\n");

    }

}
