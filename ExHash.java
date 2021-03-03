package com.company;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class ExHash {
    InputOutput IO;
    int pageSize;
    int instructionOffset;
    String filename;
    int inserts ;
    int numberIn;
    int deletes ;
    int numberDel ;
    int updates ;
    int numberUP;
    public ExHash(int pageSize)
    {
        this.IO = new InputOutput("main.txt","directory.txt",pageSize);
        this.pageSize = pageSize;
        initFiles();
        inserts = 0;
        numberIn = 0;
        deletes = 0;
        numberDel = 0;
        updates = 0;
        numberUP = 0;
    }

    public void nextOperation(String filename) //GOOD
    {
        try {
            RandomAccessFile br = new RandomAccessFile(filename, "r") ;
            System.out.println(IO.readAll());
            for(int i=0;i<this.instructionOffset;i++)
                br.readLine();
            String s = br.readLine();
            String[] data = s.split(":");
            // get opeartion type
            String operation = data[0].replace(" ","");
            Record r = new Record(s);
            System.out.println("Operation: "+ operation + " Key: "+r.key );
            if(operation.charAt(0) == 'u')
            {
                update(r);
            }
            else if(operation.charAt(0) == 'd')
            {
                smartDelete(r.key);
            }
            else if(operation.charAt(0) == 'i')
            {
                insert(r);
            }
            instructionOffset++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void insert(Record r){
        InputOutput.readDirCounter=0;
        InputOutput.writePageCounter=0;
        InputOutput.readDirCounter=0;
        InputOutput.writeDirCounter=0;
        Directory dir = IO.readDirectory();
        int index = dir.getEntryIndex(r.key);
        int pageAddress = dir.entries[index];
        Page p = IO.readPage(pageAddress);
        p.records.add(r);
        if(p.records.size() > pageSize)
        {
            if(p.depth < dir.depth)
            {
                if(dir.split(index, p, IO))
                {
                    IO.writeDirectory(dir);
                    numberIn++;
                    inserts += InputOutput.readDirCounter + InputOutput.writePageCounter + InputOutput.readDirCounter;
                    return;
                }
            }
            dir.expand();
            while(!dir.split(index, p, IO))
            {
                dir.expand();
            }
            IO.writeDirectory(dir);
        }
        else
        {
            IO.writePage(p,p.number);
        }
        numberIn++;
        inserts += InputOutput.readDirCounter + InputOutput.writePageCounter + InputOutput.readDirCounter + InputOutput.writeDirCounter;
    }

    Record search(int key)
    {
        Directory dir = IO.readDirectory();
        Page p = findRecord(key, dir);
        for(int i =0;i < p.records.size();i++)
        {
            if(p.records.get(i).key == key){
                System.out.println("Found records: \n ");
                return p.records.get(i);
            }
        }
        System.out.println("NOT FOUND");
        return null;
    }

    void simpleDelete(Record r)
    {
        Directory dir = IO.readDirectory();
        Page p = findRecord(r.key, dir);
        for(int i =0;i < p.records.size();i++)
        {
            if(p.records.get(i).key == r.key){
                p.records.remove(i);
                if(p.records.size() == 0)
                {
                    Directory.freeSlots.add(p.number);
                    int index = dir.getPageNumber(r.key);
                    dir.entries[index] = index - (int)Math.pow(2,dir.depth-1);
                    Page previousPage = IO.readPage(index - (int)Math.pow(2,dir.depth-1));
                    previousPage.depth --;
                    IO.writePage(previousPage,previousPage.number);
                }
                else
                {
                    IO.writePage(p,p.number);
                }
                return;
            }
        }
    }

    void smartDelete(int key)
    {
        InputOutput.readDirCounter=0;
        InputOutput.writePageCounter=0;
        InputOutput.readDirCounter=0;
        InputOutput.writeDirCounter=0;
        Directory dir = IO.readDirectory();
        Page p = findRecord(key, dir);
        int index = dir.getEntryIndex(key);
        int i;
        boolean found = false;
        for( i=0;i<p.records.size();i++)
            if(p.records.get(i).key == key)
            {
                found = true;
                break;
            }
        if(found)
            p.records.remove(i);
        else {
            System.out.println("NOT FOUND");
            return;
        }
        int siblingNumber = dir.findSibling(index, p.depth-1, IO);
        if(siblingNumber != -1 && dir.entries[siblingNumber] != dir.entries[index])
        {
            Page sibling = IO.readPage(dir.entries[siblingNumber]);
            if(sibling.records.size() + p.records.size() <= p.pageSize)
            {
                p.depth--;
                dir.merge(p, sibling,IO);
                for(int j=0;j<dir.entries.length;j++)
                {
                    if(dir.entries[j]==sibling.number)// blad
                    {
                        dir.entries[j] = p.number;
                    }
                }
                Directory.freeSlots.add(siblingNumber);
            }
            else
            {
                IO.writePage(p,p.number);
            }
            dir.collapse();
            IO.writeDirectory(dir);
        }
        else
        {
            IO.writePage(p,p.number);
        }
        numberDel++;
        deletes += InputOutput.readDirCounter + InputOutput.writePageCounter + InputOutput.readDirCounter + InputOutput.writeDirCounter;
    }

    void update(Record r)
    {
        InputOutput.readDirCounter=0;
        InputOutput.writePageCounter=0;
        InputOutput.readDirCounter=0;
        InputOutput.writeDirCounter=0;
        Directory dir = IO.readDirectory();
        Page p = findRecord(r.key, dir);
        for(int i =0;i < p.records.size();i++)
        {
            if(p.records.get(i).key == r.key){
                p.records.set(i,r);
                IO.writePage(p,p.number);
                numberUP++;
                updates += InputOutput.readDirCounter + InputOutput.writePageCounter + InputOutput.readDirCounter + InputOutput.writeDirCounter;
                return;
            }
        }
        System.out.println("NOT FOUND");
    }


    Page findRecord(int key, Directory dir){
        return IO.readPage(dir.getPageNumber(key));
    }

    void initFiles()
    {
        try{
            int[] entries = {0};
            Directory dir = new Directory(1,0,entries,this.pageSize);
            IO.writeDirectory(dir);
            BufferedWriter bw = new BufferedWriter(new FileWriter("main.txt",false));
            bw.write("");
            bw.flush();
            bw.close();
            Page p = new Page(0,this.pageSize,new ArrayList<>(),0);
            IO.writePage(p,p.number);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
