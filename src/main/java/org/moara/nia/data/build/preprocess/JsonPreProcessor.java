
package org.moara.nia.data.build.preprocess;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.moara.ner.NamedEntity;
import org.moara.ner.NamedEntityRecognizer;
import org.moara.ner.ReporterRecognizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class JsonPreProcessor extends DataPreprocessorImpl {
    private static final Logger logger = LoggerFactory.getLogger(DataPreprocessorImpl.class);
    NamedEntityRecognizer namedEntityRecognizer = new ReporterRecognizer();
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


            JsonObject document = new JsonObject();
            document.addProperty("CONTENTS_NO", contentsNo);
            document.addProperty("CONTENTS", contents);
            document.addProperty("ORIGINAL_URL", originalUrl);
            NamedEntity[] namedEntities = namedEntityRecognizer.recognize(contents);

            StringBuilder stringBuilder = new StringBuilder();
            for (NamedEntity namedEntity : namedEntities) {
                stringBuilder.append(", ").append(namedEntity.getValue());
            }
            String entityStr = "";
            if (stringBuilder.length() > 2) {
                entityStr = stringBuilder.substring(2);
            } else {
                noEntityCount++;
            }

            document.addProperty("ENTITY", entityStr);

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
