package org.moara.nia.data.build.compare;

import com.google.gson.JsonObject;

public interface CompareData {
    void compare(JsonObject beforeJson, JsonObject afterJson);
}
