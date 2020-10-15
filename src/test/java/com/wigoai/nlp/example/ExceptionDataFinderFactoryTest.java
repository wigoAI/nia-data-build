package com.wigoai.nlp.example;

import org.junit.Assert;
import org.junit.Test;
import org.moara.nia.data.build.preprocess.exceptionData.ExceptionDataFinder;
import org.moara.nia.data.build.preprocess.exceptionData.ExceptionFinderFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExceptionDataFinderFactoryTest {

    @Test
    public void exceptionFinderTest() {
        ExceptionDataFinder exceptionDataFinder = ExceptionFinderFactory.getExceptionFinder("reporter2");
        Assert.assertEquals(exceptionDataFinder.find("[이혜진 기자 lhj@imaeil.com] KBS 연기대상을 빛낸 가수 효린의 축하무대를 본 배우들의 각양각색 표정이 온라인에서 화제다.").getEnd(), 24);
        Assert.assertEquals(exceptionDataFinder.find("[이혜진 기자 lhj@imaeil.com] KBS 연기대상을 빛낸 가수 효린의 축하무대를 본 배우들의 각양각색 표정이 온라인에서 화제다.").getStart(), 5);
        Assert.assertEquals(exceptionDataFinder.find("김용 기자l km4966@daum.net").getEnd(), 22);
        Assert.assertEquals(exceptionDataFinder.find("/김진호기자kjh@kbmaeil.com").getEnd(),21);
        Assert.assertEquals(exceptionDataFinder.find("김다이기자 ").getEnd(), 6);
        Assert.assertEquals(exceptionDataFinder.find("hello world").getEnd(), 0);
    }

    @Test
    public void ind() {
        System.out.println("[이혜진 기자 lhj@imaeil.com] KBS 연기대상을 빛낸 가수 효린의 축하무대를 본 배우들의 각양각색 표정이 온라인에서 화제다.".lastIndexOf("기자"));
    }

}