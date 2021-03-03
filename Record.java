package com.company;
import static java.lang.Math.pow;

public class Record {
    int[] coord;
    int key;

    public Record(int [] arr, int key)
    {
        this.coord = arr;
        this.key = key;
    }

    public Record(int a)
    {
        int[] arr = new int[3];
        arr[0] = -1;
        arr[1] = -1;
        arr[2] = -1;
        this.coord = arr;
        this.key = -1;
    }

    public Record(String line)//TODO add delete parsing
    {
        String[] op_key_rec = line.split(":");
        int key = Integer.parseInt(op_key_rec[1].replace(" ", ""));
        int arr[] = new int[3];
        if(op_key_rec[0].charAt(0) == 'd')
        {
            arr[0] = -1;
            arr[1] = -1;
            arr[2] = -1;
        }
        else{
            String srec = op_key_rec[2].replace("(", "");
            srec = srec.replace(")", "");
            String[] vec = srec.split(",");
            arr[0] = Integer.parseInt(vec[0]);
            arr[1] = Integer.parseInt(vec[1]);
            arr[2] = Integer.parseInt(vec[2]);
        }
        this.key = key;
        this.coord = arr;
    }

    //Display
    public void show()
    {
        System.out.println(String.valueOf(key)+" : ("+String.valueOf(coord[0])+","+String.valueOf(coord[1])+","+String.valueOf(coord[2])+")");
    }

}
