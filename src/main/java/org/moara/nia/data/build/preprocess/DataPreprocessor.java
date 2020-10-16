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


package org.moara.nia.data.build.preprocess;

import java.io.File;

/**
 * 데이터 전처리기 추상체
 * 데이터 전처리기를 해당 추상체를 구현하여 제작
 * @author 조승현
 */
public interface DataPreprocessor {

    /**
     *
     * 정제 데이터 생성
     * 입력된 파일을 구현체에서 알맞게 정제하여 반환한다.
     * 파일 형태로 출력하기 때문에 반환값이 없다.
     * @param file File
     * @param outputPath String
     */
    void make(File file, String outputPath);

    /**
     *
     * 경로에 있는 정제 데이터 확인
     * 데이터 정제시 많은 양의 데이터를 처리하기 때문에
     * File 리스트를 생성하는 메서드 필요
     * @param path
     */
    void makeByPath(String path);
}
