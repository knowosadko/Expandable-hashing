package com.company;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class InputOutput {
    private final String mainFilename;
    private final String dirFilename;
    private final int pageSize;
    boolean dirAppend;
    boolean mainAppend;
    static public int readPageCounter = 0;
    static public int writePageCounter = 0;
    static public int readDirCounter = 0;
    static public int writeDirCounter = 0;

    public InputOutput(String mainFilename,String dirFilename ,int pageSize)
    {
        this.mainFilename = mainFilename;
        this.dirFilename = dirFilename;
        this.mainAppend = false;
        this.dirAppend = false;
        this.pageSize = pageSize;
    }

    Page readPage(int number)
    {
        System.out.println("[Read] Page Number : "+String.valueOf(number));
        Page page = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(this.mainFilename));
            for(int i=0;i<number;i++)
                br.readLine();
            String line = br.readLine();
            String[] data = line.split(" ");
            //reading header
            int pageSize = Integer.parseInt(data[0]);
            int depth = Integer.parseInt(data[1]);
            List<Record> records = new ArrayList<>();
            readPageCounter++;
            for(int i=2;i< this.pageSize*4 + 2;i+=4){ // changed
                // reading a record
                int key = Integer.parseInt(data[i]);
                int arr[] = new int[3];
                arr[0] = Integer.parseInt(data[i+1]);
                arr[1] = Integer.parseInt(data[i+2]);
                arr[2] = Integer.parseInt(data[i+3]);
                // reading a children
                if(key != -1) {
                    Record r = new Record(arr, key);
                    records.add(r);
                }
            }
            page = new Page(number,this.pageSize,records, depth);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return page;
    }

    void writePage(Page page,int address)
    {
        try {
            List<String> fileContent = null;
            try{
                fileContent = new ArrayList<>(Files.readAllLines(Paths.get(this.mainFilename)));
            }catch(NoSuchFileException e)
            {
                fileContent = new ArrayList<>();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(this.mainFilename,false));
            for(int i=0;i<address;i++)
            {
                bw.write(fileContent.get(i));
                writeNL(bw);
            }
            writeHeader(page, bw);
            int i;
            for(i=0;i<pageSize;i++)
            {
                if(i < page.records.size())
                    writeRecord(page.records.get(i),bw);
                else
                    writeRecord(new Record(-1),bw);
            }
            writePageCounter++;
            writeNL(bw);
            for(int j=address+1;j<fileContent.size();j++)
            {
                bw.write(fileContent.get(j));
                writeNL(bw);
            }
            bw.flush();
            bw.close();
            this.mainAppend = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void  writeRecord(Record r, BufferedWriter br)
    {
        String str = "";
        str += String.valueOf(r.key);
        str += " ";
        for(int a : r.coord)
        {
            str+= String.valueOf(a)+" ";
        }
        try {
            br.write(str);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    void writeHeader(Page page, BufferedWriter br)
    {
        try {
            br.write(String.valueOf(page.records.size())+" ");
            br.write(String.valueOf(page.depth)+" ");
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    void writeNL(BufferedWriter bw)
    {
        try {
            bw.write("\n");
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

   public Directory readDirectory()
    {
        Directory dir = null;
        List<Integer> entriesList = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(this.dirFilename));
            //readheader
            String line = br.readLine();
            String[] data = line.split(" ");
            int depth = Integer.parseInt(data[0]);
            int pageSize = Integer.parseInt(data[1]);
            int entry;
            //reading entries
            while((line = br.readLine())!=null)
            {
                entry = Integer.parseInt(line);
                entriesList.add(entry);
            }
            int [] entriesArray = new int[entriesList.size()];
            int i=0;
            for(int e : entriesList )
            {
                entriesArray[i] = e;
                i++;
            }
            readDirCounter++;
            dir = new Directory( entriesArray.length, depth, entriesArray, pageSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dir;
    }

    public void writeDirectory(Directory dir)
    {

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(this.dirFilename,false));
            //readheader
            String header = String.valueOf(dir.depth) + " " +  String.valueOf(dir.pageSize);
            //reading entries
            bw.write(header);
            writeNL(bw);
            for(int e : dir.entries )
            {
                String entry = String.valueOf(e);
                bw.write(entry);
                writeNL(bw);
            }
           bw.flush();
            bw.close();
            writeDirCounter++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String readAll()
    {
        String str = "";
        try {
            Directory dir = null;
            List<Integer> entriesList = new ArrayList<>();
            try {
                BufferedReader br = new BufferedReader(new FileReader(this.dirFilename));
                //readheader
                String line = br.readLine();
                String[] data = line.split(" ");
                int depth = Integer.parseInt(data[0]);
                int pageSize = Integer.parseInt(data[1]);
                int entry;
                //reading entries
                while((line = br.readLine())!=null)
                {
                    entry = Integer.parseInt(line);
                    entriesList.add(entry);
                }
                int [] entriesArray = new int[entriesList.size()];
                int i=0;
                for(int e : entriesList )
                {
                    entriesArray[i] = e;
                    i++;
                }
                dir = new Directory( entriesArray.length, depth, entriesArray, pageSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
            for(int j=0; j < dir.entries.length;j++)
            {
                str = str + " " + String.valueOf(j) + " p: " + String.valueOf(dir.entries[j]);
                BufferedReader br = new BufferedReader(new FileReader(this.mainFilename));
                for(int i=0;i<dir.entries[j];i++)
                    br.readLine();
                String line = br.readLine();
                String[] data = line.split(" ");
                //reading header
                int depth = Integer.parseInt(data[1]);
                int pageSize = Integer.parseInt(data[0]);
                List<Record> records = new ArrayList<>();
                for(int i=2;i< this.pageSize*4 + 2;i+=4){ // changed
                    // reading a record
                    int key = Integer.parseInt(data[i]);
                    int arr[] = new int[3];
                    arr[0] = Integer.parseInt(data[i+1]);
                    arr[1] = Integer.parseInt(data[i+2]);
                    arr[2] = Integer.parseInt(data[i+3]);
                    // reading a children
                    if(key != -1) {
                        Record r = new Record(arr, key);
                        records.add(r);
                    }
                }
                str = str + " main file: " + " depth: "+ depth+  " " ;

                if(records.size() == 0)
                    str += "[EMPTY]";
                else
                    for(Record r : records)
                    {
                        str += "key: " + String.valueOf(r.key);
                        str += " vec: ";
                        str += String.valueOf(r.coord[0])+" ";
                        str += String.valueOf(r.coord[1])+" ";
                        str += String.valueOf(r.coord[2])+" ";
                        str += "|";
                    }
                str += "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }
}
