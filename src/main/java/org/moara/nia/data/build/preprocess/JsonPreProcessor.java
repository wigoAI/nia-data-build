
package org.moara.nia.data.build.preprocess;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.moara.ner.NamedEntity;
import org.moara.ner.NamedEntityRecognizer;
import org.moara.ner.person.PersonNamedEntityRecognizerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 *
 */
public class JsonPreProcessor extends DataPreprocessorImpl {
    private static final Logger logger = LoggerFactory.getLogger(DataPreprocessorImpl.class);
    NamedEntityRecognizer namedEntityRecognizer = PersonNamedEntityRecognizerManager.getInstance().getNamedEntityRecognizer("reporter");
    public JsonPreProcessor() {
        this.fileExtension = ".json";
        contentsSizeLimit = 99999;
        checkQNA = false;
    }


    @Override
    protected JsonArray getDocuments(File file) {
        JsonObject jsonData = jsonFileUtil.getJsonObjectByFile(file);
        JsonArray documents = new JsonArray();
        JsonArray data = jsonData.getAsJsonArray("data");


        int noEntityCount = 0;

        int threshold = 10000;
        for (int i = 0; i < data.size(); i++) {
            JsonObject news = data.get(i).getAsJsonObject();
            String contentsNo = news.get("CONTENTS_NO").getAsString();
            String contents = news.get("CONTENTS").getAsString();
            String originalUrl = "";
            try {
                originalUrl = news.get("ORIGINAL_URL").getAsString();

            } catch (UnsupportedOperationException e) {
                System.out.println("no url contents : " + contentsNo);

            }

            if (!contents.contains("기자")) {
                noEntityCount++;
                continue;
            }



            JsonObject document = new JsonObject();
            document.addProperty("CONTENTS_NO", contentsNo);
            document.addProperty("CONTENTS", contents);
            document.addProperty("ORIGINAL_URL", originalUrl);
            NamedEntity[] namedEntities = namedEntityRecognizer.recognize(contents);


            JsonArray entities = new JsonArray();

            for (NamedEntity namedEntity : namedEntities) {
                JsonObject entity = new JsonObject();
                entity.addProperty("NAME", namedEntity.getText());
                entity.addProperty("TYPE", namedEntity.getType());
                entity.addProperty("BEGIN", namedEntity.getBegin());
                entity.addProperty("END", namedEntity.getEnd());
                entities.add(entity);
            }



            document.add("ENTITIES", entities);

            documents.add(document);

            if (i > threshold) {
                System.out.println(i);
                threshold += 10000;
            }
        }

        System.out.println("no entity news count : " + noEntityCount);

        return documents;
    }


    public static void main(String[] args) {
        JsonPreProcessor jsonPreProcessor = new JsonPreProcessor();
        String path = "D:\\moara\\ner\\t_crawling_contents.json";
        jsonPreProcessor.makeByPath(path);
    }
}
