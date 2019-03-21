/*
 * Implementation of Elitist Genetic Algorithm (TGA)
 *@author Erick García Ramírez
 *
 */

public class TGA{

    // Population parameters
    public static int N; // size of population
    public static int L; // length of genome

    // Other
    public static char[][] population;
    public static double[] fitness;
    public static char[][] tempPopulation;
    public static double pc;  // Crossover probability
    public static double pm; // Mutation probability


    /*
     * Initialise a random population of size N
     */
    public static void startPopulation(){
        population = new char[N][L];

        for(int i = 0; i < N; i++){
            for(int j = 0; j < L; j++){
                if(Math.random(0,1) < 0.5)
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
        tempPopulation = new char[N][L];
        int accFitness = Base.sum(fitness);
        double r;
        double c;
        double ca;
        int flag = 0;
        int chosen = 0;

        while(chosen < N){
            int i = 0;
            r = Math.random(0,1);
            c = r*accFitness;
            ca = 0;
            while(flag == 0 && i < N){
                ca += fitness[i]; 
                if(ca > c){
                    flag = 1;
                    tempPopulation[i] = population[i];
                }
                i += 1;
            }
        }
    }
    
    /*
     * 2. Crossover routine
     * TGA: Keep best of old population
     */
    public static void crossover(){
        double d;
        int x;
        char[] new1 = new char[L];
        char[] new2 = new char[L];

        for(int i = 0; i < N-1; i += 2){
            d = Math.random(0,1);
            // Perform crossover if d > probability of crossover
            if(pc < d){
                x = Math.random(0,L);
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
      * No need to select anymore, mutated population will be P(t). 
      */

    /*
     * MAIN methods
     * Creat a new object of class AGS
     */
    public static double[] TGA(int N, int L, double pc, double pm, int G){

       TGA.N = N;
       TGA.L = L;
       TGA.pc = pc;
       TGA.pm = pm;

       //Start with random population P(0)
       startPopulation();

       // Print P(0)
       //System.out.println("Test");
       //for(int i = 0; i < N; i++){
       //    for(int j = 0; j < L; j++)
       //        System.out.print(population[i][j]);
       //    System.out.println();
       //}

       // Temporal population to move from P(t) to P(t+1)
       char[][] tempPopulation;

       fitness = new double[N];

       for(int t = 0; t < G; t++){
           // Evaluation of fitness
           fitness = Base.fitnessEvaluation(population);
           //Selection for reproduction
           tempPopulation = crossover();
           ////Crossover
           //combined = crossover(tempPopulation);
           //// 
           ////Select best in 
           //survivors(tempPopulation, combined);
       }
       return fitness;
    }


    public static void main(String[] args){
        
        //for(int i = 0; i < 8; i++){
        //    for(int j = 0; j < 64; j++)
        //        System.out.print(Base.schemes[i][j]);
        //    System.out.println();
        //}

        double[] aux;
        aux = TGA(70, 64, 0.9, 0.05, 500);
        for(int i = 0; i < 70; i++){
                System.out.print(aux[i]);
            System.out.println();
        }

        //System.out.println(TGA(70,500, 64, schemes, 0.9, 0.05);
    }

}
