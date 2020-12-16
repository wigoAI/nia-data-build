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

package org.moara.nia.data.build.evaluator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.moara.nia.data.build.preprocess.fileUtils.json.JsonFileUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 전처리 데이터에서 문장 구분기 성능을 평가하기 위해 문장 데이터를 추출한다.
 */
public class SentenceExtractor extends JsonFileUtil {

    /**
     * 전처리 결과물로부터 문장 데이터를 추출한다.
     * @param file 전처리 결과물 json file
     */
    public void createSentenceFiles(File file) {
        JsonObject jsonObject = getJsonObjectByFile(file);

        JsonArray documents = jsonObject.getAsJsonArray("documents");

        for (int i = 2000; i < 2004; i++) {
            JsonObject document = documents.get(i).getAsJsonObject();
            List<String> sentences;

            try (BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\git\\evaluation\\data\\last\\submit" + i + ".txt"))){

                sentences = getSentences(document);

                for(String str : sentences)
                    bw.write(str + "\n");

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("error while write file");
            } catch (RuntimeException ignored) { }



        }
    }


    private List<String> getSentences(JsonObject document) {
        List<String> sentences = new ArrayList<>();
        JsonArray text = document.getAsJsonArray("text");
        if (text == null) {
            throw new RuntimeException("no text");
        }



        for (int i = 0; i < text.size(); i++) {
            JsonArray paragraph = text.get(i).getAsJsonArray();

            for (int j = 0; j < paragraph.size(); j++) {
                JsonObject sentenceObject = paragraph.get(j).getAsJsonObject();
                String sentence = sentenceObject.get("sentence").getAsString();
                sentences.add(sentence);
            }

        }


        return sentences;
    }

    public static void main(String[] args) {
        SentenceExtractor sentenceExtractor = new SentenceExtractor();

        sentenceExtractor.createSentenceFiles(new File("D:\\moara\\data\\allData\\NIA_1차_excel\\json\\test.json"));
    }

}
