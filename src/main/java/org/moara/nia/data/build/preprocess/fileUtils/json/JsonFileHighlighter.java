package org.moara.nia.data.build.preprocess.fileUtils.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.moara.nia.data.build.mecab.MecabWordClassHighlight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JsonFileHighlighter extends JsonFileEditor {
    private static final Logger logger = LoggerFactory.getLogger(JsonFileHighlighter.class);
    String[] outArray = {"M"};
    public JsonFileHighlighter() { }



    @Override
    protected JsonArray getEditText(JsonArray text){
        JsonArray editText = new JsonArray();

        try{
            // text 접근
            int index = 0;
            for(int j = 0; j < text.size() ; j++) {
                JsonArray paragraph = text.get(j).getAsJsonArray();
                JsonArray editParagraph = new JsonArray();

                // 문단 수정
                for (int k = 0 ; k < paragraph.size() ; k++) {
                    JsonObject sentence = paragraph.get(k).getAsJsonObject();
                    String targetSentence = sentence.get("sentence").getAsString();
                    String mecabResult = MecabWordClassHighlight.indexValue(targetSentence, outArray);
                    sentence.addProperty("highlight_indices", mecabResult);

                    editParagraph.add(copySentence(index++, sentence));
                }

                if(editParagraph.size() != 0) {
                    editText.add(editParagraph);
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            System.out.println("Error in highlighting");
            editText = new JsonArray();
        }


        return editText;
    }
}
