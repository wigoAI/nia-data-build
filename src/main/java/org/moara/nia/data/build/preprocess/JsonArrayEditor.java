package org.moara.nia.data.build.preprocess;

import com.google.gson.JsonArray;

@FunctionalInterface
public interface JsonArrayEditor {
    JsonArray edit(JsonArray jsonArray);
}
