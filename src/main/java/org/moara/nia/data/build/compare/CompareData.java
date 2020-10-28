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

package org.moara.nia.data.build.compare;

import com.google.gson.JsonObject;

import java.io.IOException;

/**
 * JSON 데이터 비교
 * @author
 */
public interface CompareData {

    /**
     * 데이터 정제 이후 생성된 데이터를 개선 후 비교하는 메서드
     * 개선 전 데이터와 개선 후 데이터를 입력값으로 전달해 비교한다.
     *
     * 데이터 유형에 따라 알맞은 메서드를 구현
     *
     * @param beforeJson JsonObject
     * @param afterJson JsonObject
     */
    void compare() throws IOException;
}
