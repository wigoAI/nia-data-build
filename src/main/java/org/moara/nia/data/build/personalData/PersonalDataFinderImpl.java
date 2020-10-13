package org.moara.nia.data.build.personalData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PersonalDataFinderImpl implements PersonalDataFinder {

    private final Pattern pattern;
    private String type;
    public PersonalDataFinderImpl(String type) {
        this.type = type;
        String regx = fileReader.apply(type);
        pattern = Pattern.compile(regx);
    }
    @Override
    public List<PersonalData> find(String text) {
        Matcher urlMatcher = pattern.matcher(text);
        List<PersonalData> personalData = new ArrayList<>();

        while(urlMatcher.find()) {
            personalData.add(new PersonalData(urlMatcher.start(), urlMatcher.end(), urlMatcher.group(), type));
        }


        return personalData;
    }


}
