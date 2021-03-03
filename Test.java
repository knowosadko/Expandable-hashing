package com.company;

public class Test {
    static int depth = 10;
    public static void main(String[] args) {
        System.out.println(32);
        getEntry(32);
        System.out.println(1);
        getEntry(1);
        System.out.println(0);
        getEntry(0);
        System.out.println(2938243);
        getEntry(2938243);
        System.out.println(16);
        getEntry(16);
        System.out.println(12341);
        getEntry(12341);

    }
    static public int getEntry(int key)
    {
        int address = 0;
        int tempKey = key;
        for(int i=0;i<depth;i++)
        {
            address += (tempKey % 2)*Math.pow(2,i);
            System.out.println(tempKey % 2);
            tempKey = tempKey/2;
        }
        System.out.println("Value of last 4 bits: "+String.valueOf(address));
        return 0;
    }
}
