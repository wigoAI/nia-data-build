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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jdk.nashorn.internal.parser.JSONParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.ServerError;

/**
 * 인명 데이터 API
 * @author 조승현
 */
public class Api {
    String mainUrl = "https://koreanname.me/api";
    private final String USER_AGENT = "Mozilla/5.0";
    private String dicFilePath = Api.class.getResource("")
            .getPath().split("out")[0]
            + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "dic"
            + File.separator + "korean_last_name.dic";
//    "/rank/2008/2020/1"


    public void getNames() {


        for(int i = 1 ; i < 350 ; i++) {
            System.out.println("get " + i);
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(dicFilePath, true))){
                JsonObject jsonObject = getJsonObjectByUrl("/rank/2008/2020/" + i);

                JsonArray female = jsonObject.getAsJsonArray("female");
                JsonArray male = jsonObject.getAsJsonArray("male");

                for (int j = 0 ; j < female.size() ; j++) {
                    JsonObject person = (JsonObject) female.get(j);
                    String targetName = person.get("name").toString().replace("\"", "");
                    if (targetName.length() > 2)
                        continue;
                    bw.write( targetName+ "\n");

                }
                for (int j = 0 ; j < male.size() ; j++) {
                    JsonObject person = (JsonObject) male.get(j);
                    String targetName = person.get("name").toString().replace("\"", "");
                    if (targetName.length() > 2)
                        continue;
                    bw.write( targetName+ "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }



    }

    private JsonObject getJsonObjectByUrl(String targetUrl) throws Exception {
        URL url = new URL(mainUrl + targetUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET"); // optional default is GET
        con.setRequestProperty("User-Agent", USER_AGENT); // add request header
        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close(); // print result
        if(responseCode == 200)
            return (JsonObject) JsonParser.parseString(response.toString());
        else
            return null;

    }
}
