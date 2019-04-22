/*
 * Implementation of auxiliary routines
 *@author Erick García Ramírez
 *
 */

public class NNbase{
    public static double[][] data;
    public static int initial; // 168
    public static int hidden; // 13
    public static int errtype; // p =>0 even 

    public void NNbase(double[][] data, int errtype){
        NNbase.data = data;
        NNbase.errtype = errtype;
    }
    
    /* Transform an individual to the array of weights it encodes
     */
    public static double[] genome_to_weights(char[] genome){
        double[] weights = new double[14];
        double wi;
        double dec;

        for(int i = 0; i < 14; i++){
            wi = 0.0;
            dec = 0.0;
            // Calculate integer part
            if(genome[14*i + 1] == '1') 
                wi += Math.pow(2,2);
            if(genome[14*i + 2] == '1') 
                wi += Math.pow(2,1);
            if(genome[14*i + 3] == '1') 
                wi += Math.pow(2,0);
            // Calculate decimal part
            for(int k = 4; k < 32; k++){
                if(genome[14*i + k] ==  '1') 
                    dec += Math.pow(2, 31-k);
            }
            wi += dec/Math.pow(2,28); 
            if(genome[14*i] == '1') wi *= -1;

            weights[i] = wi;    
        }
        return weights;
    }

    /*
     * Transform a given array of 14 weights to the corresponding 
     * genome
     */
    public static char[] weights_to_genome(double[] weights){
        char[] genome = new char[14*32];
        char[] gen = new char[32];
        
        for(int i = 0; i < 14; i++){
            gen = double_to_32bits(weights[i]);
            for(int j = 0; j < 32; j++)
                genome[14*i + j] = gen[j];
        }
        return genome;
    }

    /*
     * Write double as a 32-chars array
     */
    public static char[] double_to_32bits(double w){
        char[] str = new char[32];
        int integer_part;
        int decimal_part;

        if(w < 0) {str[0] = '1'; w *= -1;}
        else str[0] = '0';

        integer_part = (int) Math.floor(w);
        decimal_part = (int) Math.floor((w - Math.floor(w))*27);
        
        for(int k = 0; k < 3; k++){
            if(integer_part % 2 == 0) str[3-k] = '0'; 
            else str[3-k] = '1';
            integer_part = (int) integer_part/2;
        }
        for(int k = 0; k < 28; k++){
            if(decimal_part % 2 == 0) str[31-k] = '0'; 
            else str[31-k] = '1';
            decimal_part = (int) decimal_part/2;
        }
        return str;
    }

    /* Fitness of a given individual
     */
    public static double nnfitness(char[] individual){
        double error = 0.0;
        double[] ws = new double[14];
        ws = genome_to_weights(individual); // array of weights encoded by individual

        // Neural network output for given individual (weights)
        double[] firstLayer = new double[initial];
        double[] secondLayer = new double[initial];

        for(int k = 0; k < 168; k++){
            // First layer outputs
            for(int i = 0; i < 14; i++){
                firstLayer[k] += ws[i+1] * data[k][i];
            }
            firstLayer[k] += ws[0];
            // Second layer outputs
            secondLayer[k] = 1/(1+Math.exp(firstLayer[k]));
        }
        // Return error for individual according to assumed norm
        return error(secondLayer);
    }

    public static double error(double[] arr){
        double e = 0.0;
        if(errtype == 0){
            for(int i = 0; i < initial; i++)
                e += Math.abs(arr[i] - data[i][13]);
        }
        else{
            for(int i = 0; i < initial; i++)
                e += Math.pow(arr[i] - data[i][13], errtype);
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
        genome = new char[32*14];

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
            index = (int) (n * Math.random());
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

    public static char[] ToBinary(int n){
        char[] bin = new char[4];
        bin[0]=bin[1]=bin[2]=bin[3] = '0';
        int i = 0;

        while(0 < n && i < 4 ){
            if(n % 2 == 0)
                bin[i] = '0';
            else 
                bin[i] = '1';
            n = (int) Math.floor(n/2);
            i++;
        }
        char temp = bin[0];
        bin[0] = bin[3];
        bin [3] = temp;
        temp = bin[1];
        bin[1] = bin[2];
        bin[2] = temp;

        return bin;
    }
         
               
    public static void main(String[] args){
    
    }

}
