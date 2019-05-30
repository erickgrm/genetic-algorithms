/*
 * Implementation of VNND validation index
 * and auxiliary routines for EGA
 * @author Erick García Ramírez
 * @date May 2019
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import java.io.DataOutputStream;
import java.io.FileOutputStream;

public class AuxAGKNN{
    public static int I = 13;   // Dimension
    public static int W = I*3; // Number of coordinates for centres
    public static int L = W*28; // Length of genome
    public static double[][] data;
    public static int data_size;

    public AuxAGKNN(double[][] data){
        this.data = data;
        this.data_size = data.length;
    }


    public static double[] genome_to_individual(char[] genome) {
        double[] individual = new double[W];
        char[] bits = new char[28];
        for(int j = 0; j < W; j++) {
            for(int l = 0; l < 28; l++)
                bits[l] = genome[j*28 + l];
            individual[j] = bits28_to_double(bits);
        }
        return individual;
    }

    public static char[] individual_to_genome(char[] individual) {
        char[] genome = new char[L];
        char[] bits = new char[28];
        for(int i = 0; i < W; i++) { 
            bits = double_to_28bits(individual[i]);
            for(int l = 0; l < 28; l++)
                genome[W*28 + l] = bits[l];
        }
        return genome;
    }

    public static double bits28_to_double(char[] bits) {
        double d=0.0;
        for(int l = 0; l < 28; l++) 
            if(bits[l] == '1') d += Math.pow(2, 27-l);
        return d/(Math.pow(2,28)-1);
    }

    public static char[] double_to_28bits(double d) {
        char[] bits = new char[28];
        int intd = (int) (d*(Math.pow(2,28)-1));
        for(int l = 0; l < 28; l++) {
            if(intd % 2 == 0) 
                bits[27-l] = '0';
            else 
                bits[27-l] = '1';
            intd = (int) intd/2;
        }
        return bits;
    }

    // Find clustering for given individual
    public static double[][] clustering(double[] individual) {
        double[][] dists = new double[data_size][3];
        dists = distances_to_individual(individual);
        double[][] clusters = new double[3][data_size];
        for(int k = 0; k < 3; k++)
            for(int i = 0; i < data_size; i++)
                clusters[k][i] = 0;
        for(int i = 0; i < data_size; i++) {
            if(dists[i][0] <= dists[i][1]) {
                if(dists[i][0] <= dists[i][2])
                    clusters[0][i] = 1;
                else
                    clusters[2][i] = 1;
            }
            else {
                if(dists[i][1] <= dists[i][2])
                    clusters[1][i] = 1;
                else
                    clusters[2][i] = 1;
            }
        }
        return clusters;
    }
    
    public static double distance(double[] p, double[] q) {
        double dist_pq = 0.0;
        for(int j = 0; j < p.length; j++)
            dist_pq += Math.pow(p[j] - q[j], 2);
        return Math.sqrt(dist_pq);
    }

    // Distances from the data points to an individual
    public static double[][] distances_to_individual(double[] individual) {
        double[][] dists = new double[data_size][3];
        double[] centre0 = new double[I];
        for(int j = 0; j < I; j++)
            centre0[j] = individual[j];
        double[] centre1 = new double[I];
        for(int j = 0; j < I; j++)
            centre1[j] = individual[I+j];
        double[] centre2 = new double[I];
        for(int j = 0; j < I; j++)
            centre2[j] = individual[2*I+j];

        for(int i = 0; i < data_size; i++) { 
                dists[i][0] = distance(data[i], centre0); 
                dists[i][1] = distance(data[i], centre1); 
                dists[i][2] = distance(data[i], centre2); 
        }
        return dists;
    }

    public static double ssqe(double[] individual) {
        double[][] dists = distances_to_individual(individual);
        double[][] clusters = clustering(individual);
        double sum = 0.0;
        for(int k = 0; k < 3; k++)
            for(int i = 0; i < data_size; i++)
                if(clusters[k][i] == 1)
                    sum += Math.pow(dists[i][k], 2);
        return sum;
    }

    
    public static double individualFitness(double[] individual) {
        return ssqe(individual); 
    }

    public static double genomeFitness(char[] genome) {
        return individualFitness(genome_to_individual(genome)); 
    }
    
    // Calculate the fitness of an array of genomes 
    public static double[] genomesFitness(char[][] genomes){
        int n = genomes.length; 
        double[] values = new double[n];
        for(int i = 0; i < n; i++)
                values[i] = genomeFitness(genomes[i]);
        return values;
    }

    public static char[] best(char[][] population, double[] fitness){
        int n = population.length;
        int l = population[0].length;
        char[] best = new char[l];

        int index = 0;
        double min = fitness[0];
        for(int i = 0; i < n; i++){
            if(fitness[i] < min){
                min = fitness[i];
                index = i;
            }
        }
    return population[index];
    }

    // Copies by value arr1 into arr2
    public static void hardcopy(char[][] arr1, char[][] arr2){
        for(int i = 0; i < arr1.length; i++)
            for(int j = 0; j < arr1[0].length; j++)
                arr2[i][j] = arr1[i][j];
    }

    // @returns S the sum of the values in an array
    public static double sum(double[] arr){
        double total = 0.0;
        for(int i = 0; i < arr.length; i++)
            total += arr[i];
        return total;
    }
   
    // @returns maximum of array
    public static double max(double[] arr){
        double max = arr[0];
        for(int i = 0; i < arr.length; i++)
            if(max < arr[i])
                max = arr[i];
        return max;
    }
    public static double min(double[] arr){
        double min = arr[0];
        for(int i = 0; i < arr.length; i++)
            if(arr[i] < min)
                min = arr[i];
        return min;
    }

    public static double pmin(double[] arr, int j){
        int first = 0;
        while(first < arr.length && arr[first] <= 0) first += 1;
        if(first == arr.length) return 0.0;
        double min = arr[first];
        for(int i = first; i < arr.length; i++)
            if(arr[i] != -1 && arr[i] < min && i != j)
                min = arr[i];
        return min;
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

        double[] loner = new double[W];
        double rand;
        for(int j = 0; j < W; j++) {
            loner[j] = Math.random();
            System.out.print(loner[j]+" ");
        }
        System.out.println();


        AuxAGKNN aux = new AuxAGKNN(data);
        double[][] clusters = aux.clustering(loner);
        int counter = 0;

        for(int k = 0; k < 3; k++) {
            for(int i = 0; i < data_size; i ++)
                System.out.print(clusters[k][i]+" ");
            System.out.println("\n");
        }


            System.out.println("\n");
    }
}
