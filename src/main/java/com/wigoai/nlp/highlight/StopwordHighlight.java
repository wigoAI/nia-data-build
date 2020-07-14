package com.wigoai.nlp.highlight;

import com.wigoai.nlp.highlight.mecab.MecabWordClassHighlight;
import com.wigoai.util.ExceptionUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *  파 일 명 : StopwordhighlightDefaultOption.java
 *  설    명 : 하이라이트 기본 옵션정보
 *             초기에는 mecab 하나만 활용 하므로 여러 형태소 분석기에 대한 옵션처리 하지 않음 
 *             다른 형태소 분석기를 같이 사용할 떄 옵션처리 함
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

public class StopwordHighlight {
    private static final Logger logger = LoggerFactory.getLogger(StopwordHighlight.class);
    
    private static class Singleton {
        private static final StopwordHighlight instance = new StopwordHighlight();
    }


    public static StopwordHighlight getInstance(){
        return Singleton.instance;
    }

    private String startTag = "<s>";
    private String endTag ="</s>";
    private String [] outArray= {
            "M"
            , "S"
            , "E"
            , "V"
            , "J"
            , "X"
            , "E"
    };

    private String configPath = "config/app.yml";


    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public void readConfigFile(){

        try {

            Yaml yaml = new Yaml();
            Reader yamlFile = new FileReader(configPath);

            Map<String, Object> yamlMaps = yaml.load(yamlFile);
            //noinspection unchecked
            Map<String, Object> stopwordMap = (Map<String, Object>) yamlMaps.get("stopword");

            startTag = (String)stopwordMap.get("startTag");
            endTag = (String) stopwordMap.get("endTag");

            //noinspection unchecked
            outArray = ((List<String>)stopwordMap.get("out")).toArray(new String[0]);


            yamlFile.close();
        }catch(Exception e) {
            logger.error(ExceptionUtil.getStackTrace(e));
        }

    }


    private StopwordHighlight(){

    }

    /**
     * 단어 품사를 활용한 하이라이트
     * @return string
     */
    public String highlightWordClassJson(String jsonValue){
        //json 형태로 전달 받았을 경우


        JSONObject jsonObject = new JSONObject(jsonValue);

        try {

            String[] outArray;
            String startTag;
            String endTag;

            if (jsonObject.has("out_array")) {
                //품사 배열
                JSONArray array = jsonObject.getJSONArray("out_array");

                outArray = new String[array.length()];

                for (int i = 0; i < outArray.length; i++) {
                    outArray[i] = array.getString(i);
                }

            } else {
                outArray = this.outArray;
            }

            if (jsonObject.has("start_tag")) {
                startTag = jsonObject.getString("start_tag");
            } else {
                startTag = this.startTag;
            }

            if (jsonObject.has("end_tag")) {
                endTag = jsonObject.getString("end_tag");
            } else {
                endTag = this.endTag;
            }
            JSONObject resultObject = new JSONObject();
            resultObject.put("contents",  MecabWordClassHighlight.change(jsonObject.getString("contents"), outArray, startTag, endTag));
            resultObject.put("message", "success");

            return resultObject.toString();
        }catch(Exception e){
            logger.error(ExceptionUtil.getStackTrace(e));
            jsonObject.put("message", "error");
            return jsonObject.toString();
        }
    }
}