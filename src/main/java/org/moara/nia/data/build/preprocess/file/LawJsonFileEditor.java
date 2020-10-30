package org.moara.nia.data.build.preprocess.file;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LawJsonFileEditor extends JsonFileEditor {
    private final HashSet<String> dropData = new HashSet<>();

    public LawJsonFileEditor() { }
    public LawJsonFileEditor(HashSet<String> dropData) {
        this.dropData.addAll(dropData);
    }

    @Override
    protected JsonArray getEditDocuments(JsonArray documents) {
        Pattern dropDataPattern = Pattern.compile(".*[^\\\\.\\]다\\)례예,]$");
        JsonArray editDocuments = new JsonArray();

        int dropCount = 0;
        // Document 복사 & text 접근
        for (int i = 0; i < documents.size() ; i++) {
            JsonObject document = documents.get(i).getAsJsonObject();
            JsonObject editDocument = copyDocumentInfo(document);
            boolean dropDocumentFlag = false;
            JsonArray text = document.get("text").getAsJsonArray();
            JsonArray editText = new JsonArray();

            // text 접근
            int index = 0;
            for(int j = 0 ; j < text.size() ; j++) {

                JsonArray paragraph = text.get(j).getAsJsonArray();
                JsonArray editParagraph = new JsonArray();

                // 문단 수정
                for (int k = 0 ; k < paragraph.size() ; k++) {
                    JsonObject sentence = paragraph.get(k).getAsJsonObject();
                    JsonObject editSentence;
                    String targetSentence = sentence.get("sentence").getAsString();
                    Matcher dropDataMatcher = dropDataPattern.matcher(targetSentence);

                    if((j != text.size() - 1 && k != paragraph.size() && dropDataMatcher.find())) {
                        System.out.println(document.get("id").getAsString() + " Drop data : " + targetSentence);
                        dropDocumentFlag = true;
                        break;
                    }

                    sentence.addProperty("sentence", targetSentence);
                    editSentence = copySentence(index++, sentence);
                    editParagraph.add(editSentence);
                }

                if(dropDocumentFlag) { break; }
                if(editParagraph.size() != 0) { editText.add(editParagraph); }

            }

            if(editText.size() == 0 || dropDocumentFlag) {
                System.out.println("drop data : " + document.get("id").getAsString() );
                dropCount++;
                continue;
            }

            editDocument.add("text", editText);
            editDocuments.add(editDocument);

        }
        System.out.print("drop " + dropCount +" data in ");

        return editDocuments;
    }
}
