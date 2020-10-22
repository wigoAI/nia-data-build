package org.moara.nia.data.build.preprocess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.moara.common.data.file.FileUtil;
import org.moara.common.util.ExceptionUtil;
import org.moara.nia.data.build.preprocess.exception.LongDataException;
import org.moara.nia.data.build.preprocess.exception.OverlapDataException;
import org.moara.nia.data.build.preprocess.exception.QaDataException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TextPreprocessor extends DataPreprocessorImpl{

    @Override
    public void makeByPath(String path) {
        List<File> fileList = FileUtil.getFileList(path, ".txt");
        int count = 0;

        for(File file : fileList) {
            make(file, path);
            count++;
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

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();


        JsonObject jsonObject = initJsonObject(file);
        addDocumentArray(file, jsonObject);

        FileUtil.fileOutput(gson.toJson(jsonObject), outputPath + "\\" + getFileNameWithoutFormat(file) + ".json",false);


    }

    @Override
    protected void addDocumentArray(File file, JsonObject jsonObject) {
        JsonArray documents = new JsonArray();

//        int testCount = 0;
        int dropDataCount = 0;
        int normalDataCount = 0;
        int oldRandomIndex = 0;
        int countDecimal = 1000;

        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file.getPath()), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                JsonObject document = getDocument(line);

                if (document == null) {
                    dropDataCount++;
                    continue;
                }

                documents.add(document);
                normalDataCount++;
//            if(testCount++ > 200)
//                break;



            }
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.print("  Drop data : " + dropDataCount);
            System.out.println("  Normal data : " + (normalDataCount));
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            jsonObject.add("documents", documents);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private JsonObject getDocument(String line) {
        String [] columns = line.split("\t");
        JsonObject document = addDocumentInfo(columns);

        String contents = columns[9];
        if(contents == null || contents.length() < 2){
            return null;
        }
//        System.out.println(contents);
        List<String> paragraphList = getParagraphList(contents);

        String documentId = columns[0];
        try {
            JsonArray text = getText(paragraphList);
            document.add("text", text);
        } catch (OverlapDataException | QaDataException | LongDataException e) {
            System.out.println("drop data in id : " + documentId);
            System.out.println(e.toString());
            return null;
        } catch (Exception e) {
            System.out.println("this data something is wrong : " + documentId);
        }

        return document;
    }

    private JsonObject addDocumentInfo(String[] columns) {
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
        return document;
    }
}
