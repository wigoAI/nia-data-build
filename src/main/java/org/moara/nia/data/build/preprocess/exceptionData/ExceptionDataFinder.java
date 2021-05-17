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

package org.moara.nia.data.build.preprocess.exceptionData;


import org.moara.yido.splitter.utils.Area;

/**
 * 예외 데이터 탐색기 추상체
 * ExceptionFinderFactory 에서 구현하여 반환한다.
 *
 * @author wjrmffldrhrl
 */
@FunctionalInterface
public interface ExceptionDataFinder {

    /**
     *
     * 입력값으로 받은 text에서 구현체의 방식에 맞게 예외 데이터를 찾는다.
     * 찾은 예외 데이터는 Area객체로 위치를 반환한다.
     *
     *
     * @param text String
     * @return Area
     */
    Area find(String text);
}
