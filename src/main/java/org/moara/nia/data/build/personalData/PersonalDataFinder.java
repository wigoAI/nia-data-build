package org.moara.nia.data.build.personalData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

public interface PersonalDataFinder {

    List<PersonalData> find(String sentence);
    String bastPath = "/regx/";
    Function<String, String> fileReader = (t) -> {
        BufferedReader br = null;
        br = new BufferedReader(
                new InputStreamReader(PersonalDataFinder.class.getResourceAsStream( bastPath + t + ".regx"), StandardCharsets.UTF_8));
        try {
            return br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    };

}
