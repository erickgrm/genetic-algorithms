/*
 * Auxiliary routines
 *@author Erick García Ramírez
 */
import java.util.Scanner;

public class NNbase{
    public static int I = 14;// 14*3 + 4; // No of initial variables
    public static int W = I*3 + 4; // No of weights
    public static int L = W*32; // Length of genome

    public static double[][] data;
    public static int error_type; // Non-negative even integer
    public static int no_of_training_samples; // No of rows in data[][]
    public static int N; // Population size

    public void NNbase(double[][] data, int error_type, int N){
        NNbase.data = data;
        NNbase.error_type = error_type;
        NNbase.no_of_training_samples = data.length;
        NNbase.N = N;
    }
    
    // Transform a genome to the array of weights it encodes
    public static double[] genome_to_weights(char[] genome){
        double[] weights = new double[W];
        double wi;
        double dec;

        for(int i = 0; i < W; i++){
            wi = 0.0;
            dec = 0.0;
            // Calculate integer part
            if(genome[W*i + 1] == '1') 
                wi += Math.pow(2,2);
            if(genome[W*i + 2] == '1') 
                wi += Math.pow(2,1);
            if(genome[W*i + 3] == '1') 
                wi += Math.pow(2,0);
            // Calculate decimal part
            for(int k = 4; k < 32; k++){
                if(genome[W*i + k] ==  '1') 
                    dec += Math.pow(2, 31-k);
            }
            wi += dec/(Math.pow(2,28)-1); 
            if(genome[W*i] == '1') wi *= -1;

            weights[i] = wi;    
        }
        return weights;
    }//END of genome_to_weights

    //Transform an array of W weights to a genome
    public static char[] weights_to_genome(double[] weights){
        char[] genome = new char[L];
        char[] gen = new char[L];
        
        for(int i = 0; i < W; i++){
            gen = double_to_32bits(weights[i]);
            for(int j = 0; j < 32; j++)
                genome[W*i + j] = gen[j];
        }
        return genome;
    }//END of weights_to_genome

    /*
     * Encode a double as a 32-chars array
     * 1st bit for sign
     * 3 bits for the integer part
     * 28 bits for the decimal part
     */
    public static char[] double_to_32bits(double w){
        char[] str = new char[32];
        int integer_part = (int) w;
        double decimal_part = (int) ((w - (int) w)*(Math.pow(2,28)-1));

        if(w < 0) {str[0] = '1'; w *= -1;}
        else str[0] = '0';

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
    }//END of double_to_32bits

    // Fitness of a given genome
    public static double fitness(char[] genome){
        double error = 0.0;
        double[] weights = new double[W];
        weights = genome_to_weights(genome); 

        // Neural network output for given individual (weights)
        double[][] first_layer_outputs = new double[no_of_training_samples][3];
        double[] final_outputs = new double[no_of_training_samples];
        
        // Calculate network outputs for each training sample
        for(int k = 0; k < no_of_training_samples; k++){
            // First layer outputs, 3 outputs for each k
            for(int j = 0; j < 3; j++){
                first_layer_outputs[k][j] += weights[I*j]; // Threshold contribution 
                for(int i = 1; i < I; i++)
                    first_layer_outputs[k][j] += 
                        weights[I*j + i] * data[k][i-1]; 
                first_layer_outputs[k][j] = 
                    activation_function(first_layer_outputs[k][j]);
            }
            // Final outputs, single output for each k
            final_outputs[k] += weights[I*3]; // Threshold contribution
            for(int j = 0; j < 3; j++)
                final_outputs[k] += first_layer_outputs[k][j];
            final_outputs[k] = activation_function(final_outputs[k]);
        }
        // Return error for genome according to assumed error_type
        return error(final_outputs);
    }//END of fitness

    public static double activation_function(double x) {
        // return Math.tanh(x); // Hyperbolic tangent
        return 1/(1 + Math.exp((-1)*x)); // Sigmoid
    }

    public static double error(double[] outputs){
        double e = 0.0;
        if(error_type == 0){
            // Sum L_1 errors over all samples
            for(int k = 0; k < no_of_training_samples; k++)
                e += Math.abs(outputs[k] - data[k][13]);
        }
        else{
            // Sum L_error_type errors over all samples
            for(int k = 0; k < no_of_training_samples; k++)
                e += Math.pow(outputs[k] - data[k][13], error_type);
        }
        return e;
    }//END of error

    /*
     * Calculate the fitness of N genomes
     * @returns an array of N doubles
     */
    public static double[] fitnessEvaluation(char[][] genomes){
        double[] fitness_values = new double[N];
        char[] genome = new char[L];

        for(int i = 0; i < N; i++){
            // Extract the i-th genome 
            for(int j = 0; j < L; j++)
                genome[j] = genomes[i][j];
            // Calculate fitness of the i-th genome
            fitness_values[i] = fitness(genome);
        }
        return fitness_values;
    }

    /*
     * Calculate  relative fitness of N fitness values
     * * @returns an array of N doubles
     */
    public static double[] relativeFitness(double[] values){
        double sum_of_values = 0.0;
        double[] relative_values = new double[N];
        
        for(int i = 0; i < N; i++)
            sum_of_values += values[i];

        for(int i = 0; i < N; i++){
            if(sum_of_values != 0.0) 
                relative_values[i] = values[i]/sum_of_values;
            else 
                relative_values[i] = 0.0;
        }
        return relative_values;
    }

    /*
     * Evaluate the fitness of N genomes 
     * for the application of Statistical GA
     * @returns an array of N doubles
     */
    public static double[] sgafitnessEvaluation(char[][] genomes){
        double[] v = new double[N];
        v = fitnessEvaluation(genomes);
        double[] sga_fitness = new double[N];
        
        double mean = sum(v)/N;
        double min = minimum(v);
        
        for(int i = 0; i < N; i++)
            sga_fitness[i] = v[i] + mean + min; 

        return sga_fitness;
    }

    // Find best genome in population
    public static char[] bestGenome(char[][] population, double[] population_fitness){
        char[] best_genome = new char[L];
        double best_fitness = population_fitness[0];
        int index_of_best_genome = 0;
        int flag = 0;

        // Find minimum fitness and the index of the genome that reachs it
        for(int i = 0; i < N; i++){
            if(population_fitness[i] < best_fitness){
                best_fitness = population_fitness[i];
                index_of_best_genome = i;
                flag += 1; // Marks if not all fitness values are the same
            }
        }
        // Pick best genome
        if(0 < flag){
            for(int j = 0; j < L; j++)
               best_genome[j] = population[index_of_best_genome][j];
        }
        // If all fitness values were equal, pick best_genome randomly
        else{
            index_of_best_genome = (int) (N * Math.random());
            for(int j = 0; j < L; j++)
                best_genome[j] = population[index_of_best_genome][j];
        }
    return best_genome;
    }

    // Hard copy of arr1 into arr2
    public static void hardcopy(char[][] array1, char[][] array2){
        for(int i = 0; i < array1.length; i++)
            for(int j = 0; j < array1[0].length; j++)
                array2[i][j] = array1[i][j];
    }

    // Sum of the values in an array
    public static double sum(double[] arr){
        double total = 0.0;
        for(int i = 0; i < arr.length; i++)
            total += arr[i];
        return total;
    }

    // Maximum of array
    public static double maximum(double[] arr){
        double max = arr[0];
        for(int i = 1; i < arr.length; i++)
            if(max < arr[i])
                max = arr[i];
        return max;
    }

    // Minimum of array
    public static double minimum(double[] arr){
        double min = arr[0];
        for(int i = 1; i < arr.length; i++)
            if(arr[i] < min)
                min = arr[i];
        return min;
    }

    public static void main(String[] args){
        System.out.println();
    }
}
