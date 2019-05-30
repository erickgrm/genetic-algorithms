/*
 * Implementation of Elitist Genetic Algorithm (TGA)
 *@author Erick García Ramírez
 * Algoritmos Genéticos, MCIC 2019-2
 */
import java.util.Scanner;
public class TGA{

    // Population parameters
    public static int N; // size of population
    public static int L; // length of genome

    // Other
    public static char[][] population;
    public static double[] fitness;
    public static char[][] tempPopulation;
    public static char[] oldBest;

    public static double pc;  // Crossover probability
    public static double pm; // Mutation probability

    /*
     * Initialise a random population of size N
     */
    public static void startPopulation(){
        population = new char[N][L];

        for(int i = 0; i < N; i++){
            for(int j = 0; j < L; j++){
                if(Math.random() < 0.5)
                    population[i][j] = '1';
                else 
                    population[i][j] = '0';
            }
        }
    }

    /*
     *  1. Selection of individuals for crossover
     * From population, chooses N/2 pairs to reproduce
     * Saves new population in tempPopulation
     */
    public static void crossoverSelection(){
        double accFitness = Base.sum(fitness);
        double r;
        double c;
        double ca;
        int flag, i;

        for(int chosen = 0; chosen < N; chosen++){
            flag = 0;
            r = Math.random();
            c = r*accFitness;
            ca = 0.0;
            // Choose via proportional representation + random factor
            for(i = 0; i < N && flag == 0; i++){
                ca += fitness[i]; 
                if(ca > c){
                    flag = 1;
                    for(int j = 0; j < L; j++)
                        tempPopulation[chosen][j] = population[i][j];
                }
            }
            // If none was chosen above, pick a random one
            if(i == N){
                i = (int) (N*Math.random());
                for(int j = 0; j < L; j++)
                    tempPopulation[chosen][j] = population[i][j];
            }
        }
    }
    
    /*
     * 2. Crossover routine
     * TGA: Keep best of old population
     */
    public static void crossover(){
        int x;
        char[] new1 = new char[L];
        char[] new2 = new char[L];

        for(int i = 0; i < N-2; i += 2){
            // Perform crossover if d > probability of crossover
            if(pc < Math.random()){
                // Generate random int between 0 and L
                x = (int) (Math.random()*L);
                // Construction of new individual 1
                for(int j = 0; j < x; j++)
                    new1[j] = tempPopulation[i][j];
                for(int j = x; j < L; j++)
                    new1[j] = tempPopulation[i+1][j];
                // Construction of new individual 2
                for(int j = 0; j < x; j++)
                    new2[j] = population[i+1][j];
                for(int j = x; j < L; j++)
                    new2[j] = population[i][j];

                // Copy new individuals to tempPopulation
                for(int j = 0; j < L; j++){
                    tempPopulation[i][j] = new1[j];
                    tempPopulation[i+1][j] = new2[j];
                    }
            }
            else; 
                // If pc => d, there is no crossover and the new individuals are the current individuals. 
                // No need to change tempPopulation
        }
    }

    /* 
     * 3. Mutation routine
     * Mutate the bits of each element in the population after crossover
     * TGA: Keep best of old population
     */
    public static void mutation(){
        double q;

        for(int i=0; i < N; i++){
            for(int j=0; j < L; j++){ 
                q = Math.random();
                // If probability of mutation > q, swap the bit
                if(q < pm){
                    if(tempPopulation[i][j] == '1')
                        tempPopulation[i][j] = '0';
                    else 
                        tempPopulation[i][j] = '1';
                }
                else;
                    // No mutation takes place, leave bits unchanged
            }
        }
    }

     /*
      * 3. Selects P(t+1)
      * Ensure we keep best so far
      */
    public static void survival(){
        char[] oldBest;
        oldBest = new char[L];
        oldBest = Base.best(population, fitness);

        // Find index of worst individual in tempPopulation
        double[] tempFitness = new double[N];
        tempFitness = Base.fitnessEvaluation(tempPopulation); 
        double min = 0;
        int index = 0;
        for(int i = 0; i< N; i++){
            if(tempFitness[i] < min){
                min = tempFitness[i];
                index = i;
            }
        }
        // Swap worst in tempPopulation with oldBest, generating new population
        for(int j = 0; j < L; j++)
            tempPopulation[index][j] = oldBest[j];

        Base.hardcopy(tempPopulation, population);
    }

    /*
     * MAIN method
     * Creat a new object of class TGA
     */
    public static double TGA(int N, int L, double pc, double pm, int G){

       TGA.N = N;
       TGA.L = L;
       TGA.pc = pc;
       TGA.pm = pm;
       fitness = new double[N];

       //Start with random population P(0)
       startPopulation();

       // Temporary population to move from P(t) to P(t+1)
       tempPopulation = new char[N][L];

       // Generate  G generations
       for(int t = 0; t < G; t++){
            // Evaluation of fitness
            fitness = Base.fitnessEvaluation(population);

           // Selection for crossover
            crossoverSelection();
           
            // Crossover: Produces a temporary new population tempPopulation
            crossover();

            // Mutation: Produces a temporary new population after reproduction
            mutation();

            // Generate P(t+1) and allocates it to population. Previous best is kept (elitism)
            survival();
       }
       return Base.max(Base.fitnessEvaluation(population));
    }

    public static void main(String[] args){
        //System.out.println(TGA(70, 64, 0.9, 0.05, 500));

		// Several runs
        double  temp;
        double sum = 0.0;
        int[] freq = new int[9];
        for(int i = 0; i < 1000;  i++){
            temp = TGA(70, 64, 0.9, 0.05, 500);
            sum += temp;
            freq[(int) temp / 8] ++;
        }
            for(int i = 0; i < 9; i++){
                System.out.print(i*8);
                System.out.print(" = ");
                System.out.println(freq[i]);
            }
            System.out.println(sum/1000);

    }

}
