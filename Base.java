/*
 * Implementation of auxiliary routines
 *@author Erick García Ramírez
 *
 */

public class Base{

    public static final char[][] schemes;
    static{
        schemes = new char[8][64];
        // Fill up with *
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 64; j++)
                schemes[i][j] = '*';
        }
        // Copy schemes 
        for(int i = 0; i < 8; i++){
            for(int j = i*8; j < (i+1)*8; j++)
                schemes[i][j] = '1';
        }
    }

    public static char[] optimal; 
    static{
        optimal = new char[64];
        for(int i = 0; i < 64; i++) optimal[i] = '1';
    }


    // Other
    public static char[][] population;
    public static double[] fitness;
    public static char[][] previousPopulation;
    public static double pc;  // Crossover probability
    public static double pm; // Mutation probability


    /*
     * Evaluate the fitness of N individuals
     * @returns an array of N doubles
     */
    public static double[] fitnessEvaluation(char[][] toEvaluate){
        int n = toEvaluate.length;
        int l = toEvaluate[0].length; 
        fitness = new double[n];
        
        char genome[];
        genome = new char[64];

        //string genome = null;
            for(int i = 0; i < n; i++){
                // Extract the genome of the individual i
                for(int j = 0; j < l; j++)
                    genome[j] = toEvaluate[i][j];

                fitness[i] = targetFn(genome);
            }
        return fitness;
    }
    
    /*
     * Evaluation of target function on a given genome
     * The target function is R_1(x), 8 times the number of schemes S_i x belongs to (see page 77 in book). 
     * @param genome, array of 64 chars
     * @returns int
     */
    public static int targetFn(char[] genome){
        int value = 0;
        // cycle over the 8 schemes
        for(int i = 0; i < 8; i++){
            int flag = 1;
            // check if the given genome belongs to scheme i
            for(int j = i*8; j < (i+1)*8; j++){
                if(genome[j] != schemes[i][j])
                    flag = 0;
            }
            // add 1 to value if genome belongs to the scheme i
            value += flag;
        }
        return value*8;
    }
    
    public static void main(String[] args){

        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 64; j++)
                System.out.print(schemes[i][j]);
            System.out.println();
        }
    }

}
