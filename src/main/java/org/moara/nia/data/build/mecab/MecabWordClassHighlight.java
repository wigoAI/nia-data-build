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
import org.moara.common.string.Check;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

/**
 * mecab을 활용한 단어 품사별 하이라이트
 *
 * @author macle
 */
public class MecabWordClassHighlight {

    private static final Logger logger = LoggerFactory.getLogger(MecabWordClassHighlight.class);
    private static final HashSet<String> KOREAN_LAST_NAME_HASH = new HashSet<>();
    static {
        try {
            System.loadLibrary("MeCab");

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(MecabWordClassHighlight.class.getResourceAsStream("/dic/korean_first_name.dic"), StandardCharsets.UTF_8));
            while(true) {
                String line = br.readLine();
                KOREAN_LAST_NAME_HASH.add(line);
                if(line == null)
                    break;
            }

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
        boolean personNameCheckOption = false;
        if(outArray == null || outArray.length ==0){
            logger.error("out array set error");
            return str;
        }

        for(String option : outArray) {
            if (option.equals("PERSON_NAME")) {
                personNameCheckOption = true;
                break;
            }
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
                last = words[k].length();
            }

//            System.out.println(words[k] + " : " +index + ", " + last + ", " + second);

            // SY, NNG 등등
            String wordClassValue = words[k].substring(index + 1 ,last).trim();


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

            if(personNameCheckOption) {
                int second =  words[k].indexOf(',',last + 1);
                String wordType = words[k].substring(last + 1, second).trim();
//                System.out.println("syllable : " + syllable);
//                System.out.println("wordType : " + wordType);
                if(wordType.equals("인명")) {
                    findPersonName(str, startIndex, lastEnd, indexBuilder);
                }

            }
        }

        if(indexBuilder.length() == 0){
            return "";
        }

        return indexBuilder.substring(1);

    }

    public static void findPersonName(String str, int start, int end, StringBuilder indexBuilder) {


        if(isPersonName(str, start, end)) {
//                System.out.println("input str : " + str);
////                System.out.println(words[k]);
//                System.out.println(str.substring(start, end) + " : " + start + ", " + end);
//                System.out.println();
            indexBuilder.append(";").append("N").append(start).append(",").append(end);
        }

    }

    /**
     * TODO 1. 설정값으로 동작하게 하기
     *          - outArray 를 통해 동작 조절
     *      2. 출력값으로 동작하게 하기
     *          - 처리하고난 뒤 출력하는 값을 파싱해서 이름 블라인드 처리
     *          - N을 같이 넣어서 외부에서 N이 있으면 해당 인덱스는 블라인드 처리하도록
     * @param text String
     * @param start int
     * @param end int
     * @return boolean
     */
    public static boolean isPersonName(String text, int start, int end) {
        String name = text.substring(start, end);

        if(name.length() < 2 || name.length() > 3)
            return false;

        if(start > 0) {
            if(Check.isHangul(text.charAt(start - 1)))
                return false;
        }
        if(start > 4) {
            if(text.startsWith("법무법인 ", start - 5))
                return false;
            if(text.startsWith("주식회사 ", start - 5))
                return false;
        }
        if(end < text.length()) {
            if(Check.isHangul(text.charAt(end)))
                return false;
        }
        if (!KOREAN_LAST_NAME_HASH.contains(name.substring(0, 1)))
            return false;


        return true;


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