/*
 * Copyright (C) 2021 Wigo Inc.
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
package org.moara.nia.sentence.line;

import com.seomse.commons.utils.FileUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 라인수 측정 (디렉토리 별)
 * @author macle
 */
public class LineCount {


    public static String getView(String dirPath, String name){

        StringBuilder sb = new StringBuilder();

        NameCount [] nameCounts = getNameCounts(dirPath);

        sb.append(name).append("\n");
        for(NameCount nameCount : nameCounts){
            sb.append("\t").append(nameCount.name).append(": ").append(nameCount.count).append("\n");
        }
        return sb.toString();
    }

    @SuppressWarnings("ConstantConditions")
    public static NameCount [] getNameCounts(String dirPath){
        File file = new File(dirPath);
        File [] dirs = file.listFiles();

        List<NameCount> nameCountList = new ArrayList<>();

        for(File dir : dirs){
            if(dir.isFile()){
                continue;
            }

            String name = dir.getName();

            JSONArray array = new JSONArray(FileUtil.getFileContents(dir.listFiles()[0] ,"UTF-8"));


            long count = 0;
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                JSONArray sentences = obj.getJSONArray("text");

                count += sentences.length();
            }

            NameCount nameCount = new NameCount();
            nameCount.name = name;
            nameCount.count = count;
            nameCountList.add(nameCount);
        }
        return nameCountList.toArray(new NameCount[0]);
    }

    public static void main(String[] args) {

        System.out.println(getView("work_dir/1.Training/원시데이터", "Training"));
        System.out.println(getView("work_dir/2.Validation/원시데이터", "Validation"));
        System.out.println(getView("work_dir/3.Test/원시데이터", "Test"));
        System.out.println(getView("work_dir/4.Sample/원시데이터", "Sample"));
    }
}
