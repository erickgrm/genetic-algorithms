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
    public static int I = 13; // No of coordinates 
    public static int W = I*3; // No of entries for an individual
    public static int L = W*28; // Length of genome

    public static double[][] data;
    public static int data_size;

    public Aux(double[][] data){
        this.data = data;
        this.data_size = data.length;
    }

    // Find clustering for given individual
    public static double[][] clustering(double[] individual) {
        double[] allpoints = new double[data_size];
        for(int i = 0; i < data_size; i++)
            allpoints[i] = 1;
        double[][] ds = distances_to_centres(allpoints, individual);
        double[][] clusters = new double[3][data_size];
        double min_dist;

        for(int k = 0; k < 3; k++)
            for(int i = 0; i < data_size; i++)
                clusters[k][i] = -1;

        for(int i = 0; i < data_size; i++) {
            min_dist = min(ds[i]);
            if(min_dist == ds[i][0]) 
                clusters[0][i] = 1;
            if(min_dist == ds[i][1])
                clusters[1][i] = 1;
            if(min_dist == ds[i][2]) 
                clusters[2][i] = 1;
        }
        return clusters;
    }
    
    /* Calculate distances of points 
     * to centres; points is an array of size data_size x W
     * */
    public static double[][] distances_to_centres(double[] points, double[] individual) {
        double[][] distances = new double[data_size][3];
        for(int i = 0; i < data_size; i++)
            for(int k = 0; k < 3; k++)
                distances[i][k] = -1;
        double d; 
        for(int i = 0; i < data_size; i++) 
            if(points[i] == 1) { 
                for(int k = 0; k < 3; k++) {
                    d = 0.0;
                    for(int j = 0; j < 13; j++)
                        d += Math.pow(individual[13*k + j]-data[i][j], 2);
                    distances[i][k] = Math.sqrt(d);
                }
            }
        return distances;
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
            for(int j = 1; j < data_size; j++) {
                if(points[i] == 1 && points[j] ==1)
                    dists[i][j] = distance(data[i], data[j]);
                else dists[i][j] = -1;
            }
        return dists;
    }

    // Calculate V(cluster)
    public static double v(double[] cluster) {
        double[][] dists = distances(cluster);
        double v_of_cluster = 0.0;
        double avgmind = average_min_distances(dists);

        for(int i = 0; i < data_size; i++)
            if(cluster[i] == 1)
                v_of_cluster += Math.pow(pmin(dists[i], i)-avgmind, 2);
        return v_of_cluster/(data_size - 1);
    }

    public static double average_min_distances(double[][] dists) {
        double avg = 0.0;
        for(int i = 0; i < data_size; i++)
            avg += pmin(dists[i], i);
        return avg/data_size;
    }

    // Calculate VNND of a clustering; clusters[3][data_size]
    public static double vnnd(double[][] clusters) {
        double s = 0.0;
        int nc = clusters.length; // nc = 3
        for(int k = 0; k < nc; k++) {
            s += v(clusters[k]);
        }
        return s;
    }
    
    public static double individualFitness(double[] individual) {
        return vnnd(clustering(individual)); 
    }
    
    // Calculate fitness of genome under VNND
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
        //if(0.0 < max) 
        //    for(int j = 0; j < l; j++)
        //        best[j] = population[index][j];
        //else{
        //    index = (int) (n*Math.random());
        //    for(int j = 0; j < l; j++)
        //        best[j] = population[index][j];
        //}
    return population[index];
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
        double min = arr[0];
        if(j == 0) min = arr[1];
        for(int i = 0; i < arr.length; i++)
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

        Aux aux = new Aux(data);
        double[] centres = {0.7781495033880678, 0.8500137360767042, 0.901166941602405, 0.11713103248600301, 0.8764630886780586, 0.44478505270475543, 0.89940024874881, 0.20558902325328077, 0.2766537043327604, 0.9920679107012894, 0.2907604958517868, 0.3456855019393768, 0.24568478109570138, 
0.04933237675328693, 0.4208864622596147, 0.2928128998458866, 0.5413228926856923, 0.8777654091930591, 0.8736778455737153, 0.729514873510282, 0.6819352309477896, 0.3202703756104051, 0.9710703677351414, 0.978179264732373, 0.7763115606319589, 0.45920246638060536, 
0.08678972008373484, 0.4104675628634824, 0.06659019390713496, 0.30225838088340456, 0.5187515859259352, 0.8213885792396537, 0.7659918135627799, 0.028355784819855484, 0.1250162799843262, 0.5231423732755421, 0.16103678629188534, 0.9436624122547448, 0.3817342198704713};
        //{0.9929757415986648, 0.6392631778093546, 0.6008484758468288, 0.9463797582178554, 0.021111462343899393, 0.45047201756563787, 0.506493156800021, 0.685617699047989, 0.052122965649228414, 0.06022669397378971, 0.12165161267538224, 0.32921591523742644, 0.6871680158643723, 0.2225253292267223, 0.32628691690522027, 0.8518306905471932, 0.3494303164982435, 0.4835894982650485, 0.9096748676511454, 0.8458629356543084, 0.7088079441666899, 0.6059919432028827, 0.9851640760345909, 0.5567341095087458, 0.48016126260221476, 0.500088689849111, 0.8604661407339057, 0.6223279856977164, 0.45974038340054596, 0.5982149079375524, 0.9424724353196935, 0.27538206158348194, 0.9956252351240263, 0.9304358248801374, 0.7445260537584352, 0.5712434968771171, 0.9053938236288496, 0.9523684380664246, 0.8560163820386543};
        System.out.println(data_size);
        double[][] clusters = clustering(centres);
        int counter = 0;
        for(int k = 0; k < 3; k++) {
            for(int i = 0; i < data_size; i ++)
                if(clusters[k][i] == 1) {
                    System.out.print(i+" ");
                    counter += 1;
                }
            System.out.println("\n");
        }
    }
}
