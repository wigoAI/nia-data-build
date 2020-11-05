package org.moara.nia.data.build.preprocess.fileUtils.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class JsonFileCutter extends JsonFileEditor {
    public JsonFileCutter() { }

    @Override
    protected JsonArray getEditDocuments(JsonArray documents) {
        JsonArray editDocuments = new JsonArray();

        for (int i = 0; i < documents.size() ; i++) {
            JsonObject document = new JsonObject();
            JsonObject editDocument;
            JsonArray text;
            try {
                document = documents.get(i).getAsJsonObject();
                editDocument = copyDocumentInfo(document);
                editDocument.addProperty("drop_char_count", 0);
                text = document.get("text").getAsJsonArray();
            } catch (NullPointerException e) {
                e.printStackTrace();
                System.out.println("no Data in document : " +  document.get("id").getAsString());
                continue;
            }

            JsonArray editText;
            if(document.get("char_count").getAsInt() <= 1500) {
                editText = text;
            } else {
                editText = cutText(text, editDocument);
            }

            editDocument.add("text", editText);
            editDocuments.add(editDocument);
        }

        return editDocuments;
    }

    private JsonArray cutText(JsonArray text, JsonObject editDocument) {
        JsonArray editText = new JsonArray();
        int charCount = 0;
        // text 접근
        int index = 0;
        for(int j = 0; j < text.size() ; j++) {
            JsonArray paragraph = text.get(j).getAsJsonArray();
            JsonArray editParagraph = new JsonArray();

            // 문단 수정
            for (int k = 0 ; k < paragraph.size() ; k++) {
                JsonObject sentence = paragraph.get(k).getAsJsonObject();
                String targetSentence = sentence.get("sentence").getAsString();
                charCount += targetSentence.length();
                editParagraph.add(copySentence(index++, sentence));

                if(charCount > 1500) { break; }
            }
            editText.add(editParagraph);
            if(charCount > 1500) { break; }

        }
        int beforeCharCount = editDocument.get("char_count").getAsInt();
        String documentId = editDocument.get("id").getAsString();

        System.out.println("Document " + documentId + " drop " + (beforeCharCount - charCount) + " char");
        editDocument.addProperty("drop_char_count",(beforeCharCount - charCount));
        editDocument.addProperty("char_count", charCount);

        return editText;
    }
}
