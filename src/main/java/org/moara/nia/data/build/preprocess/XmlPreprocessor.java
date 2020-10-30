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
import org.moara.nia.data.build.Area;
import org.moara.nia.data.build.mecab.MecabWordClassHighlight;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * XML 형태의 데이터 전처리기
 *
 * @author 조승현
 */
public class XmlPreprocessor implements DataPreprocessor {
    private static final Logger logger = LoggerFactory.getLogger(XmlPreprocessor.class);
    private final SenExtract senExtract = SentenceDictionary.getInstance().getSenExtract(LangCode.KO, "NEWS");


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

    @Override
    public void makeByPath(String path) {
        List<File> fileList = FileUtil.getFileList(path, ".xml");
        String outputPath = "D:\\moara\\data\\law\\json4";
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

            String[] splitSentences = sentence.getValue().split(",");

            List<String> editSentenceList = new ArrayList<>();
            StringBuilder tmpSentence = new StringBuilder();


            for (int i = 0 ; i < splitSentences.length - 1 ; i++) {
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

            for (String sentenceValue : editSentenceList) {

                JsonObject senObj = new JsonObject();

                senObj.addProperty("index", index++);
                senObj.addProperty("sentence", sentenceValue.trim());

                paragraph.add(senObj);
            }
        }

        return paragraph;
    }

    private Area parseArea(String str) {
        int start = Integer.parseInt(str.split(",")[0]);
        int end = Integer.parseInt(str.split(",")[1]);

        return new Area(start, end);
    }

    private String blindArea(String text, List<Area> targetAreas) {
        if (targetAreas.size() == 0)
            return text;
        System.out.println("target data : " + text.replace("\n", " "));
        StringBuilder builder = new StringBuilder(text);
        for(Area area : targetAreas) {
            int index = area.getStart();

            while(index <= area.getEnd() - 1) {
                builder.setCharAt(index++, '*');
            }

        }

        return builder.toString();
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
