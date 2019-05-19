/*
 * Implementation of auxiliary routines
 * @author Erick García Ramírez
 *
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
public class Aux{
    public static int W = 160; // No of entries for an individual, labels of clustering
    public static int L = W; // Length of genome

    public static double[][] data;
    public static int data_size;

    public Aux(double[][] data){
        this.data = data;
        this.data_size = data.length;
    }

    // Find clustering for given individual
    public static double[][] clustering(double[] individual) {
        double[][] clusters = new double[3][data_size];
        for(int k = 0; k < 3; k++)
            for(int i = 0; i < data_size; i++)
                clusters[k][i] = -1;

        for(int i = 0; i < data_size; i++) {
            if(individual[i] == 0) 
                clusters[0][i] = 1;
            if(individual[i] == 1) 
                clusters[1][i] = 1;
            if(individual[i] == 2)
                clusters[2][i] = 1;
        }
        return clusters;
    }
    
    // Individual to genome
    public static char[] individual_to_genome(double[] individual) {
        char[] genome = new char[L];
        char[] temp = new char[28];
        for(int i = 0; i < W; i++) {
            temp = double_to_28bits(individual[i]);  
            for(int k = 0; k < 28; k++)
                genome[28*i+k] = temp[k];
        }
        return genome;
    }

    // Genome to individual
    public static double[] genome_to_individual(char[] genome) {
        double di;
        double[] coord = new double[W];
        for(int i = 0; i < W; i++){ 
            di = 0.0;
            for(int k = 0; k < 28; k++)
                if(genome[28*i + k] ==  '1')
                    di += Math.pow(2, 27-k);
            coord[i] = di/(Math.pow(2,28)-1);
        }
        return coord;
    }

    // Individuals to genomes
    public static char[][] individuals_to_genomes(double[][] individuals) {
        int n = individuals.length;
        int l = individuals[0].length;
        char[][] genomes = new char[n][l];

        for(int i = 0; i < n; i++)
            genomes[i] = individual_to_genome(individuals[i]);
        return genomes;
    }
    // Genomes to individuals
    public static double[][] genomes_to_individuals(char[][] genomes) {
        int n = genomes.length;
        int w = (int) genomes[0].length/28;
        double[][] individuals = new double[n][w];

        for(int i = 0; i < n; i++)
            individuals[i] = genome_to_individual(genomes[i]);
        return individuals;
    }
    /*
    * Encode a double in [0,1] as a 28-chars array
    * no sign, no integer part, all 28 bits for decimal part
    */
    public static char[] double_to_28bits(double w){
        char[] str = new char[28];
        int decimal_part = (int) (w *(Math.pow(2,28)-1));
        for(int k = 0; k < 28; k++) {
            if(decimal_part % 2 == 0) str[27-k] = '0'; 
            else str[27-k] = '1';
            decimal_part = (int) decimal_part/2;
        }
        return str;
    }//END of double_to_28bits

    // Decode an array of 28 bits to the double in [0,1] it encodes
    public static double bits_to_double(char[] bits) {
        double d = 0.0; 
            for(int k = 0; k < 28; k++){
                if(bits[k] ==  '1') 
                    d += Math.pow(2, 27-k);
            }
        return d/(Math.pow(2,28)-1);
    }

    public static double distance(double[] p, double[] q) {
        double dist_pq = 0.0;
        for(int i = 0; i < p.length; i++)
            dist_pq += Math.pow(p[i]-q[i],2);
        return Math.sqrt(dist_pq);
    }

    /* Calculate all distances between each pair
     * of points in a set */
    public static double[][] distances(double[] points) {
        double[][] dists = new double[data_size][data_size];
        for(int i = 0; i < data_size; i++) 
            for(int j = i; j < data_size; j++) {
                if(points[i] == 1 && points[j] ==1) {
                    dists[i][j] = distance(data[i], data[j]);
                    dists[j][i] = dists[i][j];
                }
                else { dists[i][j] = -1; dists[j][i] = -1;}
            }
        return dists;
    }

    // Calculate v(cluster)
    public static double v(double[] cluster) {
        double[][] dists = distances(cluster);
        double v_of_cluster = 0.0;
        double avgmind = dminbar(cluster, dists);

        for(int i = 0; i < data_size; i++)
            if(cluster[i] == 1)
                v_of_cluster += Math.pow(dmin(i,dists)-avgmind, 2);
        return v_of_cluster/(data_size - 1);
    }

    public static double dminbar(double[] cluster, double[][] dists) {
        double avg = 0.0;
        for(int i = 0; i < data_size; i++)
            if(cluster[i] == 1)
                avg += dmin(i, dists);
        return avg/data_size;
    }

    // dmin(index of point, cluster)
    public static double dmin(int i, double[][] dists) {
        int count = 0;
        while(count < data_size && dists[i][count] <= 0) count++;
        if(count == data_size) return 0.0;
        double mindist = dists[i][count];
        for(int j = count; j < data_size; j++)
            if(0 < dists[i][j] && dists[i][j] < mindist)
                mindist = dists[i][j];
        return mindist;
    }

    // Calculate VNND of a clustering; clusters[3][data_size]
    public static double vnnd(double[][] clusters) {
        double s = 0.0;
        int nc = clusters.length; // nc = 3
        for(int k = 0; k < nc; k++) {
            s += v(clusters[k]);
            //System.out.println(v(clusters[k])+" s "+ s);
        }
        return s;
    }
    
    public static double individualFitness(double[] individual) {
        return vnnd(clustering(individual)); 
    }
    
    // Calculate fitness of genome under VNND
    //public static double genomeFitness(char[] genome) {
    //    return individualFitness(genome_to_individual(genome));
    //}

    // Calculate the fitness of an array of individuals 
    public static double[] individualsFitness(double[][] individuals){
        int n = individuals.length; 
        double[] values = new double[n];
        for(int i = 0; i < n; i++)
                values[i] = individualFitness(individuals[i]);
        return values;
    }

    public static double[] best(double[][] population, double[] fitness){
        int n = population.length;
        int l = population[0].length;
        double[] best = new double[l];

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

    /*
     * Copies by value arr1 into arr2
     */
    public static void hardcopy(double[][] arr1, double[][] arr2){
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
        double[][] temp = new double[160][13];
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

        double[] loner = new double[L];
        double rand;
        for(int j = 0; j < L; j++) {
            rand = Math.random();
            if(rand <=  0.33333)
                loner[j] = 0;
            if(0.33333 < rand && rand <= 0.66666)
                loner[j] = 1;
            if(0.6666 < rand)
                loner[j] = 2;
            System.out.print(loner[j]+" ");
        }
        System.out.println();

        Aux aux = new Aux(data);
        double[][] clusters = clustering(loner);
        int counter = 0;
        System.out.println(clusters.length+" "+ clusters[0].length);
        for(int k = 0; k < 3; k++) {
            for(int i = 0; i < data_size; i ++)
                if(clusters[k][i] == 1) {
                    System.out.print(i+" ");
                    counter += 1;
                }
            System.out.println("\n");
        }
        for(int k=0;k<3;k++){
            for(int i = 0; i < clusters[k].length; i++)
                System.out.print(clusters[k][i]+" ");
            System.out.println();
        }

        System.out.println(v(clusters[0]));
        System.out.println(v(clusters[1]));
        System.out.println(v(clusters[2]));
        System.out.println(vnnd(clusters));
    }
}
