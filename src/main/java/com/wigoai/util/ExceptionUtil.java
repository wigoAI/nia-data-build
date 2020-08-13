/*
 * Copyright (C) 2020 Wigo Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wigoai.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * 예외처리 관련 유틸
 *
 * @author macle
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
