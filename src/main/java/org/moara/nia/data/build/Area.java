package org.moara.nia.data.build;

public class Area {

    private final int start;
    private final int end;

    public Area(int startIndex, int endIndex) {
        this.start = startIndex;
        this.end = endIndex;
    }

    public int getStart() { return this.start; }
    public int getEnd() { return this.end; }


}
