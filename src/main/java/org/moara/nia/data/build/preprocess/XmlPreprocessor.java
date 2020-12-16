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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.seomse.commons.data.BeginEnd;
import com.seomse.commons.utils.FileUtil;
import com.seomse.commons.utils.string.Check;
import org.moara.nia.data.build.preprocess.fileUtils.json.JsonFileUtil;
import org.moara.splitter.Splitter;
import org.moara.splitter.SplitterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * XML 형태의 데이터 전처리기
 *
 * @author wjrmffldrhrl
 */
public class XmlPreprocessor extends DataPreprocessorImpl {
    private static final Logger logger = LoggerFactory.getLogger(XmlPreprocessor.class);
    private HashSet<String> splitStrSet = new HashSet<>();
    private DocumentBuilder documentBuilder;
    private final String outputPath;
    private Splitter splitter = SplitterManager.getInstance().getSplitter("news");

    /**
     * Constructor
     */
    public XmlPreprocessor(String outputPath) {
        this.jsonFileUtil = new JsonFileUtil();
        this.outputPath = outputPath;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 문장 구분이 적용된 단어 Set 을 얻는다.
     * @return  HashSet
     */
    public HashSet<String> getSplitStrSet() { return splitStrSet; }

    @Override
    public void makeByPath(String path) {
        List<File> fileList = FileUtil.getFileList(path, ".xml");
        String[] splitPath = path.split("\\\\");
        String jsonFileName = splitPath[splitPath.length - 1];


        jsonFileName += "_" + fileList.size() + "건_";


        JsonObject jsonObject = initJsonObject(jsonFileName);
        jsonObject.add("documents", getDocuments(fileList));

        jsonFileUtil.createJsonFile(outputPath, jsonFileName, jsonObject);

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

    private JsonObject getDocument(File file) {

        Element dom = getDomElement(file);
        JsonArray text = getText(dom);
        JsonObject document = initJsonObject(dom);

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
            if(str.length() == 0) { continue; }

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
        BeginEnd[] extractSentenceList = splitter.split(paragraphValue);
        for(BeginEnd beginEnd : extractSentenceList ){

            List<String> editSentenceList = splitWithComma(paragraphValue.substring(beginEnd.getBegin(), beginEnd.getEnd()));
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
     * 실제 문장 구분이 동작하기 전 간단한 규칙으로 미리 구분을 수행한다. Preprocessing
     * @param sentenceList 문장 리스트
     * @return 전처리가 된 문장 리스트
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
            TreeSet<Integer> splitPointSet = getSplitPoint(patterns, sentence);

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

    private TreeSet<Integer> getSplitPoint(Pattern[] patterns, String sentence ) {
        TreeSet<Integer> splitPointSet = new TreeSet<>();
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(sentence);

            // 문장 구분 최소 길이
            while (matcher.find()) {

                // ex) 점유하고 있는
                if(sentence.substring(matcher.end()).startsWith("있")) { continue; }
                if(sentence.length() - matcher.end() > 8) { splitPointSet.add(matcher.end()); }
                splitStrSet.add(matcher.group().trim());
            }
        }

        removeInvalidSplitPoint(splitPointSet);

        return splitPointSet;
    }

    private void removeInvalidSplitPoint(TreeSet<Integer> splitPointSet) {
        List<Integer> removeItems = new ArrayList<>();

        int tmpSplitPoint = 0;
        for (int splitPoint : splitPointSet) {
            if (splitPoint - 8 <= tmpSplitPoint) {
                if(tmpSplitPoint == 0) {
                    removeItems.add(splitPoint);
                } else {
                    removeItems.add(tmpSplitPoint);
                }
            }
            tmpSplitPoint = splitPoint;
        }

        for (int s : removeItems) { splitPointSet.remove(s); }
    }


    public static void main(String[] args) {
        String dirPath = "D:\\moara\\data\\law\\Data\\2019\\";
        String outputPath = "D:\\moara\\data\\law\\test";

        XmlPreprocessor xmlPreprocessor = new XmlPreprocessor(outputPath);
        xmlPreprocessor.makeByPath(dirPath);


//        String dirPath = "D:\\moara\\data\\law\\data_1947-2020\\";
//        String outputPath = "D:\\moara\\data\\law\\test";
//        XmlPreprocessor xmlPreprocessor = new XmlPreprocessor(outputPath);
//
//        for(int i = 1947 ; i <= 2020 ; i++) {
//            String path = dirPath + i + "\\";
//
//            xmlPreprocessor.makeByPath(path);
//        }
    }
}
