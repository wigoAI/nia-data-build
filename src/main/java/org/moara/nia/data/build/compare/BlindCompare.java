package org.moara.nia.data.build.compare;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlindCompare extends CompareJson {

    private final Pattern blindPattern = Pattern.compile("\\*+");

    public BlindCompare(JsonObject beforeJson, JsonObject afterJson) {
        super(beforeJson, afterJson);
    }

    @Override
    public String compare() {


        StringBuilder result = new StringBuilder();

        for(int i = 0 ; i < afterDocuments.size() ; i++) {
            initCompare(result, i);

            int afterSentenceIndex = 0;
            for(String beforeStr : beforeSentenceList) {
                String afterStr = afterSentenceList.get(afterSentenceIndex++);

                if(!beforeStr.equals(afterStr)) {
                    StringBuilder blindNames = new StringBuilder();
                    result.append("before\t: ").append(beforeStr).append("\n");
                    result.append("after\t: ").append(afterStr).append("\n");

                    Matcher blindMatcher = blindPattern.matcher(afterStr);
                    while(blindMatcher.find()) {
                        blindNames.append(beforeStr, blindMatcher.start(), blindMatcher.end()).append(", ");
                    }
                    result.append("names\t: ").append(blindNames).append("\n\n");

                }
            }
            result.append(" \n\n");
        }

        result.append(" \n");

        return result.toString();
    }

}
