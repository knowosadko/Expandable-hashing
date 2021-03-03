package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
	UI();
    }

    static void UI()
    {
        int choice = 0;
        Generator gen = new Generator();
        ExHash exhash = null;
        do{
            System.out.println("##############################");
            System.out.println("Wybierz funkcję podając liczbę");
            System.out.println("1: Generuj plik z operacjami .txt");
            System.out.println("2: Symuluj haszowanie");
            System.out.println("3: Szukaj rekordu");
            System.out.println("4: Info");
            System.out.println("5: Wyjście");
            System.out.println("##############################");
            Scanner in = new Scanner(System.in);
            choice = Integer.parseInt(in.nextLine());
            if(choice == 1)
            {
                System.out.println("Podaj ilość operacji: ");
                int n = Integer.parseInt(in.nextLine());
                Random rand = new Random();
                gen.textFileGenerator(n,rand);
                System.out.println("Zapisano jako input"+String.valueOf(n)+".txt");
            }else if(choice == 2)
            {
                System.out.println("Podaj plik z operacjami:  ");
                String filename = in.nextLine();
                System.out.println("Podaj wielkosć strony:  ");
                int d = Integer.parseInt(in.nextLine());
                System.out.println("Podaj ilość operacji:  ");
                int n = Integer.parseInt(in.nextLine());
                exhash = new ExHash(d);
                for(int i=0;i<n;i++){
                    exhash.nextOperation(filename);
                }
                /*System.out.println("Insert accesses: "+String.valueOf((double)exhash.inserts/(double)exhash.numberIn));
                System.out.println("Delete accesses: "+String.valueOf((double)exhash.deletes/(double)exhash.numberDel));
                System.out.println("Update accesses: "+String.valueOf((double)exhash.updates/(double)exhash.numberUP));*/

            }else if(choice == 3)
            {
                System.out.println("Podaj klucz:  ");
                int key = Integer.parseInt(in.nextLine());
                List<Record> records = new ArrayList<>();
                InputOutput.readPageCounter=0;
                InputOutput.readDirCounter=0;
                Record r = exhash.search(key);
                System.out.println(String.valueOf(r.key)+" : "+String.valueOf(r.coord[0])+" "+String.valueOf(r.coord[1])+" "+String.valueOf(r.coord[2]));
                System.out.println("Ilosc odczytów stron: "+String.valueOf(InputOutput.readPageCounter)+" Ilosc odczytow skorowidza: "+String.valueOf(InputOutput.readDirCounter));
            } else if(choice == 4)
            {
                System.out.println("Program tworzy i symuluje operacja na plikach o rozproszonej organizacji \nZaimplementowana operacja odczytu, zapisu, usuwania i aktualizacji.");
            }
        }while(choice != 5);
    }
}
