package com.company;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

public class Directory {
    public int size;
    public int pageSize;
    public int depth;
    int[] entries;
    public static List<Integer> freeSlots;
    public static int numberOfPages= 1;

    public Directory(int size, int depth, int[] entries, int pageSize)
    {
        this.size = size;
        this.depth = depth;
        this.entries = entries;
        this.pageSize = pageSize;
        freeSlots = new ArrayList<>();
    }

    public int getEntryIndex(int key)
    {
        int address = 0;
        int tempKey = key;
        for(int i=0;i<depth;i++)
        {
            address += (tempKey % 2)*Math.pow(2,i);
            tempKey = tempKey/2;
        }
        return address;
    }

    public int getPageNumber(int key)
    {
        return entries[getEntryIndex(key)];

    }

    public boolean split(int index, Page p, InputOutput IO)
    {
        List<Integer> indexesToRemove = new ArrayList<>();
        for(int i=0; i < entries.length;i++ )
        {
            if(index % (int) Math.pow(2,p.depth + 1) != i % (int) Math.pow(2,p.depth + 1))
            {
                if(entries[i]==entries[index])
                {
                    Page newPage = new Page(getNextPageNumber(),pageSize,new ArrayList<>(), p.depth + 1);
                    List<Record> precords = new ArrayList<>();
                    this.entries[i] = newPage.number;
                    for(int j =0; p.records.size() > 0;j++)
                    {
                        int key = p.records.get(0).key;
                        if(i % (int) Math.pow(2,p.depth + 1) == key % (int) Math.pow(2,p.depth + 1))
                            newPage.records.add(p.records.remove(0));
                        else
                            precords.add(p.records.remove(0));
                    }
                    p.records = precords;
                    IO.writePage(newPage, newPage.number);
                }
            }
        }
        p.depth++;
        IO.writePage(p,p.number);
        return p.records.size() <= this.pageSize;
    }


    public void expand()
    {
        this.depth ++;
        int[] array = new int[size*2];
        for(int i=0;i<size*2;i++)
        {
            if(i<entries.length)
            {
                array[i] = entries[i];
            }
            else
            {
                int pointer = entries[i%size];
                array[i] = pointer;
            }
        }
        this.size = this.size*2;
        this.entries = array;
    }

    public void collapse()
    {
        int[] array = new int[size/2];
        for(int i=size/2;i<size;i++)
        {
            if(this.entries[i] != this.entries[i-size/2])
            {
                return;
            }
            else
                array[i-size/2] = entries[i-size/2];
        }

        this.depth --;
        this.size = this.size/2;
        this.entries = array;
    }

    private void changePointers(Page p, int index, InputOutput IO)
    {
        int i;
        int newPageNumber = p.number;
        for(i=index+1;i<this.entries.length;i++)
        {
            if(this.entries[i] == p.number)
            {
                if(i % Math.pow(2,depth) != index % Math.pow(2, depth) )
                {
                    newPageNumber = getNextPageNumber();
                    Page newPage = new Page(newPageNumber, this.pageSize, new ArrayList<>(),p.depth);
                    entries[i] = newPageNumber;
                    IO.writePage(newPage,newPage.number);
                    break;
                }
            }
        }
    }

    private int getNextPageNumber()
    {
        if(freeSlots.size()==0)
        {
            return numberOfPages++;
        }
        return freeSlots.remove(0);
    }

    public void merge(Page page, Page sibling, InputOutput IO)
    {
        page.records.addAll(sibling.records);
        IO.writePage(page,page.number);
    }

    public int findSibling(int index, int depth, InputOutput IO)
    {
        for(int i=0;i< this.entries.length;i++)
        {
            if(i % (int) Math.pow(2,depth) == index % (int) Math.pow(2,depth) && i != index)
            {
                return i;
            }
        }
        return -1;
    }
}
