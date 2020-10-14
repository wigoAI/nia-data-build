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


package org.moara.nia.data.build.personalData;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 개인 정보 탐색기 구현체
 * 실제로 개인 정보 탐색을 수행하는 구현체이다.
 *
 * @author 조승현
 */
public class PersonalDataFinderImpl implements PersonalDataFinder {

    private final Pattern pattern;
    private final String type;

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
