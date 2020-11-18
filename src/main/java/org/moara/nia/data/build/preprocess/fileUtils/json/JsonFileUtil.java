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
package org.moara.nia.data.build.preprocess.fileUtils.json;

import com.google.gson.*;
import org.moara.common.data.file.FileUtil;
import org.moara.nia.data.build.preprocess.TextPreprocessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.regex.Pattern;

/**
 * json file 관련 유틸
 */
public class JsonFileUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonFileUtil.class);

    /**
     * File 객체로부터 JsonObject 를 생성한다.
     *
     * @param file File
     * @return JsonObject
     */
    public JsonObject getJsonObjectByFile(File file) {
        JsonElement element = null;

        try {
            element = JsonParser.parseReader(new FileReader(file.getPath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return element.getAsJsonObject();
    }

    public void createJsonFile(String outputPath, String jsonFileName, JsonObject jsonObject) {
        createDir(outputPath);

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        FileUtil.fileOutput(gson.toJson(jsonObject), outputPath + "\\" + jsonFileName + ".json",false);

    }

    protected String createDir(String dirPath) {
        File outputDir = new File(dirPath);

        if (!outputDir.exists()) {
            outputDir.mkdir();
            logger.debug("create dir : " + dirPath);
        }

        return dirPath;
    }

    protected String createDir(String outputPath, String dirName) {
        String dirPath = outputPath + dirName + "\\";
        return createDir(dirPath);
    }

    protected JsonObject copySentence(int index, JsonObject targetSentence) {
        JsonObject editSentence = new JsonObject();
        editSentence.addProperty("index", index);
        editSentence.add("sentence", targetSentence.get("sentence"));
        editSentence.add("highlight_indices", targetSentence.get("highlight_indices"));
        return editSentence;
    }

    protected JsonObject copyJsonObjectInfo(JsonObject targetJsonObject) {
        JsonObject copiedJsonObject = new JsonObject();
        copiedJsonObject.add("name", targetJsonObject.get("name"));
        copiedJsonObject.add("delivery_date", targetJsonObject.get("delivery_date"));

        return copiedJsonObject;
    }

    protected JsonObject copyDocumentInfo(JsonObject targetJsonObject) {
        JsonObject copiedJsonObject = new JsonObject();
        copiedJsonObject.add("id", targetJsonObject.get("id"));
        copiedJsonObject.add("category", targetJsonObject.get("category"));
        copiedJsonObject.add("media_type", targetJsonObject.get("media_type"));
        copiedJsonObject.add("media_sub_type", targetJsonObject.get("media_sub_type"));
        copiedJsonObject.add("media_name", targetJsonObject.get("media_name"));
        copiedJsonObject.add("size", targetJsonObject.get("size"));
        copiedJsonObject.add("char_count", targetJsonObject.get("char_count"));
        copiedJsonObject.add("publish_date", targetJsonObject.get("publish_date"));
        copiedJsonObject.add("title", targetJsonObject.get("title"));

        return copiedJsonObject;
    }

    protected JsonArray[] getDocumentsArrayByFileList(List<File> fileList) {
        JsonArray[] documentsArray = new JsonArray[fileList.size()];

        int index = 0;
        for(File file : fileList) {
            JsonObject jsonObject = getJsonObjectByFile(file);
            JsonArray documents = jsonObject.getAsJsonArray("documents");
            documentsArray[index++] = documents;
        }

        return documentsArray;

    }

    protected Pattern getPattern(String regxStr) {
        return Pattern.compile(regxStr);
    }


}
