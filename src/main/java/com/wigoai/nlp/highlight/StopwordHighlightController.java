package com.wigoai.nlp.highlight;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <pre>
 *  파 일 명 : WordClassHighlight.java
 *  설    명 : 품사를 이용한 highlight
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
@RestController
public class StopwordHighlightController {


    @RequestMapping(value = "/stopword/wordclass/highlight" , method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE)
    public String highlightWordClass(@RequestBody final String jsonValue) {
        return StopwordHighlight.getInstance().highlightWordClassJson(jsonValue);
    }

}
