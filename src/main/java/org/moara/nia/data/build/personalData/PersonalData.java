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

package org.moara.nia.data.build.personalData;

/**
 * 개인 정보 데이터
 * 개인 정보 탐색기에 의해 추출된 데이터를 표현하는 클래스
 *
 * @author 조승현
 */
public class PersonalData {
    private final int start;
    private final int end;
    private final String value;
    private final String type;

    public PersonalData(int start, int end, String value, String type) {
        this.start = start;
        this.end = end;
        this.value = value;
        this.type = type;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }
}
