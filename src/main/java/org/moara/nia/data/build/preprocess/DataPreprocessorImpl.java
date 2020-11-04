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

import java.io.File;

import com.github.wjrmffldrhrl.Area;
import com.google.gson.*;

import com.seomse.poi.excel.ExcelGet;
import org.apache.poi.xssf.usermodel.*;

import org.moara.ara.datamining.textmining.dictionary.sentence.SentenceDictionary;
import org.moara.ara.datamining.textmining.dictionary.sentence.extract.SenExtract;
import org.moara.ara.datamining.textmining.document.Document;
import org.moara.ara.datamining.textmining.document.sentence.Sentence;
import org.moara.common.code.LangCode;

import org.moara.common.data.file.FileUtil;
import org.moara.common.util.ExceptionUtil;
import org.moara.nia.data.build.mecab.MecabWordClassHighlight;

import org.moara.nia.data.build.preprocess.exception.*;
import org.moara.nia.data.build.preprocess.exceptionData.ExceptionDataFinder;
import org.moara.nia.data.build.preprocess.exceptionData.ExceptionFinderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 데이터 전처리기
 * DataPreprocessor의 구현체로 실제 데이터 전처리 과정을 수행
 *
 * @author 조승현
 */
public class DataPreprocessorImpl implements DataPreprocessor {
    private final SenExtract senExtract = SentenceDictionary.getInstance().getSenExtract(LangCode.KO, Document.NEWS);
    private static final Logger logger = LoggerFactory.getLogger(DataPreprocessorImpl.class);
    private final ExcelGet excelGet = new ExcelGet();
//    private final String [] outArray= {"M"};
    private XSSFRow row;
    protected String fileExtension;


    /**
     * Constructor
     * 해당 전처리기의 타겟 파일 확장자명을 지정한다.
     */
    public DataPreprocessorImpl() { this.fileExtension = ".xlsx"; }

    @Override
    public void makeByPath(String path) {
        List<File> fileList = FileUtil.getFileList(path, this.fileExtension);
        int count = 0;

        for(File file : fileList) {
            make(file, path);
            count++;
            logger.debug("end length: " + count + "/" + fileList.size());
        }
    }

    public void make(File file, String path) {
        String outputPath = path + "json";
        File outputDir = new File(outputPath);

        if(!outputDir.exists()) {
            outputDir.mkdir();
            System.out.println("create dir : " + outputPath);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        logger.debug("start file name: " +file.getName());

        JsonObject jsonObject = initJsonObject(getFileNameWithoutFormat(file));
        JsonArray documents = getDocuments(file);
        jsonObject.add("documents", documents);

        FileUtil.fileOutput(gson.toJson(jsonObject), outputPath + "\\" + getFileNameWithoutFormat(file) + ".json",false);
        logger.debug("end file name: " +file.getName());

    }

    protected JsonObject initJsonObject(String name) {
        JsonObject jsonObject = new JsonObject();
        String delivery_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        jsonObject.addProperty("name", name);
        jsonObject.addProperty("delivery_date", delivery_date);

        return jsonObject;
    }

    protected JsonArray getDocuments(File file) {
        JsonArray documents = new JsonArray();
        XSSFSheet sheet = getExcelSheet(file);

        int rowCount = excelGet.getRowCount(sheet);
        int dropDataCount = 0;
        int normalDataCount = 0;
        int countDecimal = 1000;
        for(int rowIndex = 1; rowIndex < rowCount ; rowIndex++){
            JsonObject document = getDocument(sheet, rowIndex);

            if(document == null) {
                dropDataCount++;
                continue;
            }

            documents.add(document);
            normalDataCount++;

            if(normalDataCount > countDecimal) {
                System.out.println(normalDataCount + " / " + rowCount);
                countDecimal += 1000;
            }
        }

        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.print("  Drop data : " + dropDataCount );
        System.out.println("  Normal data : " + (normalDataCount));
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

        return documents;
    }

    private JsonObject getDocument(XSSFSheet sheet, int rowIndex) {
        row = sheet.getRow(rowIndex);

        XSSFCell cell = row.getCell(0);
        if(cell == null){
            logger.debug("rowIndex: " + rowIndex) ;
            return null;
        }

        JsonObject document = getDocumentInfo(cell);
        if(document == null) {
            return null;
        }

        String contents = getCellValue(9);
        if(contents == null){
            return null;
        }

        String documentId = document.get("id").getAsString();

        // 문단 리스트
        List<String> paragraphList = getParagraphList(contents);
        addTextToDocument(documentId, document, paragraphList);

        return document;
    }

    protected void addTextToDocument(String documentId, JsonObject document, List<String> paragraphList) {
        try {
            JsonArray text = getText(paragraphList);
            document.add("text", text);
        } catch (OverlapDataException | QaDataException | LongDataException e) {
            System.out.println("drop data in id : " + documentId);
            System.out.println(e.toString());

        } catch (Exception e) {
            System.out.println("this data something is wrong : " + documentId);
        }
    }

    protected XSSFSheet getExcelSheet(File file) {
        XSSFWorkbook work = null;

        try {
            work = new XSSFWorkbook(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        excelGet.setXSSFWorkbook(work);
        logger.debug("excel load complete");

        return work.getSheetAt(0);
    }

    private JsonObject getDocumentInfo(XSSFCell cell) {
        String sizeType = getCellValue(6);

        String id = null ;
        try{
            id = Long.toString((long)cell.getNumericCellValue());
        }catch(Exception e) {
            try {
                id = getCellValue(0);
            }catch(Exception e1){
                logger.error(ExceptionUtil.getStackTrace(e));
            }
        }

        if(!getCellValue(2).trim().equals("온라인")){ return null; }

        sizeType = SizeTypeUtil.getSizeType(sizeType);

//        if(!sizeType.equals("small")) {
//            return null;
//        }

        JsonObject document = new JsonObject();
        document.addProperty("id", id);
        document.addProperty("category", getCellValue(5));
        document.addProperty("media_type", "online");
        document.addProperty("media_sub_type", getCellValue(3));
        document.addProperty("media_name", getCellValue(1));
        document.addProperty("size", sizeType);
        document.addProperty("char_count", getCellValue(7));
        document.addProperty("publish_date", getCellValue(4));
        document.addProperty("title", getCellValue(8));

        return document;
    }

    protected List<String> getParagraphList(String contents) {
        contents = contents.replace("\\\\", "\\");

        List<String> paragraphList = new ArrayList<>();
        int lsatIndex = 0;
        while(true){
            int index = contents.indexOf("\\r\\n\\r\\n", lsatIndex);
            if(index == -1){ break; }

            String value = contents.substring(lsatIndex, index).trim();
            lsatIndex = index + 8;
            if("".equals(value) || ".".equals(value)){ continue; }

            value = editEscapeChar(value);
            paragraphList.add(value);
        }

        if(lsatIndex != contents.length()){
            String value = contents.substring(lsatIndex).trim();

            if(!"".equals(value) && !".".equals(value)){
                value = editEscapeChar(value);
                paragraphList.add(value);
            }
        }

        return paragraphList;
    }

    protected JsonArray getText(List<String> paragraphList) throws OverlapDataException, QaDataException, LongDataException{
        JsonArray text = new JsonArray();
        String overlapCheck = "";

        int index = 0;
        for(int i = 0 ; i < paragraphList.size() ; i++){
            String paragraphValue = paragraphList.get(i).trim();

            if(paragraphValue.length() == 0) { continue; }

            if(overlapCheck.equals(paragraphValue)) {
                throw new OverlapDataException("data overlap : [" + paragraphValue + "]");
            }

            boolean exceptionDataCheck = false;
            if(i < 2 || i > paragraphList.size() - 3) { exceptionDataCheck = true; }

            JsonArray paragraph = getParagraph(index, paragraphValue, exceptionDataCheck);

            if(paragraph.size() == 0) { continue; }

            text.add(paragraph);
            index += paragraph.size();
            overlapCheck = paragraphValue;


        }
        return text;
    }

    private JsonArray getParagraph(int index, String paragraphValue, boolean exceptionDataCheck) throws QaDataException, LongDataException{
        JsonArray paragraph = new JsonArray();

        // sentence split
        List<Sentence> extractSentenceList = senExtract.extractSentenceList(0, paragraphValue,"N");
        for(Sentence sentence : extractSentenceList ){
            String sentenceValue = sentence.getValue();

            if(exceptionDataCheck) { sentenceValue = deleteExceptionData(sentenceValue, 50); }
            if(sentenceValue.length() == 0){
                continue;
            } else if(sentenceValue.contains("Q : ") || sentenceValue.contains("A : ")){
                throw new QaDataException("this data is Q&A type : " + sentenceValue);
            }else if(sentenceValue.length() > 300) {
                throw new LongDataException("this data is too long : " + sentenceValue);
            }

            JsonObject senObj = getSentenceObject(index++, sentenceValue);
            paragraph.add(senObj);

        }

        return paragraph;
    }

    private JsonObject getSentenceObject(int index, String sentenceValue) {
        JsonObject sentenceObject = new JsonObject();
        sentenceObject.addProperty("index", index);
        sentenceObject.addProperty("sentence", sentenceValue.trim());
        sentenceObject.addProperty("highlight_indices" , "");
//        sentenceObject.addProperty("highlight_indices" , MecabWordClassHighlight.indexValue(sentenceValue, outArray));
        return sentenceObject;
    }

    private String deleteExceptionData(String text, int limit) {

        // 정규 표현식에 적용하기 위한 공백 추가
        // 처리 후 제거된다.
        text += " ";
        ExceptionDataFinder reporterFinder = ExceptionFinderFactory.getExceptionFinder("reporter");
        Area targetArea = reporterFinder.find(text);

        if(targetArea.getEnd() > 0 && targetArea.getEnd() < limit) {
            text = text.substring(targetArea.getEnd());
        }

        return text.trim();
    }


    protected String editEscapeChar(String value) {
        value = value.replace("\\r\\n", " ")
                .replace("  ", " ")
                .replace("[", "")
                .replace("]", "")
                .replace("\\r","\n")
                .replace("\\n","\n")
                .replace("\\t","\t")
                .replace("　"," ")
                .replace("`", "'")
                .replace("‘", "'")
                .replace("’", "'")
                .replace("“", "\"")
                .replace("”", "\"")
                .replace("＂", "\"");

        return value;
    }

    private String getCellValue(int cellNum){
        String value = excelGet.getCellValue(row, cellNum);

        if(value != null){ value = value.trim(); }

        return value;
    }

    private String getFileNameWithoutFormat(File file) {
        String fileName = file.getName();
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

}
