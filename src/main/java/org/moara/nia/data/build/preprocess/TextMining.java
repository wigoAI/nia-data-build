package org.moara.nia.data.build.preprocess;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.seomse.commons.config.Config;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.moara.yido.textmining.*;
import org.moara.yido.tokenizer.word.WordToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class TextMining extends DataPreprocessorImpl {
    private static final Logger logger = LoggerFactory.getLogger(TextMining.class);
    private final String[] outArray = {"M"};
    private final String documentType;

    public TextMining() {
        this.documentType = "news_paragraph";
    }

    public TextMining(String documentType) {
        this.documentType = documentType;
    }


    @Override
    public JsonObject getDocument(XSSFSheet sheet, int rowIndex) {
        row = sheet.getRow(rowIndex);

        XSSFCell cell = row.getCell(0);

        if (cell == null) {
            logger.debug("rowIndex: " + rowIndex);
            return null;
        }

        JsonObject documentJsonObject = getDocumentInfo(cell);
        if(documentJsonObject == null) {
            logger.debug("document null ");
            return null;
        }


        String contents = getCellValue(9);
        if(contents == null){
            logger.debug("contents null");
            return null;
        }


        JsonArray text = getText(contents);
        if(text == null){
            logger.debug("text null");
            return null;
        }

        documentJsonObject.add("text", text);

        return documentJsonObject;
    }

    private JsonArray getText(String contents) {
        JsonArray text = new JsonArray();
        Document document = new Document();

        document.setType(documentType);
        document.setContents(contents);

        DocumentMiningParagraph documentMining = (DocumentMiningParagraph) DocumentMiningFactory.newDocumentMining(document);
        Paragraph[] paragraphs = documentMining.miningParagraph();

        int index = 0;
        for (Paragraph paragraph : paragraphs) {

            JsonArray paragraphJson = getParagraphJson(paragraph);

            if(paragraphJson.size() == 0) { continue; }

            for (int i = 0; i < paragraphJson.size(); i++) {
                JsonObject sentenceObject = paragraphJson.get(i).getAsJsonObject();
                sentenceObject.addProperty("index", index++);
            }

            text.add(paragraphJson);

        }
        return text;
    }

    private JsonArray getParagraphJson(Paragraph paragraph) {
        JsonArray paragraphJson = new JsonArray();

        for (Sentence sentence : paragraph.getSentences()) {
            JsonObject sentenceJson = new JsonObject();

            WordToken[] wordTokens = sentence.getTokens();

            StringBuilder indexBuilder = new StringBuilder();
            outer:
            for (WordToken wordToken : wordTokens) {
                String partOfSpeech = wordToken.getPartOfSpeech();

                for (String out : outArray) {
                    if (partOfSpeech.startsWith(out)) {
                        indexBuilder.append(";").append(wordToken.getBegin()).append(",").append(wordToken.getEnd());
                        continue outer;
                    }
                }
            }

            String highlightIndices = "";
            if (indexBuilder.length() > 0) {
                highlightIndices = indexBuilder.substring(1);
            }



            sentenceJson.addProperty("index", 0);
            sentenceJson.addProperty("sentence", sentence.getContents());
            sentenceJson.addProperty("highlight_indices", highlightIndices);
            paragraphJson.add(sentenceJson);

        }

        return paragraphJson;
    }

    public static void main(String[] args) {
        Config.setConfig("text.mining.paragraph.splitter.default", "paragraph");

        TextMining textMining = new TextMining();
        File file = new File("D:\\moara\\data\\allData\\test_text_mining\\test.xlsx");

        textMining.make(file);

    }

}
