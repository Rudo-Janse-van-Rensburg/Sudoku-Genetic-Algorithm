import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static int boardDim = 9;

    private static Chromosome readInSudoku(String puzzleName){
        Integer[][] puzzle = new Integer[boardDim][boardDim];
        try{
            File file = new File(puzzleName+".txt");
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
    
        private static Chromosome readInSudokuString(String puzzleStr){
        Integer[][] puzzle = new Integer[boardDim][boardDim];
        try{ 
            
            Scanner sc = null;
            BufferedReader br = new BufferedReader(new StringReader(puzzleStr));
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
        /*Chromosome specimen = readInSudokuString(
                "0 0 2 0 0 0 5 0 0 \n" +
                "0 1 0 7 0 5 0 2 0 \n" +
                "4 0 0 0 9 0 0 0 7 \n" +
                "0 4 9 0 0 0 7 3 0 \n" +
                "8 0 1 0 3 0 4 0 9 \n" +
                "0 3 6 0 0 0 2 1 0 \n" +
                "2 0 0 0 8 0 0 0 4 \n" +
                "0 8 0 9 0 2 0 6 0 \n" +
                "0 0 7 0 0 0 8 0 0 "
        );*/
        Chromosome specimen = readInSudoku(args[0]);
        
        Evolution evolution = evolution = new Evolution(
                specimen,
                200000,
                11,
                0,
                0.02489);
        evolution.Evolve();


    }

}
