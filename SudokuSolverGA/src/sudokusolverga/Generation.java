import java.util.*;

public class Generation {
    public PriorityQueue<Chromosome> population;

    public Generation(){
        population = new PriorityQueue<>(new Comparator<Chromosome>() {
            @Override
            public int compare(Chromosome a, Chromosome b) {
                return a.Fitness().compareTo(b.Fitness());
            }
        });

    }

    public Chromosome Best(){
        return  population.peek();
    }

    public void Add(Chromosome child){
        this.population.add(child);
    }

    public Chromosome Remove(){
        return population.remove();
    }

}

