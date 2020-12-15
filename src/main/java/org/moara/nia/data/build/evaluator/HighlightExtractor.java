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

public class HighlightExtractor extends JsonFileUtil {

    public void createHighlightFile(File file) {
        JsonObject jsonObject = getJsonObjectByFile(file);

        JsonArray documents = jsonObject.getAsJsonArray("documents");

        List<SentenceHighlight> sentenceHighlight = new ArrayList<>();

        for (int i = 0; i < documents.size(); i++) {
            JsonObject document = documents.get(i).getAsJsonObject();

            sentenceHighlight.addAll(SentenceHighlight(document));


            if (sentenceHighlight.size() > 1000) {
                break;
            }
        }




        try (BufferedWriter bw = new BufferedWriter(new FileWriter("D:\\moara\\data\\allData\\highlight\\submit.txt"))) {
            for (int i = 0; i < 1000; i++) {
                SentenceHighlight str = sentenceHighlight.get(i);

                bw.write(str + "\n");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public List<SentenceHighlight> SentenceHighlight(JsonObject document) {
        List<SentenceHighlight> sentenceHighlights = new ArrayList<>();

        JsonArray text = document.getAsJsonArray("text");
        if (text == null) {
            throw new RuntimeException("no text");
        }

        for (int i = 0; i < text.size(); i++) {
            JsonArray paragraph = text.get(i).getAsJsonArray();

            for (int j = 0; j < paragraph.size(); j++) {
                JsonObject sentenceObject = paragraph.get(j).getAsJsonObject();

                String sentence = sentenceObject.get("sentence").getAsString();
                String highlight = sentenceObject.get("highlight_indices").getAsString();

                if (highlight.length() == 0) {
                    continue;
                }

                List<Integer> highlightAreaIndex = new ArrayList<>();
                System.out.println(sentence);
                for (String startEnd : highlight.split(";")) {
                    int start = Integer.parseInt(startEnd.split(",")[0]);
                    int end = Integer.parseInt(startEnd.split(",")[1]);

                    for (int k = start; k < end + 1; k++) {
                        highlightAreaIndex.add(k);
                        System.out.print(k + " ");
                    }
                }
                System.out.println();

                sentenceHighlights.add(new SentenceHighlight(sentence, highlightAreaIndex));
                if (sentenceHighlights.size() == 100) {
                    return sentenceHighlights;
                }
            }

        }


        return sentenceHighlights;
    }

    private class SentenceHighlight {
        String sentence;
        List<Integer> highlightIndexes;

        public SentenceHighlight(String sentence, List<Integer> highlightIndexes) {
            this.sentence = sentence;
            this.highlightIndexes = highlightIndexes;
        }

        @Override
        public String toString() {
            return "[" + sentence + "]" + " : " + highlightIndexes;
        }
    }

    public static void main(String[] args) {
        HighlightExtractor highlightExtractor = new HighlightExtractor();

        highlightExtractor.createHighlightFile(new File("D:\\moara\\data\\allData\\highlight\\highlight.json"));


    }

}
