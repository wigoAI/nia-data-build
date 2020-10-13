package com.wigoai.nlp.example;

import org.junit.Test;
import org.moara.nia.data.build.personalData.PersonalData;
import org.moara.nia.data.build.personalData.PersonalDataFinderImpl;

public class PersonalDataTest {

    @Test
    public void personalDataTest() {
        String jsonPath = "D:\\moara\\data\\allData\\NIA_1차_excel\\json\\";

        String data = "2019 포항 방문의 해 맞아2019 +8210-5261-1586포항 방문의 해 맞아 [이상원 기자 seagull@imaeil.com ] 포항시는 19일  자세한 사항은 대구국010-5261-1586제오페라어워즈 홈페이지(www.dioa-korea.com)에서 확인 가능하다. 시청 대회의실에서 시 승격 70년과 2019년 포항 방문의 해를 맞아 700만 관광객 유치를 위한 전략보고와 실천대회를 열었다.";

        for(PersonalData personalData : new PersonalDataFinderImpl("email").find(data)) {
            System.out.println(personalData.getValue());
        }
        for(PersonalData personalData : new PersonalDataFinderImpl("url").find(data)) {
            System.out.println(personalData.getValue());
        }
        for(PersonalData personalData : new PersonalDataFinderImpl("ph").find(data)) {
            System.out.println(personalData.getValue());
        }
    }
}
