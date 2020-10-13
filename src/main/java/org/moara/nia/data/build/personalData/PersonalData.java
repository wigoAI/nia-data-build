package org.moara.nia.data.build.personalData;

public class PersonalData {
    private final int start;
    private final int end;
    private final String value;
    private final String type;

    public PersonalData(int start, int end, String value, String type) {
        this.start = start;
        this.end = end;
        this.value = value;
        this.type = type;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }
}
