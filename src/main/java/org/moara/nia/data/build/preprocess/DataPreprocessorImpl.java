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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.seomse.poi.excel.ExcelGet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.moara.ara.datamining.textmining.dictionary.sentence.SentenceDictionary;
import org.moara.ara.datamining.textmining.dictionary.sentence.extract.SenExtract;
import org.moara.ara.datamining.textmining.document.Document;
import org.moara.ara.datamining.textmining.document.sentence.Sentence;
import org.moara.common.code.LangCode;

import org.moara.common.data.file.FileUtil;
import org.moara.common.util.ExceptionUtil;
import org.moara.nia.data.build.mecab.MecabWordClassHighlight;

import org.moara.nia.data.build.preprocess.exception.OverlapDataException;
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
    private static final Logger logger = LoggerFactory.getLogger(DataPreprocessorImpl.class);
    private final ExcelGet excelGet = new ExcelGet();
    private final SenExtract senExtract = SentenceDictionary.getInstance().getSenExtract(LangCode.KO, Document.NEWS);
    private XSSFRow row;

//    private PersonalDataFinderImpl phFinder = new PersonalDataFinderImpl("ph");

    private final String [] outArray= {
            "M"
//                , "S"
//                , "E"
//                , "V"
//                , "J"
//                , "X"
//                , "E"
    };


    @Override
    public void makeByPath(String path) {
        List<File> fileList = FileUtil.getFileList(path, ".xlsx");
        int count = 0;

        for(File file : fileList) {
            make(file, path);
            count++;
            logger.debug("end length: " + count + "/" + fileList.size());
        }

    }

    @Override
    public void make(File file, String path) {
        String outputPath = path + "json";
        File outputDir = new File(outputPath);

        if(!outputDir.exists()) {
            outputDir.mkdir();
            System.out.println("create dir : " + outputPath);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        logger.debug("start file name: " +file.getName());

        JsonObject jsonObject = initJsonObject(file);
        addDocumentArray(file, jsonObject);

        FileUtil.fileOutput(gson.toJson(jsonObject), outputPath + "\\" + getFileNameWithoutFormat(file) + ".json",false);
        logger.debug("end file name: " +file.getName());

    }

    private JsonObject initJsonObject(File file) {
        JsonObject jsonObject = new JsonObject() ;
        String delivery_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        jsonObject.addProperty("name", getFileNameWithoutFormat(file));
        jsonObject.addProperty("delivery_date", delivery_date);

        return jsonObject;
    }

    private void addDocumentArray(File file, JsonObject jsonObject) {
        JsonArray documents = new JsonArray();
        XSSFSheet sheet = getExcelSheet(file);
        int rowCount = excelGet.getRowCount(sheet);

        for(int rowIndex = 1; rowIndex < rowCount ; rowIndex++){

            JsonObject document = getDocument(sheet, rowIndex);
            if(document == null)
                continue;
            documents.add(document);

        }

        jsonObject.add("documents", documents);
    }

    private JsonObject getDocument(XSSFSheet sheet, int rowIndex) {
        row = sheet.getRow(rowIndex);

        XSSFCell cell = row.getCell(0);
        if(cell == null){
            logger.debug("rowIndex: " + rowIndex) ;
            return null;
        }

        JsonObject document = addDocumentInfo(cell);
        if(document == null) {
            return null;
        }


        String contents = getCellValue(9);
        if(contents == null){
            return null;
        }

        int documentId = document.get("id").getAsInt();

        // 문단 리스트
        List<String> paragraphList = getParagraphList(contents);
//        System.out.println("Preprocessing... : " + documentId);
        try {
            JsonArray text = getText(paragraphList, documentId);
            document.add("text", text);
        } catch (OverlapDataException e) {
            System.out.println("drop data in id : " + documentId);
            System.out.println(e.toString());
        } catch (Exception e) {
            System.out.println("Some date is wrong : " + documentId + " so drop the data");
            System.out.println(e.toString());
        }



        return document;
    }

    private XSSFSheet getExcelSheet(File file) {
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

    private JsonObject addDocumentInfo(XSSFCell cell) {
        JsonObject document = new JsonObject();
        String id = null ;
        String sizeType = getCellValue(6);

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

        switch (sizeType) {
            case "대":
                sizeType = "large";
                break;
            case "중":
                sizeType = "medium";
                break;
            case "소":
                sizeType = "small";
                break;
            default:
                logger.error("size type error: " + sizeType);
                return null;
        }

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

    private List<String> getParagraphList(String contents) {
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

    private JsonArray getText(List<String> paragraphList, int documentId) throws OverlapDataException {
        JsonArray text = new JsonArray();
        String overlapCheck = "";
        int index = 0;

        for(String paragraphValue  : paragraphList){
            paragraphValue = paragraphValue.trim();

            if(paragraphValue.length() == 0)
                continue;
            if(overlapCheck.equals(paragraphValue)) {
                throw new OverlapDataException("data overlap : [" + paragraphValue + "]");
            }
            JsonArray paragraph = getParagraph(index, paragraphValue, documentId);

            text.add(paragraph);
            index += paragraph.size();
            overlapCheck = paragraphValue;
        }
        return text;
    }

    private JsonArray getParagraph(int index, String paragraphValue, int documentId) {
        JsonArray paragraph = new JsonArray();

        /* if want recreate personalDataFinder
        *
        * check this commit
        * https://github.com/wigoAI/nia-data-build/commit/a24d2ed9d0e8b17f97a6316f70883ae421dd8e48
        *
        * */

        // sentence split
        for(Sentence sentence : senExtract.extractSentenceList(0, paragraphValue,"N")){
            String sentenceValue = sentence.getValue();
            JsonObject senObj = new JsonObject();


            senObj.addProperty("index", index++);
            senObj.addProperty("sentence", sentenceValue);
            senObj.addProperty("highlight_indices" , MecabWordClassHighlight.indexValue(sentenceValue, outArray));
            paragraph.add(senObj);

        }

        return paragraph;
    }



    private String editEscapeChar(String value) {


        value = value.replace("\\r","\n")
                .replace("\\n","\n")
                .replace("\\t","\t")
                .replace("　"," ")
                .replace("‘", "'")
                .replace("’", "'")
                .replace("“", "\"")
                .replace("”", "\"");

        return value;
    }

    private String getCellValue(int cellNum){
        String value = excelGet.getCellValue(row, cellNum);

        if(value != null){
            value = value.trim();
        }

        return value;
    }
    private String getFileNameWithoutFormat(File file) {
        String fileName = file.getName();
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

}