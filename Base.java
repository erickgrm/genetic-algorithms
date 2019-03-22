/*
 * Implementation of auxiliary routines
 *@author Erick García Ramírez
 *
 */

public class Base{

    //public static double[] fitness;
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

    /*
     * Evaluate the fitness of N individuals
     * @returns an array of N doubles
     */
    public static double[] fitnessEvaluation(char[][] toEvaluate){
        int n = toEvaluate.length;
        int l = toEvaluate[0].length; 
        double[] values = new double[n];
        
        char genome[];
        genome = new char[64];

        //string genome = null;
            for(int i = 0; i < n; i++){
                // Extract the genome of the individual i
                for(int j = 0; j < l; j++)
                    genome[j] = toEvaluate[i][j];

                values[i] = targetFn(genome);
            }
        return values;
    }
    /*
     * Calcuate  relative fitness of the individuals
     * @returns an array of N doubles
     */
    public static double[] relFitness(double[] values){
        double maxValue = 0.0;
        int n = values.length;
        double[] relValues = new double[n];
        
        for(int i = 0; i < n; i++)
            maxValue += values[i];

        for(int i = 0; i < n; i++){
            if(maxValue != 0.0) 
                relValues[i] = values[i]/maxValue;
            else 
                relValues[i] = 0.0;
        }
        return relValues;
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
    
    public static char[] best(char[][] population, double[] fitness){
        int n = population.length;
        int l = population[0].length;
        char[] best = new char[l];

        int index = 0;
        double max = 0.0;
        for(int i = 0; i < n; i++){
            if(max < fitness[i]){
                max = fitness[i];
                index = i;
            }
        }

        for(int j = 0; j < l; j++)
            best[j] = population[index][j];

    return best;
    }

    /*
     * Copies by value arr1 into arr2
     */
    public static void hardcopy(char[][] arr1, char[][] arr2){
        for(int i = 0; i < arr1.length; i++)
            for(int j = 0; j < arr1[0].length; j++)
                arr2[i][j] = arr1[i][j];
    }

   /*
    * @returns S the sum of the values in an array
    */
    public static double sum(double[] arr){
        double total = 0.0;
        for(int i = 0; i < arr.length; i++)
            total += arr[i];
        return total;
    }
   /*
    * @returns maximum of array
    */
    public static double max(double[] arr){
        double max = 0.0;
        for(int i = 0; i < arr.length; i++)
            if(max < arr[i])
                max = arr[i];
        return max;
    }


    public static void main(String[] args){

        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 64; j++)
                System.out.print(schemes[i][j]);
            System.out.println();
        }
    }

}
