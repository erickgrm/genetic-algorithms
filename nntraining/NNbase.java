/*
 * Implementation of auxiliary routines
 *@author Erick García Ramírez
 *
 */

public class Base{

    /* Transform an individual to the array of weights it encodes
     */
    public static double[] transform(individual){

    }



    /* Fitness of a given individual
     */
    public static double nnfitness(individual){
        error = 0.0;
        double[] ws = new double[initial];
        ws = transform(individual); // array of weights encoded by individual

        // Neural network output for given individual (weights)
        double[] firstLayer = new double[168];
        double[] secondLayer = new double[168];

        for(int k = 0; k < 168; k++){
            // First layer outputs
            for(int i = 0; i < initial; i++){
                firstLayer[k] += ws[i+1] * data[k][i];
            }
            // Second layer outputs
                secondLayer[k] = 1/(1+Math.exp(firstLayer[j]));
        }
        // Return error for individual according to assumed norm
        return error(secondLayer);

    public static double error(double[] arr){
        double e = 0.0;
        if(this.errtype == 0){
            for(int i = 0; i < arr.length(); i++)
                e += Math.abs(arr[i] - data[i][13]);
        }
        else{
            for(int i = 0; i < arr.length(); i++)
                e += Math.pow(arr[i] - data[i][13],p);
        }
        return e;
    }

    /*
     * Evaluate the fitness of N individuals under SGA
     * @returns an array of N doubles
     */
    public static double[] sgafitnessEvaluation(char[][] toEvaluate){
        int n = toEvaluate.length;
        double[] v = new double[n];
        v = fitnessEvaluation(toEvaluate);
        double[] vsga = new double[n];
        
        double mean = 0.0;
        double min = v[0];

            for(int i = 0; i < n; i++){
                mean += v[i];
                if(v[i] < min)
                    min = v[i];
            }
            mean /= n;
            
            for(int i = 0; i < n; i++)
                vsga[i] = v[i] + mean + min; 

        return vsga;
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

                values[i] = nnfitness(genome);
            }
        return values;
    }

    /*
     * Calculate  relative fitness of the individuals
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

        if(0.0 < max) 
            for(int j = 0; j < l; j++)
                best[j] = population[index][j];
        else{
            index = (int) (n*Math.random());
            for(int j = 0; j < l; j++)
                best[j] = population[index][j];
        }
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

    }

}
