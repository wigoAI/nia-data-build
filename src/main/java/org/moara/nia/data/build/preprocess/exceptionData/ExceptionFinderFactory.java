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


import org.moara.splitter.utils.Area;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 예외 데이터 탐색기 팩토리
 * 각 문서마다 적용 될 예외 데이터 탐색기를 제공
 *
 * @author wjrmffldrhrl
 */
public class ExceptionFinderFactory {
    private static final String reporterRegx = "기자[^가-힣]";
    private static final String emailFindRegx = "[0-9a-zA-Z][0-9a-zA-Z\\_\\-\\.]+[0-9a-zA-Z]@[0-9a-zA-Z][0-9a-zA-Z\\_\\-]*[0-9a-zA-Z](\\.[a-zA-Z]{2,6}){1,2}([^가-힣a-zA-Z0-9]{0,10})";
    private static final Pattern reporterFindPattern = Pattern.compile(reporterRegx);
    private static final Pattern emailFindPattern = Pattern.compile(emailFindRegx);

    /**
     *
     * 해당 Factory는 인스턴스를 생성할 필요 없이 바로 ExceptionDataFinder 를 반환받을 수 있다.
     * 탐색을 원하는 예외 데이터의 유형을 입력값으로 전달하면 그에 맞는 ExceptionDataFinder 를 반환한다.
     *
     * @param dataType String
     * @return ExceptionDataFinder
     */
    public static ExceptionDataFinder getExceptionFinder(String dataType) {

        if(dataType.equals("reporter")) {
            return text -> {
                Area area;
                Matcher reporterMatcher = reporterFindPattern.matcher(text);

                if(reporterMatcher.find()) {

                    Matcher emailMatcher = emailFindPattern.matcher(text);

                    int start = reporterMatcher.start();
                    int end = reporterMatcher.end();

                    while(emailMatcher.find()) {
                        if(emailMatcher.start() < start) { start = emailMatcher.start(); }
                        end = emailMatcher.end();
                    }

                  area = new Area(start, end);

                } else {
                    area = new Area(0); }


                return area;
            };
        } else {
            return text -> new Area(0);
        }



    }
}
