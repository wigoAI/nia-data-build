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

import org.moara.ara.datamining.textmining.dictionary.sentence.SentenceDictionary;
import org.moara.ara.datamining.textmining.dictionary.sentence.extract.SenExtract;
import org.moara.ara.datamining.textmining.document.Document;
import org.moara.common.code.LangCode;
import org.moara.common.data.file.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * XML 형태의 데이터 전처리기
 * TODO 1. DOM 활용해서 XML 파일 읽기
 *      2. 예시 정제 데이터 생성하기
 *      3. 문장 구분 수행하기
 * @author 조승현
 */
public class XmlPreprocessor implements DataPreprocessor {
    private static final Logger logger = LoggerFactory.getLogger(DataPreprocessorImpl.class);
    private final SenExtract senExtract = SentenceDictionary.getInstance().getSenExtract(LangCode.KO, Document.NEWS);
    private final String [] outArray= {"M"};


    @Override
    public void makeByPath(String path) {
        List<File> fileList = FileUtil.getFileList(path, ".xml");
        int count = 0;

        for(File file : fileList) {
            make(file, path);
            count++;
            logger.debug("end length: " + count + "/" + fileList.size());
        }
    }

    @Override
    public void make(File file, String outputPath) {

    }
}
