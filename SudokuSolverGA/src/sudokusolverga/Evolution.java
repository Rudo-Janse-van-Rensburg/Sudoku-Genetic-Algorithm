import java.util.*;

/**
 * To DO:
 * =====================
 * -> conditionally run generation longer if it encounters a near optimal
 */
public class Evolution {
    public boolean debug = true;
    private static Chromosome specimen;
    private static GeneticOperators geneticOperators;
    private static int generationNumber, elitism, bestFitness, populationSize, maxGenerations, additionalGenerations, seed, cataclysmicOperations;
    private double mutationChance;
    private Random seedGenerator;
    private HashMap<String, Integer[][]> hmBelief;
    private HashMap<Integer, Integer[]>[] sbBelief;

    public Evolution(Chromosome chromosome, int generationLimit, int POP, int ELIT, double mc) {
        specimen = chromosome;
        elitism = ELIT;
        populationSize = POP;
        mutationChance = mc;
        seedGenerator = new Random(42069);
        instantiateGeneticOperators();
        bestFitness = specimen.Fitness();
        maxGenerations = generationLimit;
        additionalGenerations = 0;
        cataclysmicOperations = 0;
    }

    public boolean Evolve() {
        cataclysmicOperations = 0;
        hmBelief = new HashMap<String, Integer[][]>();
        sbBelief  = new HashMap[Chromosome.DIMENSION];
        for (int i =0; i < Chromosome.DIMENSION; i++)sbBelief[i] = new HashMap<Integer, Integer[]>();
        boolean solutionFound = false, nearOptimal, promising;
        Generation currentGeneration = new Generation();
        int currentGenerationFittest;
        bestFitness = Integer.MAX_VALUE;
        while (!solutionFound) {
            additionalGenerations = 0;
            CreatePopulation(currentGeneration);
            nearOptimal = false;
            promising = false;
            while (!promising && !(solutionFound = Solution(currentGeneration)) && generationNumber <= maxGenerations) {
                currentGenerationFittest = currentGeneration.Best().Fitness();
                if (currentGenerationFittest < bestFitness) {
                    bestFitness = currentGenerationFittest;
                    Print(currentGeneration);
                }
                if ((promising = currentGenerationFittest <= 8)) {
                    if ((nearOptimal = currentGenerationFittest <= 4)) {
                        additionalGenerations = 3 * generationNumber;
                        Chromosome[] arr = getGenerationArray(currentGeneration);
                        for (int i = 0; i < arr.length; i++) {
                            if (arr[i].Fitness() <= 4) {
                                updateBeliefSpace(arr[i]);
                            }
                        }
                    }
                }
                EvolveGeneration(currentGeneration);
            }
            while (promising && !nearOptimal && generationNumber <= 5 * maxGenerations && !(solutionFound = Solution(currentGeneration))) {
                currentGenerationFittest = currentGeneration.Best().Fitness();
                if (currentGenerationFittest < bestFitness) {
                    bestFitness = currentGenerationFittest;
                    Print(currentGeneration);
                }
                if ((nearOptimal = currentGenerationFittest <= 4)) {
                    additionalGenerations = 3 * generationNumber;
                    Chromosome[] arr = getGenerationArray(currentGeneration);
                    for (int i = 0; i < arr.length; i++){
                        if (arr[i].Fitness() <= 4){
                            updateBeliefSpace(arr[i]);
                        }
                    }
                }
                EvolveGeneration(currentGeneration);
            }
            while (promising && nearOptimal && !(solutionFound = Solution(currentGeneration)) && generationNumber <= additionalGenerations) {
                currentGenerationFittest = currentGeneration.Best().Fitness();
                if (currentGenerationFittest < bestFitness) {
                    bestFitness = currentGenerationFittest;
                    Print(currentGeneration);
                }
                if (currentGenerationFittest <= 4) {
                    Chromosome[] arr = getGenerationArray(currentGeneration);
                    for (int i = 0; i < arr.length; i++) if (arr[i].Fitness() <= 4) updateBeliefSpace(arr[i]);
                }
                EvolveGeneration(currentGeneration);
            }
        }
        return solutionFound;
    }

    private void CreatePopulationHM(Generation generation) {
        additionalGenerations = 0;
        instantiateGeneticOperators();
        System.out.println((cataclysmicOperations) + ".) CREATING POPULATION...");
        ++cataclysmicOperations;
        Chromosome individual, prototype = new Chromosome(specimen);
        if (!hmBelief.isEmpty() && hmBelief.size() >= 9) {
            Integer[][] beliefArray = getBeliefArray();
            for (int i = 0; i < Chromosome.DIMENSION; i++)
                for (int j = 0; j < Chromosome.DIMENSION; j++) prototype.subBlocks[i][j] = beliefArray[i][j];
        }
        Clear(generation);
        for (int i = 0; i < populationSize; i++) {
            individual = new Chromosome(prototype);
            while (!Full(individual)) InsertNumber(individual);
            generation.Add(individual);
        }
        generationNumber = 1;
        Print(generation);
    }

    private void CreatePopulation(Generation generation){
        System.out.println((cataclysmicOperations) + ".) CREATING POPULATION...");
        Clear(generation);
        instantiateGeneticOperators();
        additionalGenerations = 0;
        generationNumber = 1;
        ++cataclysmicOperations;
        Chromosome individual, prototype = new Chromosome(specimen);
        if(!hmBelief.isEmpty() && hmBelief.size() >= 20){
            Integer[][] beliefArray = getBeliefArray();
            for (int i = 0; i < Chromosome.DIMENSION; i++)
                for (int j = 0; j < Chromosome.DIMENSION; j++) prototype.subBlocks[i][j] = beliefArray[i][j];
            for (int i = 0; i < populationSize; i++) {
                individual = createChromosomeFromBelief(prototype);
                while (!Full(individual)) InsertNumber(individual);
                generation.Add(individual);
            }
        }else{
            if (!hmBelief.isEmpty() && hmBelief.size() >= 9) {
                Integer[][] beliefArray = getBeliefArray();
                for (int i = 0; i < Chromosome.DIMENSION; i++)
                    for (int j = 0; j < Chromosome.DIMENSION; j++) prototype.subBlocks[i][j] = beliefArray[i][j];
            }
            Clear(generation);
            for (int i = 0; i < populationSize; i++) {
                individual = new Chromosome(prototype);
                while (!Full(individual)) InsertNumber(individual);
                generation.Add(individual);
            }
        }
        Print(generation);
    }

    public void EvolveGeneration(Generation generation) {
        Chromosome[] populationArr = getGenerationArray(generation);
        OrderArray(populationArr);
        Chromosome p1, p2, parentBest = new Chromosome(populationArr[0]);
        Clear(generation);
        for (int i = populationArr.length - 1; i >= elitism; i--) {
            p1 = populationArr[geneticOperators.Random(elitism, i)];
            p2 = populationArr[geneticOperators.Random(elitism, i)];
            geneticOperators.Crossover(p1, p2, generation);
        }
        for (int i = 0; i < elitism; i++) generation.Add(populationArr[i]);
        populationArr = getGenerationArray(generation);
        OrderArray(populationArr);
        int genNum = generationNumber;
        Clear(generation);
        generationNumber = genNum +1;
        for (int i = populationSize - 1; i >= 0; i--) generation.Add(populationArr[i]);
        if (Equals(parentBest, generation.Best())) generation.Best().penalty += 1;
    }

    /**
     * =====================================================================================
     * Helper Functions
     * =====================================================================================
     */

    private Chromosome createChromosomeFromBelief(Chromosome prototype){
        Chromosome derivative = new Chromosome(prototype);
        double percent;
        int index, beliefSubblock;
        Integer[][] values;
        for (int sb = 0; sb < Chromosome.DIMENSION; sb++){

            values = new Integer[sbBelief[sb].size()][Chromosome.DIMENSION];
            index = 0;
            for (Map.Entry<Integer, Integer[]> mapEntry : sbBelief[sb].entrySet())values[index++] = mapEntry.getValue();
            beliefSubblock = geneticOperators.Random(0, values.length-1);
            for (int i =0; i < Chromosome.DIMENSION; i++)prototype.subBlocks[sb][i] = values[beliefSubblock][i];
            /*percent = geneticOperators.Random();
            if(percent <= (hmBelief.size()/50)){

            }*/
        }
        return derivative;
    }

    private void updateBeliefSpace(Chromosome nearOptimal){
        if(!hmBelief.containsKey(Hash(nearOptimal.subBlocks))){
            hmBelief.put(Hash(nearOptimal.subBlocks), arrayCopy(nearOptimal.subBlocks));
            System.out.println("UPDATING BELIEF SPACE #" + (hmBelief.size()));
            Print(nearOptimal);
            for (int subblock = 0; subblock < Chromosome.DIMENSION; subblock++)
                if(!sbBelief[subblock].containsKey(Arrays.hashCode(nearOptimal.subBlocks[subblock])))
                    sbBelief[subblock].put(Arrays.hashCode(nearOptimal.subBlocks[subblock]),arrayCopy(nearOptimal.subBlocks[subblock]));
        }
    }

    private Integer[][] getBeliefArray() {
        Integer[][] hiddenGivens = new Integer[Chromosome.DIMENSION][Chromosome.DIMENSION];
        String[] keys = new String[hmBelief.size()];
        Integer[][][] values = new Integer[hmBelief.size()][Chromosome.DIMENSION][Chromosome.DIMENSION];
        int index = 0;
        for (HashMap.Entry<String, Integer[][]> mapEntry : hmBelief.entrySet()) {
            keys[index] = mapEntry.getKey();
            values[index] = mapEntry.getValue();
            ++index;
        }
        int previousNumber, k;
        boolean consistent;
        for (int i = 0; i < Chromosome.DIMENSION; i++) {
            for (int j = 0; j < Chromosome.DIMENSION; j++) {
                if (!Chromosome.given[i][j]) {
                    previousNumber = 0;
                    consistent = true;
                    k = 0;
                    while (consistent && k < hmBelief.size()) {
                        if (previousNumber == 0) previousNumber = values[k][i][j];
                        else if (previousNumber != values[k][i][j]) consistent = false;
                        ++k;
                    }
                    hiddenGivens[i][j] = consistent ? previousNumber : 0;
                } else hiddenGivens[i][j] = specimen.subBlocks[i][j];
            }
        }
        return hiddenGivens;
    }

    private String Hash(Integer[][] array) {
        String hash = "";
        for (int i = 0; i < Chromosome.DIMENSION; i++) {
            hash += Arrays.hashCode(array[i]);
        }
        return hash;
    }

    private void instantiateGeneticOperators() {
        geneticOperators = new GeneticOperators((seed = seedGenerator.nextInt()), mutationChance);
    }

    private boolean Equals(Integer[][] a, Integer[][] b) {
        for (int i = 0; i < Chromosome.DIMENSION; i++) {
            for (int j = 0; j < Chromosome.DIMENSION; j++) {
                if (a[i][j] != b[i][j]) return false;
            }
        }
        return true;
    }

    private void clearConsole() {
        System.out.print('\u000C');
        try {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                Runtime.getRuntime().exec("cls");
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (final Exception e) {
            //  Handle any exceptions.
        }
    }

    private static Chromosome Clone(Chromosome a) {
        return new Chromosome(a);
    }

    private Integer[] arrayCopy(Integer[] arr){
        Integer[] copy = new Integer[arr.length];
        for (int i =0; i < arr.length; i++)copy[i] = arr[i];
        return copy;
    }

    private Integer[][] arrayCopy(Integer[][] array) {
        return Arrays.stream(array).
                map(el -> el.clone()).toArray(a -> array.clone());
    }

    private boolean Equals(Chromosome a, Chromosome b) {
        for (int sub = 0; sub < a.SUB_BLOCKS * a.SUB_BLOCKS; sub++) {
            for (int position = 0; position < a.SUB_BLOCK_DIMENSION * a.SUB_BLOCK_DIMENSION; position++) {
                if (a.subBlocks[sub][position] != b.subBlocks[sub][position]) return false;
            }
        }
        return true;
    }

    private boolean Full(Chromosome a) {
        for (int i = 0; i < a.SUB_BLOCKS * a.SUB_BLOCKS; i++) {
            for (int j = 0; j < a.SUB_BLOCK_DIMENSION * a.SUB_BLOCK_DIMENSION; j++) {
                if (a.subBlocks[i][j] == 0) return false;
            }
        }
        return true;
    }

    private boolean Solution(Generation gen) {
        Object[] populationArr = gen.population.toArray();
        for (int i = 0; i < populationArr.length; i++) {
            if (((Chromosome) populationArr[i]).Fitness() == 0) {
                System.out.println("SOLUTION ");
                Print(((Chromosome) populationArr[i]));
                return true;
            }
        }
        return false;
    }

    private void Print(Chromosome a) {
        System.out.println("");
        for (int i = 0; i < a.DIMENSION; i++) {
            Integer[] r = a.getRow(i);
            for (int j = 0; j < a.DIMENSION; j++) {
                System.out.print(r[j] + " ");
            }
            System.out.println();
        }
        System.out.println("Fitness: " + a.Fitness());
    }

    private void Print(Generation gen) {
        clearConsole();
        System.out.println("Run #" + cataclysmicOperations + "|" + "Generation #" + (generationNumber));
        System.out.println("-------------------------------");
        System.out.println("Max Gen + Bonus Gen :   " + (maxGenerations + additionalGenerations));
        System.out.println("Mutation chance     :   " + mutationChance);
        System.out.println("Seed                :   " + seed);
        System.out.println("Best Fitness        :   " + gen.Best().Fitness());
        System.out.println("Near Optimals       :   " + hmBelief.size());
        Print(gen.Best());
        System.out.println("-------------------------------");
    }

    public void Clear(Generation gen) {
        gen.population = null;
        gen.population = new PriorityQueue<>(new Comparator<Chromosome>() {
            @Override
            public int compare(Chromosome a, Chromosome b) {
                return a.Fitness().compareTo(b.Fitness());
            }
        });
    }

    private static void OrderArray(Chromosome[] array) {
        Arrays.sort(array, (t1, t2) -> {
            return t1.Fitness().compareTo(t2.Fitness());
        });
    }

    private boolean Empty(Generation gen) {
        return gen.population.isEmpty();
    }

    private  boolean Contains(Integer[] array, Integer number){
        for (int i=0; i < array.length; i++)if(array[i] == number) return true;
        return false;
    }

    private void InsertNumber(Chromosome chromosome) {
        for (int i = 0; i < chromosome.DIMENSION; i++) {
            insertMissingNumber(i, chromosome.subBlocks[i]);
            chromosome.updateChromosomeHelperArrays();
        }
    }

    private void insertMissingNumber(int sb, Integer[] subblock) {
        List<Integer> availableNumbers = new ArrayList<Integer>();
        for (int i = 0; i < 9; i++) {
            if (!containsDigit(subblock, i + 1)) {
                availableNumbers.add(i + 1);
            }
        }
        if (!availableNumbers.isEmpty()) {
            int numberToInsert = availableNumbers.get(geneticOperators.Random(0, availableNumbers.size() - 1));
            int positionToAddTo = 0;
            while (subblock[positionToAddTo] != 0 && !subblock[positionToAddTo].equals(0) && positionToAddTo < 9) {
                positionToAddTo++;
            }
            subblock[positionToAddTo] = numberToInsert;
            return;
        }
    }

    private boolean containsDigit(Integer[] array, Integer digit) {
        for (int i = 0; i < 9; i++) {
            if (array[i] == digit || array[i].equals(digit))
                return true;
        }
        return false;
    }

    private Chromosome[] getGenerationArray(Generation gen) {
        Object[] array = gen.population.toArray();
        ArrayList<Chromosome> arrayList = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            arrayList.add(new Chromosome((Chromosome) array[i]));
        }
        Chromosome[] chromosomes = new Chromosome[arrayList.size()];
        for (int i = 0; i < chromosomes.length; i++) {
            chromosomes[i] = arrayList.get(i);
        }
        OrderArray(chromosomes);
        return chromosomes;
    }

    private void Print(Boolean[][] array) {
        for (int i = 0; i < Chromosome.DIMENSION; i++) {
            for (int j = 0; j < Chromosome.DIMENSION; j++) {
                if (array[i][j]) System.out.print("T ");
                else System.out.print("F ");
            }
            System.out.println();
        }
    }

}
