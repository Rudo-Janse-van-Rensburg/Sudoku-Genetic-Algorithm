import java.awt.*;
import java.util.Arrays;

public class Chromosome {
    public Integer[][] subBlocks;
    public int penalty;
    public static int DIMENSION = 9,
            SUB_BLOCKS           = 3,
            SUB_BLOCK_DIMENSION  = 3;
    public int [][] ROW_NUMBER_OCCURENCES= null,
            COLUMN_NUMBER_OCCURENCES = null;
    public static Boolean [][]given = null;

    public Chromosome(Integer[][] puzzle){
        COLUMN_NUMBER_OCCURENCES = new int[DIMENSION][DIMENSION];
        ROW_NUMBER_OCCURENCES = new int[DIMENSION][DIMENSION];
        subBlocks = new Integer[9][9];
        for(int subblockrow =0; subblockrow < SUB_BLOCKS; subblockrow++){
            for(int subblockcolumn =0; subblockcolumn < SUB_BLOCKS; subblockcolumn++){
                for (int block=0; block < SUB_BLOCK_DIMENSION*SUB_BLOCK_DIMENSION; block++){
                    subBlocks[(SUB_BLOCKS*subblockrow)+subblockcolumn][block] = puzzle[(subblockrow*SUB_BLOCKS)+(int)Math.floor(block/3)][(SUB_BLOCK_DIMENSION*subblockcolumn)+block%3];
                }
            }
        }
        if(given == null){
            given = new Boolean[DIMENSION][DIMENSION];
            for (int i =0; i < DIMENSION; i++){
                for (int j =0; j < DIMENSION; j++){
                    given[i][j] = subBlocks[i][j] != 0;
                }
            }
        }
        updateChromosomeHelperArrays();
    }

    public void updateChromosomeHelperArrays(){
        /*COLUMN_NUMBER_OCCURENCES = new int[DIMENSION][DIMENSION];
        ROW_NUMBER_OCCURENCES = new int[DIMENSION][DIMENSION];*/
        Integer[] r, c;
        for (int i= 0; i < DIMENSION; i++){
            r = getRow(i);
            c = getColumn(i);
            for (int j = 0; j < DIMENSION; j++){
                ROW_NUMBER_OCCURENCES[i][j] = 0;
                COLUMN_NUMBER_OCCURENCES[i][j]=0;
                for (int k =0; k < DIMENSION; k++){
                    if(j+1 == r[k]){
                        ROW_NUMBER_OCCURENCES[i][j] += 1;
                    }
                    if(j+1 == c[k]){
                        COLUMN_NUMBER_OCCURENCES[i][j] += 1;
                    }
                }
            }
            r = null;
            c = null;
        }
        r = null;
        c = null;
    }

    public Chromosome(Chromosome chromosome){
        this.subBlocks = new Integer[DIMENSION][DIMENSION];
        this.COLUMN_NUMBER_OCCURENCES = new int[DIMENSION][DIMENSION];
        this.ROW_NUMBER_OCCURENCES = new int[DIMENSION][DIMENSION];
        for (int i=0; i < DIMENSION; i++){
            System.arraycopy(chromosome.subBlocks[i],0, this.subBlocks[i],0, DIMENSION);
            System.arraycopy(chromosome.COLUMN_NUMBER_OCCURENCES[i], 0, this.COLUMN_NUMBER_OCCURENCES[i], 0, DIMENSION);
            System.arraycopy(chromosome.ROW_NUMBER_OCCURENCES[i], 0, this.ROW_NUMBER_OCCURENCES[i], 0, DIMENSION);
        }
    }

    public Integer[] getRow(int r){
        Integer[] row   = new Integer[DIMENSION];                       //
        int rowSubBlock = (int) Math.floor(r/3)*SUB_BLOCKS;             //finding start sub-block
        int rowOffset   = 0 + (r%SUB_BLOCKS)*SUB_BLOCK_DIMENSION;       //finding start of sub-block array
        int currentCol  = 0;                                            //
        for (int subBlock =0; subBlock < SUB_BLOCKS; subBlock++){
            for (int col = 0; col < SUB_BLOCK_DIMENSION; col++){
                row[currentCol++] =
                        subBlocks[(rowSubBlock)+subBlock]
                                [(rowOffset)+col];
            }
        }
        return row;
    }

    public Integer[] getColumn(int c){
        Integer[] column    = new Integer[DIMENSION];                   //
        int colSubBlock     =  0 + (int) Math.floor(c/3);               //finding start sub-block
        int colOffset       =  (c % SUB_BLOCKS);                        //finding start of sub-block array
        int currentRow = 0;                                             //
        for (int subBlock = 0; subBlock < SUB_BLOCKS; subBlock++){
            for (int row =0; row < SUB_BLOCK_DIMENSION; row++){
                column[currentRow++] =
                        subBlocks[(colSubBlock)+(SUB_BLOCKS*subBlock)]
                                [(colOffset)+(SUB_BLOCK_DIMENSION*row)];
            }
        }
        return column;
    }

    public Integer Fitness(){
        int numberMissingNumbersRow = 0, numberMissingNumbersCol= 0;
        for (int i=0; i < DIMENSION; i++){
            for (int j=0; j < DIMENSION; j++){
                if(COLUMN_NUMBER_OCCURENCES[i][j] ==0) ++numberMissingNumbersCol;
                else if(COLUMN_NUMBER_OCCURENCES[i][j] > 1) numberMissingNumbersCol += COLUMN_NUMBER_OCCURENCES[i][j]-1;


                if(ROW_NUMBER_OCCURENCES[i][j] ==0) ++numberMissingNumbersRow;
                else if(ROW_NUMBER_OCCURENCES[i][j] > 1) numberMissingNumbersRow += ROW_NUMBER_OCCURENCES[i][j]-1;
            }
        }
        return numberMissingNumbersCol + numberMissingNumbersRow + penalty;
    }

}
