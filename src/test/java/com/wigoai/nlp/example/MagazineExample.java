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

package com.wigoai.nlp.example;

import com.google.gson.JsonObject;
import org.junit.Test;
import org.moara.nia.data.build.preprocess.DataPreprocessor;
import org.moara.nia.data.build.preprocess.DataPreprocessorImpl;
import org.moara.nia.data.build.preprocess.SizeTypeUtil;
import org.moara.nia.data.build.preprocess.TextPreprocessor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author macle
 */
public class MagazineExample {

    public static void main(String[] args) {

        int count = 0;
        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\moara\\data\\allData\\잡지\\잡지_주간경향.월간중앙.이코노미스트_13429건(Tab_Delimited).txt"), StandardCharsets.UTF_8))){

            String line;
            while ((line = br.readLine()) != null) {
                count++;
                String [] columns = line.split("\t");


                System.out.println(columns.length);

                JsonObject document = new JsonObject();
                document.addProperty("id", columns[0]);
                document.addProperty("category", "잡지");
                document.addProperty("media_type", "online");
                document.addProperty("media_sub_type", "잡지");
                document.addProperty("media_name", columns[1]);
                document.addProperty("size", SizeTypeUtil.getSizeType(columns[6]));
                document.addProperty("char_count", columns[7]);
                document.addProperty("publish_date", columns[4] );
                document.addProperty("title", columns[8]);

                System.out.println(columns[9]);

            }
        }catch(Exception e){
            e.printStackTrace();
        }


        System.out.println(count);


    }

    @Test
    public void TextPreprocessorTest() {
        TextPreprocessor textPreprocessor = new TextPreprocessor();

        String dirPath ="D:\\moara\\data\\allData\\잡지\\";

        textPreprocessor.makeByPath(dirPath);


    }
}
