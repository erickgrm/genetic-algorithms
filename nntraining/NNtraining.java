/*
 * Implementation of neural network training through a genetic algorithm 
 *@author Erick García Ramírez
 * Algoritmos Genéticos, MCIC 2019-2
 */


/* We are given the model of a neural network with: 
 * 13 + 1 initial neurons
 * 3 hidden neurons
 * 1 exit neuron 
 * If we feed x to the network, at the exit neuron we get a 
 * value y in [0,1] s.t. the classification is as follows:
 * if y <= 0.25, x is of class 1
 * if 0 < 0.25 <= 0.75, x is of class 2
 * if 0.75 < y, x is of class 3
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

public class NNtraining{
    // Neural network parameters and training data
    public static int errtype;
    public static double[][] data;
    //public final initial; // Number of explicative variables
    //public final hidden; // Number of hidden neurons

    // Population parameters
    public static int N; // size of population
    public static int L; // length of genome

    // Other
    public static char[][] population;
    public static double[] fitness;
    public static double[] relFitness;
    public static double[] bitsProbabilities;

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
     * Calculate the probability of each bit
     */
    public static void calcBitsProbabilities(){
        for(int k = 0; k < L; k++){
            bitsProbabilities[k] = 0.0;
        }

        for(int k = 0; k < L; k++){
            for(int i = 0; i < N; i++){
                if(population[i][k] == '1')
                bitsProbabilities[k] += relFitness[i];
            }
        }
    }

    /*
     * Generate the new population using the probabilities calculated for each bit
     */
    public static void generateNewPopulation(){
        // Find best before changing the whole population
        char[] oldBest = new char[L];
        oldBest = NNbase.best(population, fitness);
    
        // Generate new population bit by bit
        double d = 0.0;

        for(int i = 0; i < N; i++){
            for(int j = 0; j < L; j++){
                
                d = Math.random();
                // Select bit
                if(0.005 < Math.random()){
                    if(bitsProbabilities[j] < d)
                        population[i][j] = '0';
                    else 
                        population[i][j] = '1';
                }
                else{
                    if(bitsProbabilities[j] < d)
                        population[i][j] = '1';
                    else 
                        population[i][j] = '0';
                }
            }
        }
        // Find index of worst individual in population
        double[] tempFitness = new double[N];
        tempFitness = NNbase.sgafitnessEvaluation(population); 
        double min = 0.0;
        int index = 0;
        for(int i = 0; i< N; i++){
            if(tempFitness[i] < min){
                min = tempFitness[i];
                index = i;
            }
        }
        // Swap worst in population with oldBest, generating new population
        for(int j = 0; j < L; j++)
            population[index][j] = oldBest[j];
    }

    /*
     * MAIN method
     * Creat a new object of class NNtraining
     */
    public static double NNtraining(int N, int G, double[][] data, int errtype){

       NNtraining.data = data; 
       NNtraining.N = N;
       NNtraining.errtype = errtype;
       fitness = new double[N];
       relFitness = new double[N];
       bitsProbabilities = new double[14*32];

       //Start with random population P(0)
       startPopulation();

       // Generate G generations
       for(int t = 0; t < G; t++){
            // Evaluation of fitness
            fitness = NNbase.sgafitnessEvaluation(population);
            
            // Calculate Relative fitness
            relFitness = NNbase.relFitness(fitness);
            
            // Calculate the probability of each bit
            calcBitsProbabilities(); 

            // Generate individuals
            generateNewPopulation();
       }

       // Calculate fitness of the last generation and return best
       return NNbase.max(NNbase.fitnessEvaluation(population));
    }

    public static void main(String[] args){
        
        // Read original data
        double[][] temp = new double[168][16];
        try {
            File file = new File("mlptrain.csv");

            int row = 0;
            int col = 0;
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = null;

            while((line = br.readLine()) != null && row < 168){
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
            System.out.println(":(");
        }

        // Transform response variable to a number in [0,1] 
        double[][] data = new double[168][14];
        for(int i = 0; i < 168; i++)
            for(int j = 0; j < 13; j++)
                data[i][j] = temp[i][j];
        for(int i = 0; i < 168; i++){
            if(temp[i][13] == 1.0)
                data[i][13] = 0;
            if(temp[i][14] == 1.0)
                data[i][13] = 0.5;
            if(temp[i][15] == 1.0)
                data[i][13] = 1.0;
        }

        //System.out.println(NNtraining(10, 5, data, 2));
        NNbase base = new NNbase();
        
        //for(int j = 0; j < 14; j++)
        //System.out.println(data[0][j]);

        //System.out.println("value is "+data[993][2])
        
        char[] individual  = new char[32*14];
            for(int j = 0; j < 32*14; j++){
                if(Math.random() < 0.5)
                    individual[j] = '1';
                else 
                    individual[j] = '0';
            }
        
        for(int j = 0; j < 32*14; j++)
            System.out.print(individual[j]);
        System.out.println();

        double[] t = new double[14];
        t = NNbase.transform(individual);
        System.out.println(t.length);
        for(int j = 0; j < 14; j++)
            System.out.println(t[j]);
        
        //for(int i = 0; i < 
        //System.out.println();
    
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
