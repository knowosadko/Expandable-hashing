package com.company;

import java.util.ArrayList;
import java.util.List;

public class Page {
    int d;
    int depth;
    int number;
    int pageSize;
    List<Record> records;

    public Page(int number, int pageSize, List<Record> records, int depth)
    {
        this.number = number;
        this.pageSize = pageSize;
        this.records = records;
        this.depth = depth;
        this.d = depth;
    }
}
