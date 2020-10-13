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

    void make(File file, String outputPath);
    void makeByPath(String path);
}
