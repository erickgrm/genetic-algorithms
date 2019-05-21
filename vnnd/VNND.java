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
    public static int L = 160; // Length of genome
    public static double[][] population;
    public static double[][] tempPopulation; 
    public static double pm; // Mutation probability

    //  Initialise a random population of size N
    public static void startPopulation(){
        population = new double[N][L];
        double rand;

        for(int i = 0; i < N; i++){
            for(int j = 0; j < L; j++){
                rand = Math.random();
                if(rand <= 0.3333)
                    population[i][j] = 0;
                if(0.3333 < rand && rand <= 0.6666)
                    population[i][j] = 1;
                if(0.6666 < rand) 
                    population[i][j] = 2;
            }
        }
    }

    // Order individuals according to fitness
    public static double[][] orderByFitness(double[][] individuals){
        int n = individuals.length;
        int l = individuals[0].length;
        double temp;
        double swp;
        double[] fit = Aux.individualsFitness(individuals);

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
    public static double[][] anularCrossover(double[][] individuals){
        int n = individuals.length;
        int l = individuals[0].length;
        int x;
        int y;
        int swap;
        double[] new1 = new double[l];
        double[] new2 = new double[l];
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
    public static double[][] uniformMutation(double[][] individuals){
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < L; j++){ 
                // If probability of mutation > random number, swap the bit
                if(Math.random() < pm) {
                    if(individuals[i][j] == 0) {
                        if(0.5 < Math.random())
                            individuals[i][j] = 1;
                        else 
                            individuals[i][j] = 2;
                    }
                    if(individuals[i][j] == 1) {
                        if(0.5 < Math.random())
                            individuals[i][j] = 0;
                        else 
                            individuals[i][j] = 2;
                    }
                    if(individuals[i][j] == 2) {
                        if(0.5 < Math.random())
                            individuals[i][j] = 0;
                        else 
                            individuals[i][j] = 1;
                    }
                }
            }
        }
        return individuals;
    }// END of uniformMutation

    //  Generate next generation
    public static double[][] generateNewPopulation(double[][] individuals){
        double[][] best = new double[N][L];
        double[][] orderedIndividuals = orderByFitness(individuals);
        for(int i = 0; i < N; i++)
            for(int j = 0; j < L; j++)
                best[i][j] = orderedIndividuals[i][j];
        return best;
    }

    // Creat a new object of class VNND
    public static double[] VNND(int N, int L, double[][] training_data, double pm, int G){
       VNND.N = N;
       VNND.L = L;
       VNND.pm = pm;
       tempPopulation = new double[N][L];
       Aux aux = new  Aux(training_data);
       //Start with random population P(0)
       startPopulation();
       for(int t = 0; t < G; t++){
            Aux.hardcopy(tempPopulation, population);
            // Order by fitness
            tempPopulation = orderByFitness(tempPopulation);
            // Deterministic crossover
            tempPopulation = anularCrossover(tempPopulation);
            // Mutation 
            tempPopulation = uniformMutation(tempPopulation);
            // Concatenate old population with tempPopulation
            double[][] temp = new double[2*N][L];
            for(int i = 0; i < N; i++)
                for(int j = 0; j < L; j++)
                    temp[i][j] = population[i][j];
            for(int i = 0; i < N; i++)
                for(int j = 0; j < L; j++)
                    temp[N+i][j] = tempPopulation[i][j];
            // Select N best
            population = generateNewPopulation(temp);
       }
       return population[0];
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

       Aux aux = new Aux(training_data);
       double[] fitted = VNND(70, L, training_data, 0.05, 200);
       double[][] clusters = Aux.clustering(fitted);
       for(int i = 0; i < L; i++) {
           for(int k = 0; k < 3; k++) 
               System.out.print(clusters[k][i]+" ");
           System.out.println();
       }
    }
}
