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

package org.moara.nia.data.build.preprocess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.moara.ara.datamining.textmining.dictionary.sentence.SentenceDictionary;
import org.moara.ara.datamining.textmining.dictionary.sentence.extract.SenExtract;
import org.moara.ara.datamining.textmining.document.sentence.Sentence;
import org.moara.common.code.LangCode;
import org.moara.common.data.file.FileUtil;
import org.moara.common.string.Check;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;

import java.text.SimpleDateFormat;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * XML 형태의 데이터 전처리기
 *
 * @author 조승현
 */
public class XmlPreprocessor implements DataPreprocessor {
    private static final Logger logger = LoggerFactory.getLogger(XmlPreprocessor.class);
    private final SenExtract senExtract = SentenceDictionary.getInstance().getSenExtract(LangCode.KO, "NEWS");
    private HashSet<String> splitStrSet = new HashSet<>();

    private final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder documentBuilder;

    /**
     * Constructor
     */
    public XmlPreprocessor() {
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public HashSet<String> getSplitStrSet() {
        return splitStrSet;
    }

    @Override
    public void makeByPath(String path) {
        List<File> fileList = FileUtil.getFileList(path, ".xml");
        String outputPath = "D:\\moara\\data\\law\\json5";
        String[] splitPath = path.split("\\\\");
        String jsonFileName = splitPath[splitPath.length - 1];

        File outputDir = new File(outputPath);
        if(!outputDir.exists()) {
            outputDir.mkdir();
        }

        jsonFileName += "_" + fileList.size() + "건_";
        JsonObject jsonObject = initJsonObject(jsonFileName);
        jsonObject.add("documents", getDocuments(fileList));

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        FileUtil.fileOutput(gson.toJson(jsonObject), outputPath + "\\" + jsonFileName + ".json",false);
        logger.debug("end file name : " +jsonFileName);

    }


    private JsonArray getDocuments(List<File> fileList) {
        JsonArray documents = new JsonArray();

        int count = 0;
        for(File file : fileList) {
            logger.debug("start file name: " +file.getName());
            documents.add(getDocument(file));

            count++;
            logger.debug("end length: " + count + "/" + fileList.size());

        }

        return documents;
    }

    private JsonObject initJsonObject(String name) {
        System.out.println("init json");
        JsonObject jsonObject = new JsonObject() ;
        String delivery_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        jsonObject.addProperty("name", name);
        jsonObject.addProperty("delivery_date", delivery_date);

        return jsonObject;
    }

    private JsonObject getDocument(File file) {

        Element dom = getDomElement(file);
        JsonObject document = initJsonObject(dom);
        JsonArray text = getText(dom);
        document.add("text", text);

        return document;
    }

    private JsonObject initJsonObject(Element dom) {
        JsonObject document = new JsonObject();
        document.addProperty("id", getTagValue("SerialNo", dom));
        document.addProperty("title", getTagValue("CaseName", dom));
        document.addProperty("case_number", getTagValue("CaseNo", dom));
        document.addProperty("publish_date", getTagValue("PubDate", dom));
        document.addProperty("size","size");
        document.addProperty("char_count",0);
        document.addProperty("court_name", getTagValue("CourtName", dom));
        document.addProperty("court_code", getTagValue("CourtCode", dom));
        document.addProperty("category", getTagValue("CaseType", dom));
        document.addProperty("case_code", getTagValue("CaseCode", dom));
        document.addProperty("case_type", getTagValue("CaseType", dom, 1));


        int documentSize = dom.getElementsByTagName("Jurisdiction").item(0).getTextContent().length();
        document.addProperty("char_count", documentSize);

        if(documentSize < 600)
            document.addProperty("size","small");
        else if( documentSize < 1500 )
            document.addProperty("size","medium");
        else
            document.addProperty("size", "large");

        return document;
    }

    private String getTagValue(String tagName, Element dom) {
        return dom.getElementsByTagName(tagName).item(0).getTextContent().trim();
    }
    private String getTagValue(String tagName, Element dom, int index) {
        return dom.getElementsByTagName(tagName).item(index).getTextContent().trim();
    }




    private Element getDomElement(File file) {
        Element root = null;
        try {
            Document document = documentBuilder.parse(file);
            root = document.getDocumentElement();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return root;
    }

    private JsonArray getText(Element dom) {
        JsonArray content = new JsonArray();
        String text = dom.getElementsByTagName("Jurisdiction").item(0).getTextContent();

        int index = 0;
        for(String str : text.trim().split("\\n")) {
            str = str.trim();
            if(str.length() == 0)
                continue;

            JsonArray paragraph = getParagraph(str, index);
            index += paragraph.size();

            content.add(paragraph);
        }

        return content;
    }

    private JsonArray getParagraph(String paragraphValue, int index) {
        JsonArray paragraph = new JsonArray();
        paragraphValue = editEscapeChar(paragraphValue);
        // sentence split
        List<Sentence> extractSentenceList = senExtract.extractSentenceList(0, paragraphValue,"N");
        for(Sentence sentence : extractSentenceList ){

            List<String> editSentenceList = splitWithComma(sentence.getValue());
            editSentenceList = splitWithRegx(editSentenceList);

            for (String sentenceValue : editSentenceList) {

                JsonObject senObj = new JsonObject();

                senObj.addProperty("index", index++);
                senObj.addProperty("sentence", sentenceValue.trim());

                paragraph.add(senObj);
            }
        }

        return paragraph;
    }

    private List<String> splitWithComma(String sentence) {
        String[] splitSentences = sentence.split(",");
        List<String> editSentenceList = new ArrayList<>();
        StringBuilder tmpSentence = new StringBuilder();


        for (int i = 0; i < splitSentences.length - 1 ; i++) {
            splitSentences[i] += ",";
        }

        for (String splitSentence : splitSentences) {
            tmpSentence.append(splitSentence);

            if(splitSentence.length() < 8) {
                continue;
            }
            if(Check.isNumber(tmpSentence.toString().charAt(tmpSentence.length() - 2))) {
                continue;
            }
            if(tmpSentence.toString().contains("(") && !tmpSentence.toString().contains(")")) {
                continue;
            }

            editSentenceList.add(tmpSentence.toString());
            tmpSentence = new StringBuilder();
        }
        if (tmpSentence.length() > 0) {
            editSentenceList.add(tmpSentence.toString());
        }
        return editSentenceList;
    }


    /**
     * TODO 1. ~하고 있~ 에 대한 처리
     * ex)     {
     *             "index": 6,
     *             "sentence": "나. 피고가 아무런 권원 없이 계쟁건물을 점유하고"
     *           },
     *           {
     *             "index": 7,
     *             "sentence": "있는 이상 전소유자이고"
 *               }
     * @param sentenceList
     * @return
     */
    private List<String> splitWithRegx(List<String> sentenceList) {
        List<String> editSentence = new ArrayList<>();

        // 것이므로, 것으로서 등등
        // 것이라고, 것이라고는 등등 제외
        Pattern[] patterns = {
                Pattern.compile("\\s것([가-힣]{2,3})+[면서써로만나지]\\s"),
                Pattern.compile("\\s[가-힣]*[하이]고\\s"),
                Pattern.compile("\\s[가-힣]*으나\\s"),
                Pattern.compile("\\s[가-힣]*면\\s")};

        // 구분점 찾기
        for (String sentence : sentenceList) {
            TreeSet<Integer> splitPointSet = new TreeSet<>();

            for (Pattern pattern : patterns) {
                Matcher matcher = pattern.matcher(sentence);

                // 문장 구분 최소 길이
                while (matcher.find()) {
                    splitStrSet.add(matcher.group().trim());

                    if(sentence.substring(matcher.end()).startsWith("있")) {
                        // 점유하고 있는는
                       continue;
                    }

                    if(sentence.length() - matcher.end() > 8) {
                        splitPointSet.add(matcher.end());
                    }
                }
            }

            List<Integer> removeItems = new ArrayList<>();

            int tmpSplitPoint = 0;
            for (int splitPoint : splitPointSet) {
                if (splitPoint - 8 <= tmpSplitPoint) {
                    if(tmpSplitPoint == 0) {
                        removeItems.add(splitPoint);
                    } else  {
                        removeItems.add(tmpSplitPoint);
                    }
                }
                tmpSplitPoint = splitPoint;
            }

            for (int s : removeItems) {
                splitPointSet.remove(s);
            }

            // 문장구분
            int startIndex = 0;
            for (int splitPoint : splitPointSet) {
                editSentence.add(sentence.substring(startIndex, splitPoint).trim());
                startIndex = splitPoint;
            }
            editSentence.add(sentence.substring(startIndex).trim());

        }

        return editSentence;

    }



    private String editEscapeChar(String value) {
        value = value.replace("\\r","\n")
                .replace("\\n","\n")
                .replace("\\t","\t")
                .replace("　"," ")
                .replace("`", "'")
                .replace("‘", "'")
                .replace("’", "'")
                .replace("“", "\"")
                .replace("”", "\"");

        return value;
    }
}
