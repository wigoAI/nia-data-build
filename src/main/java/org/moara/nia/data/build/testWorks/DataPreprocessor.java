package org.moara.nia.data.build.testWorks;

import java.io.File;

public interface DataPreprocessor {

    void make(File file, String outputPath);
    void makeByPath(String path);
}
