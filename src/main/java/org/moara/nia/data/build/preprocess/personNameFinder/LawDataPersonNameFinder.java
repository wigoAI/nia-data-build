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

package org.moara.nia.data.build.preprocess.personNameFinder;

import org.moara.common.string.Check;
import org.moara.nia.data.build.Area;
import org.moara.nia.data.build.mecab.MecabWordClassHighlight;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * 법률 데이터 인명 탐색기
 * TODO 1. 사용하지 않을 가능성이 있지만 계속 구현할 것
 * @author 조승현
 */
public class LawDataPersonNameFinder implements PersonNameFinder{
    private final String[] frontConditionRegxArray = {"변호사\\s"};
    private final String[] backConditionRegxArray = {"\\s외\\s\\d\\s인"};
    private final Pattern[] frontConditionPatterns;
    private final Pattern[] backConditionPatterns;

    public LawDataPersonNameFinder() {
        backConditionPatterns = Arrays.stream(backConditionRegxArray)
                .map(Pattern::compile)
                .toArray(Pattern[]::new);
        frontConditionPatterns = Arrays.stream(frontConditionRegxArray)
                .map(Pattern::compile)
                .toArray(Pattern[]::new);
    }

    @Override
    public List<Area> findPersonNAme(String text) {
        List<Area> personNameAreas = new ArrayList<>();
        for (Pattern pattern : backConditionPatterns) {
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {

            }
        }
        return personNameAreas;
    }
}
