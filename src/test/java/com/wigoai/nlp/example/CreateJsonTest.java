package com.wigoai.nlp.example;

import org.junit.Assert;
import org.junit.Test;
import org.moara.nia.data.build.preprocess.*;

public class CreateJsonTest {

//    『』

    @Test
    public void createJsonTestByTestWorks() {
        String dirPath ="D:\\moara\\data\\allData\\";
        DataPreprocessor dataPreprocessor = new DataPreprocessorImpl();
        for(int i = 3; i < 6 ; i++) {
            dataPreprocessor.makeByPath(dirPath + "NIA_" + (i + 1) + "차_excel\\");
        }


    }

    @Test
    public void createTestJsonTest() {
        String dirPath ="D:\\moara\\data\\allData\\test\\";

        DataPreprocessor dataPreprocessor = new DataPreprocessorImpl();

        dataPreprocessor.makeByPath(dirPath);
    }

    @Test
    public void findExceptionTest() {
        ExceptionFinder exceptionFinder1 = ExceptionFinderFactory.getExceptionFinder("동양일보");
        String text1 = "임재업 도내 11,604가구 대상 도민 삶 관련 의식 조사- (동양일보 임재업 기자) 충북 도민중 75.1%는 현 교육제도에 문제가 있다고 생각하는 것으로 나타났다.";

        Assert.assertEquals(exceptionFinder1.find(text1), 47);


        ExceptionFinder exceptionFinder2 = ExceptionFinderFactory.getExceptionFinder("전라일보");
        String text2 = "여야4당, 패스트트랙 추진에 한국당 “의원직 총사퇴” 반발 공전끝낸 국회 차질 불가피 김형민 기자l jal74@naver.com \\r\\n3월 임시국회가 11일부터 ";

        Assert.assertEquals(exceptionFinder2.find(text2), 71);

    }

}
