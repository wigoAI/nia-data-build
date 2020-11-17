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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 *
 * txt 파일 전처리기
 *
 * @author wjrmffldrhrl
 */
public class TextPreprocessor extends DataPreprocessorImpl{


    /**
     * Constructor
     * DataPreprocessorImpl 에서 정의한 fileExtension 을 상속받아 초기화 한다.
     * 해당 변수는 파일 리스트를 가져올 때 사용된다.
     */
    public TextPreprocessor() { this.fileExtension = ".txt"; }

    @Override
    protected JsonArray getDocuments(File file) {
        JsonArray documents = new JsonArray();

        int dropDataCount = 0;
        int normalDataCount = 0;
        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file.getPath()), StandardCharsets.UTF_8))) {
            String line;

            while ((line = br.readLine()) != null) {
                JsonObject document = getDocument(line);

                if (document == null) {
                    dropDataCount++;
                    continue;
                }

                documents.add(document);
                normalDataCount++;

            }

            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.print("  Drop data : " + dropDataCount);
            System.out.println("  Normal data : " + (normalDataCount));
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

            return documents;

        }catch(Exception e){
            e.printStackTrace();
        }

        return documents;
    }

    private JsonObject getDocument(String line) {
        String [] columns = line.split("\t");
        JsonObject document = addDocumentInfo(columns);

        String contents = columns[9];
        if(contents == null || contents.length() < 2){
            return null;
        }

        List<String> paragraphList = getParagraphList(contents);
        String documentId = columns[0];
        addTextToDocument(documentId, document, paragraphList);

        return document;
    }

    private JsonObject addDocumentInfo(String[] columns) {
        JsonObject document = new JsonObject();
        document.addProperty("id", columns[0]);
        document.addProperty("category", "잡지");
        document.addProperty("media_type", "online");
        document.addProperty("media_sub_type", "잡지");
        document.addProperty("media_name", columns[1]);
        document.addProperty("size", SizeTypeUtil.getSizeType(columns[6]));
        document.addProperty("char_count", columns[7]);
        document.addProperty("publish_date", columns[4] );
        document.addProperty("title", columns[8]);
        return document;
    }
}
