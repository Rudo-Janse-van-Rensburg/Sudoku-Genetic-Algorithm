import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static int boardDim = 9;

    private static Chromosome readInSudoku(String puzzleName){
        Integer[][] puzzle = new Integer[boardDim][boardDim];
        try{
            File file = new File(
                    "/home/rudo/Desktop/Homework/COS 314/A/02/sudokus/"+puzzleName+".txt"
            );
            Scanner sc = null;
            BufferedReader br = new BufferedReader(new FileReader(file));
            int row = 0, col = 0;
            String line =null;
            while(row < boardDim && (line = br.readLine()) != null){
                col = 0;
                sc = new Scanner(line).useDelimiter(" ");
                while(sc.hasNext() && col < boardDim ){
                    puzzle[row][col++] = Integer.parseInt(sc.next().trim());
                }
                sc.close();
                ++row;
            }
            br.close();
        }catch(Exception e){
                System.err.println("Error: "+e.getMessage());
        }


        return new Chromosome(puzzle);
    }

    public static void main(String[] args) {
        Chromosome specimen = readInSudoku("s15a");
        Evolution evolution = evolution = new Evolution(
                specimen,
                200000,
                11,
                0,
                0.02489);
        evolution.Evolve();


    }

}
