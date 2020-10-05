package com.wigoai.nlp.example;

import org.moara.nia.data.build.mecab.MecabWordClassHighlight;

/**
 * <pre>
 *  파 일 명 : StopwordHigh.java
 *  설    명 :
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

public class StopwordHighlightExample {

    public static void main(String[] args) {

        String [] outArray =  {
                "M"
                , "S"
                , "E"
                , "V"
                , "J"
                , "X"
                , "E"

        };
        String testText = "국내 양대 포털인 네이버·카카오가 어떤 혐오표현을 얼마나 규제하는지에 대한 정보를 공개해야 한다는 지적이 나왔다.\n"
                +  "\n"
                +"한국인터넷자율정책기구(KISO)는 9일 오후 서울 종로구 새문안로 S타워에서 '온라인상 혐오표현 그 해법은 무엇인가'라는 주제로 포럼을 열었다. KISO 이사회 의장은 여민수 카카오 공동대표가 맡고 있다"
                ;
        System.out.println(MecabWordClassHighlight.change(testText,outArray,"<s>","</s>"));

    }
}
