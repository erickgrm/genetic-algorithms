/*
 * Training of a neural network through a genetic algorithm 
 *@author Erick García Ramírez
 * Algoritmos Genéticos, MCIC 2019-2
 */

/* We are given the model of a neural network with: 
 * 13 + 1 initial neurons
 * 3 hidden neurons
 * 1 exit neuron 
 * If we feed x to the network, at the exit neuron we get a 
 * value y in [0,1], and the classification is as follows:
 * if y <= 0.25, x is of class 1
 * if 0.25 < y <= 0.75, x is of class 2
 * if 0.75 < y, x is of class 3
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

public class NNtraining{
    // Neural network parameters and training data
    
    public static int I = 14; // No of entry variables (13 + threshold)
    public static int W = I*3 + 4; // No of weights
    public static int L = W*32; // Length of genome

    // Parameters for Statistical GA
    public static int N ; // size of population
    public static char[][] population;
    public static double[] population_fitness;
    public static double[] relative_fitness;
    public static double[] sga_fitness;
    public static double[] bits_probabilities;

    // Initialise a random population of size N
    public static void starting_population(){
        population = new char[N][L];

        for(int i = 0; i < N; i++){
            for(int j = 0; j < L; j++){
                if(Math.random() < 0.5)
                    population[i][j] = '1';
                else 
                    population[i][j] = '0';
            }
        }
    }//End of starting_population
    
    // Calculate the probability of each bit
    public static void bitsProbabilities(){
        for(int k = 0; k < L; k++)
            bits_probabilities[k] = 0.0;

        for(int k = 0; k < L; k++){
            for(int i = 0; i < N; i++){
                if(population[i][k] == '1')
                    bits_probabilities[k] += relative_fitness[i];
            }
        }
    }//End bitsProbabilities 

    // Generate the new population using the probabilities calculated for each bit
    public static void generateNewPopulation(){
        // Find best genome before changing the whole population
        char[] old_best = new char[L];
        old_best = NNbase.bestGenome(population, population_fitness);
    
        // Generate new population bit by bit
        double d = 0.0;
        for(int i = 0; i < N-1; i++){
            for(int j = 0; j < L; j++){
                d = Math.random();
                // Select bit
                if(0.005 < Math.random()){
                    if(bits_probabilities[j] < d)
                        population[i][j] = '0';
                    else population[i][j] = '1';
                }
                else{
                    if(bits_probabilities[j] < d)
                        population[i][j] = '1';
                    else population[i][j] = '0';
                }
            }
        }
        // Add best individual
        for(int j = 0; j < L; j++)
            population[N-1][j] = old_best[j];
    }

    /*
     * MAIN method
     * Creat a new object of class NNtraining
     */
    public static double[] NNtraining(int N, int G, double[][] training_data, int error_type){

       NNbase aux = new NNbase(training_data, error_type, N);  
       NNtraining.N = N;
       population_fitness = new double[N];
       relative_fitness = new double[N];
       sga_fitness = new double[N];
       bits_probabilities = new double[L];

       // Random starting population
       starting_population();

       // Generate G generations
       for(int t = 0; t < G; t++){
           population_fitness = aux.fitnessEvaluation(population);

           // SGA fitness
           sga_fitness = aux.sgafitnessEvaluation(population_fitness);
           
           // Calculate Relative fitness
           relative_fitness = aux.relativeFitness(sga_fitness);
           
           // Calculate the probability of each bit
           bitsProbabilities(); 

           // Evolve population
           generateNewPopulation();
       }

       // Calculate fitness of the last generation and return best
       population_fitness = aux.fitnessEvaluation(population);
       double[] best_weights = new double[W];
       best_weights = aux.genome_to_weights(aux.bestGenome(population, population_fitness));
       for(int i = 0; i < best_weights.length; i++)
           System.out.print(best_weights[i]+" ");
       System.out.println();
       System.out.println(aux.min(population_fitness));
       return best_weights;
    }

    public static void main(String[] args){
        // Read original data
        double[][] temp = new double[160][16];
        try {
            File file = new File("mlptrain.csv");

            int row = 0;
            int col = 0;
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = null;

            while((line = br.readLine()) != null && row < 160){
                StringTokenizer st=new StringTokenizer(line, ",");
                while(st.hasMoreTokens()){
                    temp[row][col] = Double.parseDouble(st.nextToken());
                    col++;
                }
                col = 0;
                row++;
            }
        }
        catch(IOException e){
            System.out.println("Couldn't read file :(");
        }//END read original data

        // Transform response variable to a number in [0,1] 
        // and build training data
        double[][] training_data = new double[160][14];
        for(int i = 0; i < 160; i++)
            for(int j = 0; j < 13; j++)
                training_data[i][j] = temp[i][j];
        for(int i = 0; i < 160; i++){
            if(temp[i][13] == 1)
                training_data[i][13] = 0;
            if(temp[i][14] == 1)
                training_data[i][13] = 0.5;
            if(temp[i][15] == 1)
                training_data[i][13] = 1.0;
        }
        
       double[] model_weights = new double[W];
       model_weights = NNtraining(50, 500, training_data, 2);
       ModelEvaluation model = new ModelEvaluation(model_weights, training_data, 2);

       double[] predictions = model.predict();
       for(int i = 0; i < predictions.length; i++){
           System.out.print(predictions[i]+" ");
       }
       System.out.println();
       System.out.println(model.sum_of_errors());
       
       // char[] genome  = new char[L];
       //     for(int j = 0; j < L; j++){
       //         if(Math.random() < 0.5)
       //             genome[j] = '1';
       //         else 
       //             genome[j] = '0';
       //     }

       // double[] t = new double[W];
       // t = aux.genome_to_weights(genome);
       // char[] s = aux.weights_to_genome(t);
       // System.out.println();
       // for(int i = 0; i < W; i++){
       //     for(int j = 0; j < 4; j++)
       //         System.out.print(genome[32*i+j]);
       //     System.out.print(" ");
       //     for(int j = 4; j < 32; j++)
       //         System.out.print(genome[32*i+j]);
       //     System.out.println();
       //     System.out.print(t[i]);
       //     System.out.println();
       //     for(int j = 0; j < 4; j++)
       //         System.out.print(s[32*i+j]);
       //     System.out.print(" ");
       //     for(int j = 4; j < 32; j++)
       //         System.out.print(s[32*i+j]);
       //     System.out.println();
       //     System.out.println();
       // }
        // Several runs
        //double  temp;
        //double sum = 0.0;
        //int[] freq = new int[9];
        //for(int i = 0; i < 1000;  i++){
        //    temp = SGA(70, 64, 500);
        //    //System.out.println(temp);
        //    sum += temp;
        //    freq[(int) temp / 8] ++;
        //}
        //    for(int i = 0; i < 9; i++){
        //        System.out.print(i*8);
        //        System.out.print(" = ");
        //        System.out.println(freq[i]);
        //    }
        //    System.out.println(sum/1000);
        
    }
}
