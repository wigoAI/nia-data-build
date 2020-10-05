package com.wigoai.nlp.example;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.moara.nia.data.build.testWorks.ExceptionFinder;
import org.moara.nia.data.build.testWorks.ExceptionFinderImpl;
import org.moara.nia.data.build.testWorks.TestWorks;
import org.moara.nia.data.build.testWorks.TestWorksImpl;

public class CreateJsonTest {

    @Test
    public void createJsonTestByTestWorks() {

        String dirPath ="D:\\moara\\data\\testworks\\";

        TestWorks testWorks = new TestWorksImpl();
        testWorks.makeByPath(dirPath);
    }

    @Test
    public void findExceptionTest() {
        ExceptionFinder exceptionFinder = new ExceptionFinderImpl();

        String text = "임재업 도내 11,604가구 대상 도민 삶 관련 의식 조사- (동양일보 임재업 기자) 충북 도민중 75.1%는 현 교육제도에 문제가 있다고 생각하는 것으로 나타났다.";

        Assert.assertEquals(exceptionFinder.find(text), 47);
    }
}
