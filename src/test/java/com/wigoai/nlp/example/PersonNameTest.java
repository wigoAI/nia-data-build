package com.wigoai.nlp.example;

import org.junit.Test;
import org.moara.nia.data.build.mecab.MecabWordClassHighlight;
import org.moara.nia.data.build.preprocess.personNameFinder.Api;
import org.moara.nia.data.build.preprocess.personNameFinder.PersonNameFinder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PersonNameTest {

    @Test
    public void findNameTest() {
        String str = "【변 호 인】 변호사 조승현 외";
        String [] outArray= {"PERSON_NAME", "M"};

        System.out.println(MecabWordClassHighlight.indexValue(str, outArray));
    }

    @Test
    public void replaceTest() {
        String str = "【원고, 피상고인】 한국철도공사 (소송대리인 법무법인(유한) 동인 담당변호사 홍성무 외 1인)";
        String regx = "\\s[가-힣]{2,3}\\s외\\s\\d인";
        Pattern pattern = Pattern.compile(regx);
        Matcher matcher = pattern.matcher(str);

        while(matcher.find()) {
            System.out.println(matcher.start());
            System.out.println(matcher.end());
        }
        System.out.println(str.replaceAll(regx, " *** 외 *인"));

    }

    @Test
    public void getNamesTest() {
        Api api = new Api();

        api.getNames();
    }

}
