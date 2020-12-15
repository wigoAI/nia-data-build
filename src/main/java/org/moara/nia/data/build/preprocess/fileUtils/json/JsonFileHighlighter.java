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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.moara.common.data.file.FileUtil;
import org.moara.yido.tokenizer.TokenizerManager;
import org.moara.yido.tokenizer.word.WordToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;


/**
 * json file highlighting
 * @author wjrmffldrhrl
 */
public class JsonFileHighlighter extends JsonFileEditor {
    private static final Logger logger = LoggerFactory.getLogger(JsonFileHighlighter.class);
    String[] outArray = {"M"};

    @Override
    protected JsonArray getEditText(JsonArray text){
        JsonArray editText = new JsonArray();

        try{
            // text 접근
            int index = 0;
            for(int j = 0; j < text.size() ; j++) {
                JsonArray paragraph = text.get(j).getAsJsonArray();
                JsonArray editParagraph = new JsonArray();

                // 문단 수정
                for (int k = 0 ; k < paragraph.size() ; k++) {
                    JsonObject sentence = paragraph.get(k).getAsJsonObject();
                    String targetSentence = sentence.get("sentence").getAsString();

                    WordToken [] wordTokens = (WordToken [])TokenizerManager.getInstance().getTokenizer().getTokens(targetSentence);

                    StringBuilder indexBuilder = new StringBuilder();

                    outer:
                    for(WordToken wordToken: wordTokens){

                        String partOfSpeech = wordToken.getPartOfSpeech();

                        for(String out : outArray){
                            if(partOfSpeech.startsWith(out)){
                                indexBuilder.append(";").append(wordToken.getBegin()).append(",").append(wordToken.getEnd());
                                continue outer;
                            }
                        }
                    }

                    String mecabResult = "";
                    if (indexBuilder.length() > 0) {
                        mecabResult = indexBuilder.substring(1);
                    }

                    sentence.addProperty("highlight_indices", mecabResult);

                    editParagraph.add(copySentence(index++, sentence));
                }

                if(editParagraph.size() != 0) {
                    editText.add(editParagraph);
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            logger.debug("Error in highlighting");
            editText = new JsonArray();
        }


        return editText;
    }

    public static void main(String[] args) {
        JsonFileEditor jsonFileEditor = new JsonFileHighlighter();

        String path = "D:\\moara\\data\\기고문_3차\\json\\";
        List<File> fileList = FileUtil.getFileList(path, ".json");

        for(File file : fileList) {
            jsonFileEditor.editJsonFile(file, path);
        }
    }
}
