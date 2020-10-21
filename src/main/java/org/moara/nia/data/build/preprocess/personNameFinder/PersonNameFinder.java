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
import org.moara.nia.data.build.mecab.MecabWordClassHighlight;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * 인명 탐색기
 * @author 조승현
 */
public class PersonNameFinder {

    private static final HashSet<String> KOREAN_LAST_NAME_HASH = new HashSet<>();
    private static final String lawyerRegx = "변호사\\s[가-힣]{2,3}";
    private static final Pattern lawyerPattern = Pattern.compile(lawyerRegx);

    public PersonNameFinder() {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(MecabWordClassHighlight.class.getResourceAsStream("/dic/korean_first_name.dic"), StandardCharsets.UTF_8));
            while(true) {
                String line = br.readLine();
                KOREAN_LAST_NAME_HASH.add(line);
                if(line == null)
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String blindPersonName(String str) {
        if(str.contains("【") || str.contains("】") || str.startsWith("(")) {
            Matcher matcher = lawyerPattern.matcher(str);
            while (matcher.find()) {
                if(matcher.group().length() == 7) {
                    str = str.replaceAll(lawyerRegx, "변호사 ***");
                } else {
                    str = str.replaceAll(lawyerRegx, "변호사 **");
                }
            }
        }

        return str;
    }


    /**
     *
     * @param text String
     * @param start int
     * @param end int
     * @return boolean
     */
    public static boolean isPersonName(String text, int start, int end) {
        String name = text.substring(start, end);

        if(name.length() < 2 || name.length() > 3)
            return false;

        if(start > 0) {
            if(Check.isHangul(text.charAt(start - 1)))
                return false;
        }
        if(start > 4) {
            if(text.startsWith("법무법인 ", start - 5))
                return false;
            if(text.startsWith("주식회사 ", start - 5))
                return false;
        }
        if(end < text.length()) {
            if(Check.isHangul(text.charAt(end)))
                return false;
        }
        if (!KOREAN_LAST_NAME_HASH.contains(name.substring(0, 1)))
            return false;


        return true;


    }

}
