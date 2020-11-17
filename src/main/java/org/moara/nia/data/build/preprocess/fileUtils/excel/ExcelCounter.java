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
package org.moara.nia.data.build.preprocess.fileUtils.excel;

import com.seomse.poi.excel.ExcelGet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.moara.common.data.file.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 *
 * excel 특정 자료 카운터
 *
 * @author wjrmffldrhrl
 */
public class ExcelCounter {
    private ExcelGet excelGet = new ExcelGet();
    private XSSFRow row;

    /**
     * 경로내에 있는 excel 파일에 대한 카운팅
     * @param path String
     * @return HashSet 카운트 된 파일 id 출력력
    */
    public HashSet<String> countByPath(String path) {
        List<File> fileList = FileUtil.getFileList(path, ".xlsx");
        HashSet<String> idHash = new HashSet<>();
        int totalCount = 0;

        for(File file : fileList) {
            List<String> idList = count(file);
            totalCount += idList.size();

            idHash.addAll(idList);
        }

        System.out.println("total : " + totalCount);

        return idHash;
    }

    /**
     * 엑셀 파일에 대한 카운팅
     * @param file File
     * @return List
     */
    public List<String> count(File file) {
        XSSFSheet sheet = getExcelSheet(file);
        List<String> idList = new ArrayList<>();
        int rowCount = excelGet.getRowCount(sheet);
        int count = 0;

        for(int rowIndex = 1; rowIndex < rowCount ; rowIndex++){
            row = sheet.getRow(rowIndex);
            XSSFCell cell = row.getCell(0);
            if(cell == null) { break; }

            String id = null;
            try{
                id = Long.toString((long)cell.getNumericCellValue());
            }catch(Exception e) {
                try {
                    id = getCellValue(0);
                }catch(Exception ignored){ }
            }

            String contents = getCellValue(9);
            if(contents == null){ continue; }

            if ((!contents.contains("기자 ") && !contents.contains("기자") &&  !contents.contains("리포터"))
            || (contents.contains(" 내가 ") || contents.contains(" 나는 ") || contents.contains(" 나의 "))) {
                idList.add(id);
                count++;
            }


        }

        System.out.println(count);

        return idList;
    }

    private XSSFSheet getExcelSheet(File file) {
        XSSFWorkbook work = null;
        try {
            work = new XSSFWorkbook(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        excelGet.setXSSFWorkbook(work);

        return work.getSheetAt(0);
    }

    protected String getCellValue(int cellNum){
        String value = excelGet.getCellValue(row, cellNum);

        if(value != null){
            value = value.trim();
        }

        return value;
    }
}
