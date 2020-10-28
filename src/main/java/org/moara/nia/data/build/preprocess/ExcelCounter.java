package org.moara.nia.data.build.preprocess;

import com.seomse.poi.excel.ExcelGet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.moara.common.data.file.FileUtil;
import org.moara.common.util.ExceptionUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ExcelCounter {
    private ExcelGet excelGet = new ExcelGet();
    private XSSFRow row;

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

    public List<String> count(File file) {
        XSSFSheet sheet = getExcelSheet(file);
        List<String> idList = new ArrayList<>();
        int rowCount = excelGet.getRowCount(sheet);
        int count = 0;

        for(int rowIndex = 1; rowIndex < rowCount ; rowIndex++){
            row = sheet.getRow(rowIndex);
            XSSFCell cell = row.getCell(0);
            if(cell == null) {
                break;
            }

            String id = null;
            try{
                id = Long.toString((long)cell.getNumericCellValue());
            }catch(Exception e) {
                try {
                    id = getCellValue(0);
                }catch(Exception e1){ }
            }

            String contents = getCellValue(9);
            if(contents == null){
                continue;
            }

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
