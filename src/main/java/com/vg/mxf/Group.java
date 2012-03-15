package com.vg.mxf;

public enum Group {
    Unknown,
    UniversalSets,
    GlobalSets,
    LocalSets,
    VariableLengthPacks,

    /**
     * Defined-Length Packs are the most efficient (and least flexible) grouping
     * of data items that eliminates the use of both Keys and Local Tags and
     * removes the length for all individual items within the group. Thus
     * Defined-Length packs rely on a Standard or RP which defines both the
     * order of data items, the length of each data item within the pack and the
     * UL of each item in the pack.
     */
    DefinedLengthPacks,
    Reserved;

    public static Group groupFromInt(int i) {
        if (i < Group.values().length) {
            return Group.values()[i];
        } else {
            return Group.Reserved;
        }
    }

}
