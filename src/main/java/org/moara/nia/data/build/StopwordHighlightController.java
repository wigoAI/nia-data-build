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

package org.moara.nia.data.build;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 불용어 하이라이트 rest api
 *
 * @author macle
 */
@RestController
public class StopwordHighlightController {


    @RequestMapping(value = "/stopword/wordclass/highlight" , method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public String highlightWordClass(@RequestBody final String jsonValue) {
        return StopwordHighlight.getInstance().highlightWordClassJson(jsonValue);
    }

}
