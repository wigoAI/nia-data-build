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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.moara.common.data.file.FileUtil;
import org.moara.nia.data.build.preprocess.exception.LongDataException;
import org.moara.nia.data.build.preprocess.exception.OverlapDataException;
import org.moara.nia.data.build.preprocess.exception.QaDataException;

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
 * @author 조승현
 */
public class TextPreprocessor extends DataPreprocessorImpl{

    @Override
    public void makeByPath(String path) {
        List<File> fileList = FileUtil.getFileList(path, ".txt");
        int count = 0;

        for(File file : fileList) {
            make(file, path);
            count++;
        }

    }

    @Override
    public void make(File file, String path) {
        String outputPath = path + "json";
        File outputDir = new File(outputPath);

        if(!outputDir.exists()) {
            outputDir.mkdir();
            System.out.println("create dir : " + outputPath);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();


        JsonObject jsonObject = initJsonObject(file);
        JsonArray documents = getDocuments(file);
        jsonObject.add("documents", documents);

        FileUtil.fileOutput(gson.toJson(jsonObject), outputPath + "\\" + getFileNameWithoutFormat(file) + ".json",false);


    }

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
//        System.out.println(contents);
        List<String> paragraphList = getParagraphList(contents);

        String documentId = columns[0];
        try {
            JsonArray text = getText(paragraphList);
            document.add("text", text);
        } catch (OverlapDataException | QaDataException | LongDataException e) {
            System.out.println("drop data in id : " + documentId);
            System.out.println(e.toString());
            return null;
        } catch (Exception e) {
            System.out.println("this data something is wrong : " + documentId);
        }

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
