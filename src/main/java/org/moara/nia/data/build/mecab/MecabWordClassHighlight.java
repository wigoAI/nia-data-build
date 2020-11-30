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

import org.moara.yido.tokenizer.Token;
import org.moara.yido.tokenizer.TokenizerManager;
import org.moara.yido.tokenizer.word.WordToken;

/**
 * mecab을 활용한 단어 품사별 하이라이트
 *
 * @author macle
 */
public class MecabWordClassHighlight {

    /**
     * 불용어 하이라이트 처리
     * @param text text
     * @param outArray 불용어로 지정할 품사 배열
     * @param startTag 풀용어 시작 테그
     * @param endTag 불영어 끌 테크
     * @return 하이라이트가 붙은 값
     */
    public static String change(String text, String [] outArray, String startTag, String endTag){

        if(outArray == null || outArray.length ==0){
            throw new RuntimeException("out array set error");
        }


        StringBuilder changeBuilder = new StringBuilder();
        StringBuilder tagBuilder = new StringBuilder();

        int lastEnd = 0;

        Token [] tokens = TokenizerManager.getInstance().getTokenizer().getTokens(text);

        outer:
        for(Token token : tokens){
            WordToken wordToken = (WordToken)token;
            String partOfSpeech = wordToken.getPartOfSpeech();


            int startIndex = token.getBegin();
            int end = token.getEnd();

            if(lastEnd < startIndex){
                if(tagBuilder.length() > 0){

                    changeBuilder.append(startTag).append(tagBuilder).append(endTag);
                    tagBuilder.setLength(0);
                }
                changeBuilder.append(text, lastEnd, startIndex);

            }

            lastEnd = end;

            for(String out : outArray){
                if(partOfSpeech.startsWith(out)){
                    tagBuilder.append(token.getText());
                    continue outer;
                }
            }

            if(tagBuilder.length() > 0){

                changeBuilder.append(startTag).append(tagBuilder).append(endTag);
                tagBuilder.setLength(0);
            }
            changeBuilder.append(token.getText());

        }

        if(tagBuilder.length() > 0){
            changeBuilder.append(startTag).append(tagBuilder).append(endTag);
            tagBuilder.setLength(0);
        }

        return changeBuilder.toString();
    }

    /**
     * 불용어 위치정보
     * @param text text
     * @param outArray 불용어로 지정할 품사 배열
     * @return 불용어 위치정보 ; 과,로 구분된 n개의 정보
     */
    public static String indexValue(String text, String [] outArray){

        if(outArray == null || outArray.length ==0){
            throw new RuntimeException("out array set error");
        }

        StringBuilder indexBuilder = new StringBuilder();

        Token [] tokens = TokenizerManager.getInstance().getTokenizer().getTokens(text);

        outer:
        for(Token token : tokens){
            WordToken wordToken = (WordToken)token;
            String partOfSpeech = wordToken.getPartOfSpeech();

            for(String out : outArray){
                if(partOfSpeech.startsWith(out)){
                    indexBuilder.append(";").append(wordToken.getBegin()).append(",").append(wordToken.getEnd());
                    continue outer;
                }
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