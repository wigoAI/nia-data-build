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
package org.moara.nia.data.build.mecab;

import org.chasen.mecab.Tagger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * mecab을 활용한 단어 품사별 하이라이트
 *
 * @author macle
 */
public class MecabWordClassHighlight {

    private static final Logger logger = LoggerFactory.getLogger(MecabWordClassHighlight.class);

    static {
        try {
            System.loadLibrary("MeCab");
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            logger.error("Cannot load the example native code.\nMake sure your LD_LIBRARY_PATH contains '.'\n" + e);
            System.exit(1);
        }

    }


    public static String change(String str, String [] outArray, String startTag, String endTag){

        if(outArray == null || outArray.length ==0){
            logger.error("out array set error");
            return str;
        }

        StringBuilder changeBuilder = new StringBuilder();

        Tagger tagger = new Tagger();
        String parse = tagger.parse(str);
        String [] words =  parse.split("\n");

        int lastEnd = 0;

        StringBuilder tagBuilder = new StringBuilder();

        outer:
        for (int k = 0; k <words.length -1 ; k++) {
            int index = words[k].indexOf('\t');
            String syllable = words[k].substring(0,index).trim();

            int last = words[k].indexOf(',',index);
            if(last == -1){
                last =words[k].length();
            }
            String wordClassValue = words[k].substring(index +1 ,last).trim();




            int startIndex = str.indexOf(syllable, lastEnd);
            int end = startIndex + syllable.length();

            if(lastEnd < startIndex){
                if(tagBuilder.length() > 0){

                    changeBuilder.append(startTag).append(tagBuilder).append(endTag);
                    tagBuilder.setLength(0);
                }
                changeBuilder.append(str, lastEnd, startIndex);

            }

            lastEnd = end;

            for(String out : outArray){
                if(wordClassValue.startsWith(out)){
                    tagBuilder.append(syllable);
                    continue outer;
                }
            }

            if(tagBuilder.length() > 0){

                changeBuilder.append(startTag).append(tagBuilder).append(endTag);
                tagBuilder.setLength(0);
            }
            changeBuilder.append(syllable);

        }

        if(tagBuilder.length() > 0){
            changeBuilder.append(startTag).append(tagBuilder).append(endTag);
            tagBuilder.setLength(0);
        }
        return changeBuilder.toString();
    }


    public static String indexValue(String str, String [] outArray){
        if(outArray == null || outArray.length ==0){
            logger.error("out array set error");
            return str;
        }


        Tagger tagger = new Tagger();
        String parse = tagger.parse(str);
        String [] words =  parse.split("\n");

        StringBuilder indexBuilder = new StringBuilder();

        int lastEnd = 0;

        for (int k = 0; k <words.length -1 ; k++) {
            int index = words[k].indexOf('\t');

            int last = words[k].indexOf(',',index);
            if(last == -1){
                last =words[k].length();
            }

            String wordClassValue = words[k].substring(index +1 ,last).trim();
            boolean isOut = false;

            for(String out : outArray){
                if(wordClassValue.startsWith(out)){

                    isOut = true;
                    break;
                }
            }

            String syllable = words[k].substring(0,index).trim();
            int startIndex = str.indexOf(syllable, lastEnd);
            lastEnd = startIndex + syllable.length();

            if(isOut){

                indexBuilder.append(";").append(startIndex).append(",").append(lastEnd);
            }


        }

        if(indexBuilder.length() == 0){
            return "";
        }

        return indexBuilder.substring(1);

    }

    public static void main(String[] args) {

        String [] outArray= {
                "M"
//                , "S"
//                , "E"
//                , "V"
//                , "J"
//                , "X"
//                , "E"
        };

        String text = "2017년 2월(404억2,921만원) 보다 무려 161.7%(653억8,751만원) 증액됐다.";

        System.out.println(change(text, outArray,"<s>","</s>"));
        System.out.println(indexValue(text, outArray));

    }

}