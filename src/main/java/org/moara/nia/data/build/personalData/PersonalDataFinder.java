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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

/**
 * 개인 정보 탐색기 인터페이스
 * 블라인드 처리 할 데이터들을 조회할 추상체
 * 이 인터페이스를 구현하여 .regx 파일에 담겨있는 정규표현식으로
 * 원하는 데이터를 찾아낸다.
 *
 * @author 조승현
 * */
public interface PersonalDataFinder {

    List<PersonalData> find(String sentence);
    String bastPath = "/regx/";
    Function<String, String> fileReader = (t) -> {
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(PersonalDataFinder.class.getResourceAsStream( bastPath + t + ".regx"), StandardCharsets.UTF_8))) {
            return br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    };

}
