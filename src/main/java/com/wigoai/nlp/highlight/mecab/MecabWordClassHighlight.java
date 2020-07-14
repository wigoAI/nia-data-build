package com.wigoai.nlp.highlight.mecab;

import com.wigoai.util.ExceptionUtil;
import org.chasen.mecab.Tagger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 *  파 일 명 : MecabWordClassHighlight.java
 *  설    명 :품사를 이용한 highlight (mecab) 활용
 *           초기에는 mecab만 활용할 예정이므로 이클래스 하나에 작성함
 *
 *  작 성 자 : macle(김용수)
 *  작 성 일 : 2020.07
 *  버    전 : 1.0
 *  수정이력 :
 *  기타사항 :
 * </pre>
 *
 * @author Copyrights 2020 by ㈜ WIGO. All right reserved.
 */

public class MecabWordClassHighlight {


    private static final Logger logger = LoggerFactory.getLogger(MecabWordClassHighlight.class);


    static {
        try {
            System.loadLibrary("MeCab");
        } catch (Exception e) {
            logger.error(ExceptionUtil.getStackTrace(e));
            logger.error("Cannot load the example native code.\nMake sure your LD_LIBRARY_PATH contains \'.\'\n" + e);
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
}