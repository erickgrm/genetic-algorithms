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
    public static char[][] previousPopulation;
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
     * 1. Crossover routine
     * Evaluates fitness of all current individuals, chooses pairs to recombine, recombine
     * stops when reaching a new population of size N
     * TGA: Keep best of old population
     * @returns new population
     */

    /* 
     * 2. Mutation routine
     * Mutate the bits of each element in the new  population (after crossover)
     * TGA: Keep best of old population
     * @returns new population
     * /

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
