package org.moara.nia.data.build.preprocess;

import com.seomse.commons.config.Config;
import com.seomse.commons.utils.ExceptionUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.moara.yido.textmining.*;
import org.moara.yido.tokenizer.word.WordToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class TextMining extends DataPreprocessorImpl {
    private static final Logger logger = LoggerFactory.getLogger(TextMining.class);
    String[] outArray = {"M"};

    @Override
    public void make(File file) {
        String path = file.getParentFile().getPath();
        String outputPath = path + "json";
        logger.debug("start file name: " +file.getName());

        XSSFSheet sheet = getExcelSheet(file);
        int rowCount = excelGet.getRowCount(sheet);

        for(int rowIndex = 1; rowIndex < rowCount ; rowIndex++){
            row = sheet.getRow(rowIndex);
            XSSFCell cell = row.getCell(0);

            if(cell == null){
                logger.debug("rowIndex: " + rowIndex) ;
                break;
            }

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



            String title = getCellValue(8);
            String documentType = "news_paragraph";
//            String category = getCellValue(5);
//            String media_type = "online";
//            String media_sub_type = getCellValue(3);
//            String media_name = getCellValue(1);
//            String size = SizeTypeUtil.getSizeType(getCellValue(6));
//            String char_count = getCellValue(7);
//            String publish_date = getCellValue(4);
            String contents = getCellValue(9);
            Document document = new Document();

            document.setId(id);
            document.setTitle(title);
            document.setType(documentType);
            document.setContents(contents);

            DocumentMiningParagraph documentMining = (DocumentMiningParagraph) DocumentMiningFactory.newDocumentMining(document);

            Paragraph[] paragraphs = documentMining.miningParagraph();

            for (Paragraph paragraph : paragraphs) {
                System.out.println("Paragraph : " + paragraph.getContents());

                for (Sentence sentence : paragraph.getSentences()) {
                    System.out.println("Sentence : " + sentence.getContents());
                    WordToken[] wordTokens = sentence.getTokens();

                    StringBuilder indexBuilder = new StringBuilder();

                    outer:
                    for(WordToken wordToken: wordTokens){

                        String partOfSpeech = wordToken.getPartOfSpeech();

                        for(String out : outArray){
                            if(partOfSpeech.startsWith(out)){
                                indexBuilder.append(";").append(wordToken.getBegin()).append(",").append(wordToken.getEnd());
                                continue outer;
                            }
                        }
                    }

                    if (indexBuilder.length() > 0) {
                        System.out.println("Highlight : " + indexBuilder.substring(1));
                    } else {
                        System.out.println("Highlight :");
                    }

                }
                System.out.println();
            }
        }

    }

    public static void main(String[] args) {
        Config.setConfig("text.mining.paragraph.splitter.default", "paragraph");

        TextMining textMining = new TextMining();
        File file = new File("D:\\moara\\data\\allData\\test_text_mining\\test.xlsx");

        textMining.make(file);

        System.out.println("2018.12.30\\r\\n소지섭이 올해 MBC TV에서 주연한 '내 뒤에 테리우스'는 시청률로만 보면 '숨바꼭질' 등 주말극이 더 높았지만 화제성은 단연 최고였다.".substring(72, 73));
    }

}
