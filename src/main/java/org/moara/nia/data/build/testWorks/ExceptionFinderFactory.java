package org.moara.nia.data.build.testWorks;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExceptionFinderFactory {
    public static ExceptionFinder getExceptionFinder(String dataType) {
        ExceptionFinder exceptionFinder = text -> 0;

        if(dataType.equals("동양일보")) {
            String targetException = " 기자)";
            exceptionFinder =  text -> text.lastIndexOf(targetException) + targetException.length();
        } else if(dataType.equals("전라일보")) {
            String regular = "[0-9a-zA-Z][0-9a-zA-Z\\_\\-\\.]+[0-9a-zA-Z]@[0-9a-zA-Z][0-9a-zA-Z\\_\\-]*[0-9a-zA-Z](\\.[a-zA-Z]{2,6}){1,2}";

            exceptionFinder = text -> {
                Matcher emailMatcher = Pattern.compile(regular).matcher(text);
                while (emailMatcher.find()) {
                    return emailMatcher.end();
                }
                return 0;
            };
        }

        return exceptionFinder;
    }
}
