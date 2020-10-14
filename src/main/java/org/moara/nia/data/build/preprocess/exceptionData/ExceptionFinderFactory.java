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


package org.moara.nia.data.build.preprocess.exceptionData;

import org.moara.nia.data.build.Area;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 예외 데이터 탐색기 팩토리
 * 각 문서마다 적용 될 예외 데이터 탐색기를 제공
 *
 * TODO 1. [기자] 단어가 포함된 문장 제거하기
 *      2. 또다른 반복 데이터 제거하기
 * @author 조승현
 */
public class ExceptionFinderFactory {
    private static final String reporterFindRegx = "(기자[^가-힣a-zA-Z0-9]{1,10})([0-9a-zA-Z][0-9a-zA-Z\\_\\-\\.]+[0-9a-zA-Z]@[0-9a-zA-Z][0-9a-zA-Z\\_\\-]*[0-9a-zA-Z](\\.[a-zA-Z]{2,6}){1,2}([^가-힣a-zA-Z0-9]{0,10}))?";
    private static final Pattern reporterFindPattern = Pattern.compile(reporterFindRegx);

    public static ExceptionDataFinder getExceptionFinder(String dataType) {

        if(dataType.equals("reporter")) {
            return text -> {
                Matcher matcher = reporterFindPattern.matcher(text);
                int start = 0;
                int end = 0;
                while(matcher.find()) {
                    start = matcher.start();
                    end = matcher.end();
                }
                return new Area(start, end);

            };
        } else {
            return text -> new Area(0, 0);
        }



    }
}
