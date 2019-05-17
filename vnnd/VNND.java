/*
 * Implementation of Eclectic Genetic Algorithm (VNND)
 *@author Erick García Ramírez
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import java.io.DataOutputStream;
import java.io.FileOutputStream;

public class VNND{

    // Population parameters
    public static int N; // size of population
    public static int W = 160; // No of entries for an individual
    public static int L = W*28; // Length of genome
    // Other
    public static char[][] population;
    public static char[][] tempPopulation; 
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

    // Order individuals according to fitness
    public static char[][] orderByFitness(char[][] individuals){
        int n = individuals.length;
        int l = individuals[0].length;
        double temp;
        char swp;
        double[] fit = Aux.genomesFitness(individuals);

        for(int r = 0; r < n; r++){
            for(int i = 0; i < n-1; i++){
                if(fit[i+1] < fit[i]){
                    temp = fit[i];
                    fit[i] =  fit[i+1];
                    fit[i+1] = temp;
                    // Swap corresponding individuals
                    for(int j = 0; j < l; j++){
                        swp = individuals[i][j];
                        individuals[i][j] = individuals[i+1][j];
                        individuals[i+1][j] = swp;
                    }
                }
            }
        }
        return individuals;
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
    public static char[][] uniformMutation(char[][] individuals){
        for(int i=0; i < N; i++){
            for(int j=0; j < L; j++){ 
                // If probability of mutation > random number, swap the bit
                if(Math.random() < pm){
                    if(individuals[i][j] == '1')
                        individuals[i][j] = '0';
                    else 
                        individuals[i][j] = '1';
                }
            }
        }
        return individuals;
    }// END of uniformMutation

     /*
      * Select P(t+1)
      * Ensure we keep the N best so far
      */
    public static char[][] generateNewPopulation(char[][] genomes){
        char[][] best = new char[N][L];
        char[][] orderedIndividuals = orderByFitness(genomes);

        for(int i = 0; i < N; i++)
            for(int j = 0; j < L; j++)
                best[i][j] = orderedIndividuals[i][j];
        return best;
    }

    /*
     * MAIN method
     * Creat a new object of class VNND
     */
    public static double[] VNND(int N, int L, double[][] training_data, double pm, int G){

       VNND.N = N;
       VNND.L = L;
       VNND.pm = pm;
       tempPopulation = new char[N][L];
       Aux aux = new  Aux(training_data);

       //Start with random population P(0)
       startPopulation();

       for(int t = 0; t < G; t++){
            aux.hardcopy(population,tempPopulation);

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
        
       double[] ft = aux.genomesFitness(population);
       for(int i = 0; i < N; i++)
           System.out.println(ft[i]);

       char[] best_genome = new char[L];
       for(int j = 0; j < L; j++)
            best_genome[j] = population[0][j];

       double[] aux_individual = aux.genome_to_individual(best_genome);

       double[] best_individual = new double[W+1];
       for(int i = 0; i < W; i++) {
           if(aux_individual[i] <= 0.25)
               best_individual[i] = 0;
           if(0.25 < aux_individual[i] && aux_individual[i] < 0.75)
               best_individual[i] = 1;
           if(0.75 < aux_individual[i])
               best_individual[i] = 2;
       }
       best_individual[W] = aux.min(aux.genomesFitness(population));

       // Return best individual in last generation
       return best_individual;
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

       double[][] training_data = data;
       double[][] test_data = data; // No split yet

       double[] fitted = VNND(70, L, training_data, 0.1, 100);
       System.out.println("Best fitness = "+fitted[W]+"\nReached for the clustering:");
       System.out.println("Cluster 0");
       for(int i = 0; i < W; i++) {
           if(fitted[i] == 0)
               System.out.print(i+" ");
       }
       System.out.println("Cluster 1");
       for(int i = 0; i < W; i++) {
           if(fitted[i] == 1)
               System.out.print(i+" ");
       }
       System.out.println("Cluster 2");
       for(int i = 0; i < W; i++) {
           if(fitted[i] == 2)
               System.out.print(i+" ");
        }
    }
}
