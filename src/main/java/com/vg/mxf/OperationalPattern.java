package com.vg.mxf;

public class OperationalPattern extends Key {

    public int getItemComplexity() {
        return item[13 - 8 - 1].get();
    }

    public char getPackageComplexity() {
        return (char) ('a' + (item[14 - 8 - 1].get() - 1));
    }

}
