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
    private static final Logger logger = LoggerFactory.getLogger(DataPreprocessorImpl.class);
    private final SenExtract senExtract = SentenceDictionary.getInstance().getSenExtract(LangCode.KO, "NEWS");
    private final String [] outArray= {"PERSON_NAME", "M"};

    private final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder documentBuilder;

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
        String outputPath = path + "json";
        String[] splitPath = path.split("\\\\");
        String jsonFileName = splitPath[splitPath.length - 1];

        File outputDir = new File(outputPath);
        if(!outputDir.exists()) {
            outputDir.mkdir();
        }

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
//        for (int i = 0 ; i < childNodes.getLength() ; i++ ) {
//            Node node = childNodes.item(i);
//
//            if (node.getNodeType() == Node.TEXT_NODE) { continue; }
//            String value = node.getTextContent();
//
////            Element nodeElement = (Element)node;
////            System.out.println(node.getNodeName() + " : " + nodeElement.getAttribute("Desc") + " : " + data.trim());
//
//            if(node.getNodeName().equals("Content") ){
//                JsonArray content = getContent(value);
//                document.add("Content", content);
//
//            } else {
//
//                document.addProperty(node.getNodeName(), value.trim());
//            }
//        }

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
        document.addProperty("decision", getTagValue("Decision", dom));
        document.addProperty("jurisdiction", getTagValue("Jurisdiction", dom));
        document.addProperty("reference_1", getTagValue("Reference1", dom));
        document.addProperty("reference_2", getTagValue("Reference2", dom));

        int documentSize = dom.getElementsByTagName("Content").item(0).getTextContent().length();
        document.addProperty("char_count", documentSize);

        if(documentSize < 4000)
            document.addProperty("size","small");
        else if( documentSize < 8000 )
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
        String text = dom.getElementsByTagName("Content").item(0).getTextContent();

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
            String sentenceValue = sentence.getValue();
            JsonObject senObj = new JsonObject();

            senObj.addProperty("index", index++);
            String[] mecabResult = MecabWordClassHighlight.indexValue(sentenceValue, outArray).split(";");
            StringBuilder highlightIndexBuilder = new StringBuilder();
            List<Area> nameAreas = new ArrayList<>();

            for(String str : mecabResult) {
                if(str.startsWith("N")) {
                    nameAreas.add(parseArea(str.substring(1)));
                } else {
                    highlightIndexBuilder.append(";").append(str);
                }
            }
            sentenceValue = blindArea(sentenceValue, nameAreas);
            senObj.addProperty("sentence", sentenceValue.trim());

            String highlightIndex = "";
            if(highlightIndexBuilder.length() > 0)
                highlightIndex = highlightIndexBuilder.substring(1);
            senObj.addProperty("highlight_indices" , highlightIndex);
            paragraph.add(senObj);

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
