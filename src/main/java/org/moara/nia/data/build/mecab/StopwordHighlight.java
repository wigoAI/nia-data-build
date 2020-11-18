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
package org.moara.nia.data.build.mecab;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

/**
 * 뷸용어 하이라이트
 *
 * @author macle
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
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());

        }

    }


    private StopwordHighlight(){

    }


    public String highlightWordClass(String value){
       return MecabWordClassHighlight.change(value, outArray, startTag, endTag);
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
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            jsonObject.put("message", "error");
            return jsonObject.toString();
        }
    }
}