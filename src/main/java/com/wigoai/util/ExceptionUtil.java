package com.wigoai.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * <pre>
 *  파 일 명 : ExceptionUtil.java
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

public class ExceptionUtil {

    /**
     *  Exception.printStackTrace value get
     * @param e Exception e
     * @return stackTrace String Value
     */
    public static  String getStackTrace(Exception e){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(out);
        e.printStackTrace(printStream);
        String exceptionStackTrace = out.toString();

        try{out.close();}catch(Exception ignore){}
        try{printStream.close();}catch(Exception ignore){}

        return exceptionStackTrace;
    }
}
