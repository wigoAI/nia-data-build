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

package org.moara.nia.data.build.preprocess.personNameFinder;

import org.moara.nia.data.build.Area;

import java.util.List;

/**
 * 인명 탐색기 추상체
 */
public interface PersonNameFinder {

    /**
     *
     * 문자열을 받으면 해당 문자열에서 사람 이름의 위치를 찾아 Area 형태로 인덱스 위치를 알려준다.
     *
     * @param text String
     * @return Area[]
     */
    public List<Area> findPersonNAme(String text);
}