package org.moara.nia.data.build.evaluator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.moara.nia.data.build.preprocess.fileUtils.json.JsonFileUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SentenceExtractor extends JsonFileUtil {

    public void createSentenceFiles(File file) {
        JsonObject jsonObject = getJsonObjectByFile(file);

        JsonArray documents = jsonObject.getAsJsonArray("documents");

        for (int i = 2000; i < 2004; i++) {
            JsonObject document = documents.get(i).getAsJsonObject();
            List<String> sentences;
            try {
                sentences = getSentences(document);

            } catch (RuntimeException e) {
                continue;
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\git\\evaluation\\data\\last\\submit" + i + ".txt"))){
                for(String str : sentences)
                    bw.write(str + "\n");

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("error while write file");
                continue;
            }



        }
    }


    public List<String> getSentences(JsonObject document) {
        List<String> sentences = new ArrayList<>();
        JsonArray text = document.getAsJsonArray("text");
        if (text == null) {
            throw new RuntimeException("no text");
        }



        for (int i = 0; i < text.size(); i++) {
            JsonArray paragraph = text.get(i).getAsJsonArray();

            for (int j = 0; j < paragraph.size(); j++) {
                JsonObject sentenceObject = paragraph.get(j).getAsJsonObject();
                String sentence = sentenceObject.get("sentence").getAsString();
                sentences.add(sentence);
            }

        }


        return sentences;
    }

    public static void main(String[] args) {
        SentenceExtractor sentenceExtractor = new SentenceExtractor();

        sentenceExtractor.createSentenceFiles(new File("D:\\moara\\data\\allData\\NIA_1차_excel\\json\\test.json"));
    }

}
