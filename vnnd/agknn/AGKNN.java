/*
 * Implementation of Eclectic Genetic Algorithm (AGKNN)
 *@author Erick García Ramírez
*/
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import java.io.DataOutputStream;
import java.io.FileOutputStream;

public class AGKNN{
    // Population parameters
    public static int N; // size of population
    public static int W = 3*13; // Dimension
    public static int L = 28*W; // Length of genome
    public static char[][] population;
    public static char[][] tempPopulation; 
    public static double pm; // Mutation probability
    public static int nm;

    //  Initialise a random population of size N
    public static void startPopulation(){
        population = new char[N][L];
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < L; j++) {
                if(Math.random() < 0.5)
                    population[i][j] = '0';
                else
                    population[i][j] = '1';
            }
        }
    }

    // Order genomes according to fitness
    public static char[][] orderByFitness(char[][] genomes){
        int n = genomes.length;
        int l = genomes[0].length;
        double temp;
        char swp;
        double[] fit = AuxAGKNN.genomesFitness(genomes);

        for(int r = 0; r < n; r++){
            for(int i = 0; i < n-1; i++){
                if(fit[i+1] < fit[i]){
                    temp = fit[i];
                    fit[i] =  fit[i+1];
                    fit[i+1] = temp;
                    // Swap corresponding genomes
                    for(int j = 0; j < l; j++){
                        swp = genomes[i][j];
                        genomes[i][j] = genomes[i+1][j];
                        genomes[i+1][j] = swp;
                    }
                }
            }
        }
        return genomes;
    }// End of orderByFitness

    // Anular crossover routine
    public static char[][] anularCrossover(char[][] individuals){
        int n = individuals.length;
        int l = individuals[0].length;
        int x;
        int y;
        int swap;
        char[] new1 = new char[l];
        char[] new2 = new char[l];
        // Crossover i and n-i-1
        for(int i = 0; i < (int) n/2; i++){
            // Get starting and finishing positions 
            x = (int) (l*Math.random());
            y = (int) (l*Math.random());
            if (y < x) {swap = x; x = y; y = swap;} 
            // Perform crossing
            for(int j = 0; j < x; j++) {
                new1[j] = individuals[i][j];
                new2[j] = individuals[n-i-1][j];
            }
            for(int j = x; j < y; j++) {
                new1[j] = individuals[n-i-1][j];
                new2[j] = individuals[i][j];
            }
            for(int j = y; j < l; j++) {
                new1[j] = individuals[i][j];
                new2[j] = individuals[n-i-1][j];
            }
            // Copy new individuals 
            for(int j = 0; j < l; j++){
                individuals[i][j] = new1[j];
                individuals[n-i-1][j] = new2[j];
            }
        }
        return individuals;
    }// END of anularCrossover

    // Uniform mutation 
    public static char[][] uniformMutation(char[][] individuals) {
        int n = individuals.length;
        int l = individuals[0].length;
        int index;
        for(int i = 0; i < n; i++) {
                for(int m = 0; m < 54; m++) {
                    index = (int) (l * Math.random());
                    if(individuals[i][index] == '0')
                        individuals[i][index] = '1';
                    else 
                        individuals[i][index] = '0';
                }
        }
        return individuals;
    }// END of uniformMutation

    //  Generate next generation
    public static char[][] generateNewPopulation(char[][] genomes){
        char[][] best = new char[N][L];
        char[][] orderedGenomes = orderByFitness(genomes);
        for(int i = 0; i < N; i++)
            for(int j = 0; j < L; j++)
                best[i][j] = orderedGenomes[i][j];
        return best;
    }

    // Creat a new object of class AGKNN
    public static double[] AGKNN(int N, double[][] training_data, double pm, int G){
       AGKNN.N = N;
       AGKNN.pm = pm;
       tempPopulation = new char[N][L];
       AuxAGKNN aux = new  AuxAGKNN(training_data);
       //Start with random population P(0)
       startPopulation();
       for(int t = 0; t < G; t++){
            AuxAGKNN.hardcopy(population, tempPopulation);
            // Order by fitness
            tempPopulation = orderByFitness(tempPopulation);
            // Deterministic crossover
            tempPopulation = anularCrossover(tempPopulation);
            // Mutation 
            tempPopulation = uniformMutation(tempPopulation);
            // Concatenate old population with tempPopulation
            char[][] temp = new char[2*N][L];
            for(int i = 0; i < N; i++)
                for(int j = 0; j < L; j++)
                    temp[i][j] = population[i][j];
            for(int i = 0; i < N; i++)
                for(int j = 0; j < L; j++)
                    temp[N+i][j] = tempPopulation[i][j];
            // Select N best
            population = generateNewPopulation(temp);
       }
       //System.out.println(aux.genomeFitness(population[0])+" "+aux.genomeFitness(population[N-1]));
       return aux.genome_to_individual(population[0]); // returns the 13*3 coordinates for the best three centres
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

        // Build data array, ignore labels
        double[][] data = new double[160][13];
        for(int i = 0; i < 160; i++)
            for(int j = 0; j < 13; j++)
                data[i][j] = temp[i][j];

        // labels
        double[] labels = new double[160];
        for(int i = 0; i < 160; i++) {
            if(temp[i][13+0] == 1.0) labels[i] = 0.0;
            if(temp[i][13+1] == 1.0) labels[i] = 1.0;
            if(temp[i][13+2] == 1.0) labels[i] = 2.0;
        }
        double[][] training_data = data;
        double[][] test_data = data; // No split yet


        AuxAGKNN aux = new AuxAGKNN(training_data);
        double[] fitted = AGKNN(70, training_data, 0.05, 400);
        double[][] clusters = aux.clustering(fitted);
        for(int i = 0; i < 160; i ++) {
            if(clusters[0][i] == 1)
                System.out.print(0);
            if(clusters[1][i] == 1)
                System.out.print(1);
            if(clusters[2][i] == 1)
                System.out.print(2);
        }
        System.out.println(aux.individualFitness(fitted));

        /*
        double fit = 0;
        double best_s = 1000;
        double s = 0.0;
        double[] best = new double[W];
        int flag = 0;
        for(int h = 0; h < 100; h++){
            fitted = AGKNN(70, training_data, 0.05, 400);
            fit = aux.individualFitness(fitted);
            s += fit;
            if(fit < best_s) {
                best_s = fit;
                best = fitted;
            }
        }
        s /= 100;
        System.out.println("SSQE minima promedio: "+ s);
        System.out.println("SSQE minima: "+ best_s);
        System.out.println("Alcanzada para el etiquetado: ");
        */
    }
}
