package com.wigoai.nlp.example;

import org.junit.Test;
import org.moara.nia.data.build.mecab.MecabWordClassHighlight;

public class PersonNameTest {

    @Test
    public void findNameTest() {
        String str = "【변 호 인】 변호사 이상용 외";
        String [] outArray= {"PERSON_NAME", "M"};

        System.out.println(MecabWordClassHighlight.indexValue(str, outArray));
    }



}
