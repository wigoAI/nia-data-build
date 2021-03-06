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

package org.moara.nia.data.build.preprocess;

/**
 * @author macle
 */
public class SizeTypeUtil {

    /**
     * 약속한 사이즈 유형 얻기
     * @param typeValue String
     * @return String
     */
    public static String getSizeType(String typeValue){

        switch (typeValue) {
            case "대":
            case "초대":
                return "large";

            case "중":
                return "medium";

            case "소":
            case "초소":
                return "small";

            default:
                throw new RuntimeException("size type error: " + typeValue);

        }
    }

}
