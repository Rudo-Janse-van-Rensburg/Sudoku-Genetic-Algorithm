import org.w3c.dom.xpath.XPathResult;

import javax.print.CancelablePrintJob;
import java.awt.image.CropImageFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GeneticOperators {
    /**
     * Interfaces
     * */
    public double  mutationChance;
    private Random generator ;

    public GeneticOperators(int seed, double md){
        this.mutationChance = md;
        generator = new Random(seed);
    }

    private void Mutation(Chromosome candidate){
        OneToFiveSwapMutation(candidate);
        candidate.updateChromosomeHelperArrays();
    }

    public void Crossover(Chromosome a, Chromosome b, Generation generation){
        double decide = generator.nextDouble();
        double uni = 2/3, oneP = 1/3;
        if(decide > uni){
            UniformCrossover(a,b,generation);//fair amount of randomness, so do less of it.
        }else if(decide > oneP){
            OnePointCrossover(a,b,generation);//less randomness, less elitism.
        }else{
            TwoPointCrossover(a,b,generation);//more elitism, so do less.
        }
        UniformCrossover(a,b,generation);
    }
    /**
     * Mutation
     **/
    public void ThreeSwapMutation(Chromosome parent){
        /**
         * 3-Swap Mutation
         * */
        int swap = 0,
                positionA,
                positionB,
                numberOfSwaps = 3;
        boolean omitted = false;
        String lastSwap = "";
        for (int subblock=0; subblock < Chromosome.DIMENSION; subblock++){
            lastSwap = "";
            omitted = false;
            swap = 0;
            while(swap < numberOfSwaps){
                do{
                    positionA = Random(0,8);
                    positionB = Random(0,8);
                }while(positionA == positionB
                        && (lastSwap.equalsIgnoreCase(positionA+""+positionB)
                        || lastSwap.equalsIgnoreCase(positionB+""+positionA)
                )
                );
                lastSwap = positionA+""+positionB;
                omitted = Swap(
                        parent,                                          //chromosome to perform swap on
                        subblock,                                       //which sub-block to perfrom swap on
                        positionA,                                      //position a
                        positionB                                       //position b
                );
                ++swap;
                parent.updateChromosomeHelperArrays();
            }
            omitted = false;

        }

    }

    public void OneToFiveSwapMutation(Chromosome parent){
        /**
         * 1-5-Swap Mutation
         * */
        int swap = 0,
                positionA,
                positionB,
                numberOfSwaps;
        boolean omitted = false;
        String lastSwap = "";
        for (int subblock=0; subblock < Chromosome.DIMENSION; subblock++){
            lastSwap = "";
            omitted = false;
            numberOfSwaps = Random(1,5);
            swap = 0;
            while(!omitted && swap < numberOfSwaps){
                do{
                    positionA = Random(0,8);
                    positionB = Random(0,8);
                }while(positionA == positionB
                        && (lastSwap.equalsIgnoreCase(positionA+""+positionB)
                            || lastSwap.equalsIgnoreCase(positionB+""+positionA)
                            )
                );
                lastSwap = positionA+""+positionB;
                omitted = Swap(
                        parent,                                          //chromosome to perform swap on
                        subblock,                                       //which sub-block to perfrom swap on
                        positionA,                                      //position a
                        positionB                                       //position b
                );
                ++swap;
                parent.updateChromosomeHelperArrays();
            }
            omitted = false;
        }
        parent.updateChromosomeHelperArrays();
    }


    /**
     * Reproduction
     **/
    private Chromosome Reproduction(Chromosome parent){
        /**
         * Reproduction
         * */
        return new Chromosome(parent);
    }

    private void OnePointCrossover(Chromosome a, Chromosome b, Generation generation){
        /**
         * Crossover at one point
         * */
        Chromosome childA = new Chromosome(a);
        Chromosome childB = new Chromosome(b);
        int crossoverpoint = Random(0,8);
        for (int sublock=crossoverpoint; sublock < a.SUB_BLOCKS*a.SUB_BLOCKS; sublock++){
            for (int position = 0; position < a.SUB_BLOCK_DIMENSION*a.SUB_BLOCK_DIMENSION;position++){
                childA.subBlocks[sublock][position] = b.subBlocks[sublock][position];
                childB.subBlocks[sublock][position] = a.subBlocks[sublock][position];
            }
        }
        childA.updateChromosomeHelperArrays();
        childB.updateChromosomeHelperArrays();

        if(generator.nextDouble() < mutationChance){
            Mutation(childA);
        }
        if(generator.nextDouble() < mutationChance){
            Mutation(childB);
        }
        generation.Add(childA);
        generation.Add(childB);
    }

    private void TwoPointCrossover(Chromosome a, Chromosome b, Generation generation){
        /**
         * Crossover between two points
         * */
        Chromosome childA = new Chromosome(a);
        Chromosome childB = new Chromosome(b);
        int crossoverpointA = Random(0,8);
        int crossoverpointB = Random(0,8);
        if(crossoverpointA > crossoverpointB){
            int temp = crossoverpointB;
            crossoverpointB = crossoverpointA;
            crossoverpointA = temp;
        }
        for (int sublock = crossoverpointA; sublock <= crossoverpointB; sublock++ ){
            for (int position = 0; position < a.SUB_BLOCK_DIMENSION*a.SUB_BLOCK_DIMENSION;position++){
                childA.subBlocks[sublock][position] = b.subBlocks[sublock][position];
                childB.subBlocks[sublock][position] = a.subBlocks[sublock][position];
            }
        }
        childA.updateChromosomeHelperArrays();
        childB.updateChromosomeHelperArrays();

        if(generator.nextDouble() < mutationChance){
            Mutation(childA);
        }
        if(generator.nextDouble() < mutationChance){
            Mutation(childB);
        }
        generation.Add(childA);
        generation.Add(childB);
    }

    private void UniformCrossover(Chromosome a, Chromosome b,Generation generation){
        /**
         * Crossover grabbing randomly from either parent
         * */
        Chromosome child = new Chromosome(a);
        for (int subblock =0; subblock < a.SUB_BLOCKS*a.SUB_BLOCKS; subblock++){
            if(generator.nextDouble()< 0.5){
                for (int position = 0; position < a.SUB_BLOCK_DIMENSION*a.SUB_BLOCK_DIMENSION;position++){
                    child.subBlocks[subblock][position] = b.subBlocks[subblock][position];
                }
            }
        }
        child.updateChromosomeHelperArrays();

        if(generator.nextDouble() < mutationChance){
            Mutation(child);
        }
        generation.Add(child);
    }

    /**
     * ==========================================================================================
     * HELPER FUNCTIONS
     * ==========================================================================================
     **/


    private boolean Full(Integer[] array){
        for (int i=0; i < 9; i++){
            if(array[i] == 0) return false;
        }
        return true;
    }

    private int[] getRowColFromSubBlockOffset(int sublock, int offset){
        return new int[]{
                ((int)Math.floor(sublock/3)*3) + ((int)Math.floor(offset/3)),                                       //row
                ((sublock % 3)*3) + ((offset % 3))};                                                                //column
    }

    public int Random(int min, int max){
        return generator.nextInt(max - min + 1) + min;
    }

    public double Random(){
        return generator.nextDouble();
    }

    private boolean ValidSwap(Chromosome chromosome, int subblock, int positionA,int positionB){
        if(positionA == positionB){
            return true;
        }else if(!chromosome.given[subblock][positionA] && !chromosome.given[subblock][positionB] ){
            int[] rowcolA = getRowColFromSubBlockOffset(subblock,positionA);
            int[] rowcolB = getRowColFromSubBlockOffset(subblock,positionB);
            if(chromosome.subBlocks[subblock][positionA].equals(0) && chromosome.subBlocks[subblock][positionB].equals(0)){
                return true;
            }else if(chromosome.subBlocks[subblock][positionA].equals(0) && !chromosome.subBlocks[subblock][positionB].equals(0)){
                return (chromosome.ROW_NUMBER_OCCURENCES[rowcolA[0]][chromosome.subBlocks[subblock][positionB]-1] +
                        chromosome.COLUMN_NUMBER_OCCURENCES[rowcolA[1]][chromosome.subBlocks[subblock][positionB]-1]  < 4
                );
            }else if(!chromosome.subBlocks[subblock][positionA].equals(0) && chromosome.subBlocks[subblock][positionB].equals(0)){
                return (chromosome.ROW_NUMBER_OCCURENCES[rowcolB[0]][chromosome.subBlocks[subblock][positionA]-1] +
                        chromosome.COLUMN_NUMBER_OCCURENCES[rowcolB[1]][chromosome.subBlocks[subblock][positionA]-1]  < 4
                );
            }else{
                return (
                        (chromosome.ROW_NUMBER_OCCURENCES[rowcolA[0]][chromosome.subBlocks[subblock][positionB]-1] +
                                chromosome.COLUMN_NUMBER_OCCURENCES[rowcolA[1]][chromosome.subBlocks[subblock][positionB]-1]  < 4 )
                                &&
                                (chromosome.ROW_NUMBER_OCCURENCES[rowcolB[0]][chromosome.subBlocks[subblock][positionA]-1] +
                                        chromosome.COLUMN_NUMBER_OCCURENCES[rowcolB[1]][chromosome.subBlocks[subblock][positionA]-1]  < 4));

            }
        }
        return false;
    }

    private boolean Swap(Chromosome chromosome, int subblock,int positionA, int positionB){
        if(ValidSwap(chromosome,subblock,positionA,positionB)){
            Integer temp =
                    chromosome.subBlocks[subblock][positionA];
            chromosome.subBlocks[subblock][positionA] =
                    chromosome.subBlocks[subblock][positionB];
            chromosome.subBlocks[subblock][positionB] =
                    temp;
            return false;
        }
        return true;
    }
}
