package org.moara.nia.data.build.preprocess;

import org.moara.ara.datamining.textmining.dictionary.sentence.SentenceDictionary;
import org.moara.ara.datamining.textmining.dictionary.sentence.extract.SenExtract;
import org.moara.ara.datamining.textmining.document.Document;
import org.moara.common.code.LangCode;
import org.moara.common.data.file.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class XmlPreprocessor implements DataPreprocessor {
    private static final Logger logger = LoggerFactory.getLogger(DataPreprocessorImpl.class);
    private final SenExtract senExtract = SentenceDictionary.getInstance().getSenExtract(LangCode.KO, Document.NEWS);
    private final String [] outArray= {"M"};

    @Override
    public void makeByPath(String path) {
        List<File> fileList = FileUtil.getFileList(path, ".xml");
        int count = 0;

        for(File file : fileList) {
            make(file, path);
            count++;
            logger.debug("end length: " + count + "/" + fileList.size());
        }
    }

    @Override
    public void make(File file, String outputPath) {

    }
}
