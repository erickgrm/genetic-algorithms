/*
 * Implementation of   Genetic Algorithm (TGA)
 *@author Erick García Ramírez
 *
 */

public class TGA{

    // Population parameters
    public static int N; // size of population
    public static int G; // number of generations
    public static int L; // length of genome
    
    public static char[][] population;
    public static double[][] fitness;
    public static char[][] previousPopulation;
    public static double pc = 0.9;  // Crossover probability
    public static double pm = 0.05; // Mutation probability

    public static char[] optimal; 

    /*
     * Initialise a random population of size N
     */
    public static void initialization(){
        population = new char[N][L];
        fitness = new double[N];
        for(int i = 0; i < N; i++){
            for(int j = 0; j < L; j++){
                if(Math.random() < 0.5)
                    population[i][j] = '1';
                else 
                    population[i[[j] = '0';
            }
        }
    }
    
    /*
     * Evaluate the fitness of N individuals
     * @returns an array of N doubles
     */
    public static double[] fitnessEvaluation(char[][] toEvaluate){
        evaluations = new double[N];
        //string genome = null;
            for(int i = 0; i < N; i++){
                genome = toEvaluate[i];
                evaluations[i] = targetFn(genome);
            }
        return evaluations;
    }
    
    /*
     * Value of target function on given genome
     * Here, the target function is the hamming distance between the optimal genome and the given genome
     * @param genome
     * @returns int
     */
    public static int targetFn(char[] genome){
        value = 0;
        for(int i = 0; i < optimal.length(); i++)
            if(genome[i] != optimal[i])
                value += 1;
        return value;
    }



}
