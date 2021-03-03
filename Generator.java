package com.company;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class Generator {

    public void textFileGenerator(int n, Random rand)
    {
        int inserts = 0;
        int deletes = 0;
        int updates = 0;
        Path path = Paths.get("input"+String.valueOf(n) +".txt");
        try{
            ArrayList<Integer> keys = new ArrayList<>();
            FileWriter myWriter = new FileWriter("input"+String.valueOf(n)+".txt");
            for(int i=0;i<n;i++)
            {
                int operationType = rand.nextInt(10);
                if(operationType < 3 && keys.size() > 0 && i > 4) // delete case
                {
                    deletes++;
                    int randIndex = rand.nextInt(keys.size());
                    String str ="d : ";
                    int key = keys.get(randIndex);
                    keys.remove(randIndex);
                    str = str + String.valueOf(key);
                    str += "\n";
                    myWriter.write(str);
                    myWriter.flush();
                }
                else if(operationType < 6 && keys.size() > 0 && i > 4)// update case
                {
                    updates++;
                    String str ="u : ";
                    int randIndex = rand.nextInt(keys.size());
                    int key = keys.get(randIndex);
                    str = str + String.valueOf(key);
                    str += ":";
                    str += "(";
                    str = str + String.valueOf(rand.nextInt(1000));
                    str += ",";
                    str = str + String.valueOf(rand.nextInt(1000));
                    str += ",";
                    str = str + String.valueOf(rand.nextInt(1000));
                    str += ")\n";
                    myWriter.write(str);
                    myWriter.flush();
                }
                else // insert case
                {
                    inserts++;
                    String str ="i : ";
                    int randKey = rand.nextInt(1000);
                    keys.add(randKey);
                    str = str + String.valueOf(randKey);
                    str += ":";
                    str += "(";
                    str = str + String.valueOf(rand.nextInt(1000));
                    str += ",";
                    str = str + String.valueOf(rand.nextInt(1000));
                    str += ",";
                    str = str + String.valueOf(rand.nextInt(1000));
                    str += ")\n";
                    myWriter.write(str);
                    myWriter.flush();

                }
            }
            System.out.println("File consits of: "+String.valueOf(inserts)+" inserts, "+String.valueOf(updates)+" updates, "+String.valueOf(deletes)+" deletes.");

        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}