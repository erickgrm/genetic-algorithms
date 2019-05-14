/*
 * Implementation of auxiliary routines
 * @author Erick García Ramírez
 *
 */
public class Aux{
    public static int I = 13; // No of coordinates 
    public static int W = I*3; // No of entries for an individual
    public static int L = W*28; // Length of genome

    public static double[][] data;
    public static int N; // Population size

    public Aux(double[][] data, int N){
        this.data = data;
        this.N = N;
    }

    // Find clustering for given individual
    public static double[][][] clustering(double[][] data, double[] individual) {
        int n = data.length; // 160 or less
        double[][] ds = distances_to_centres(data, individual);
        double[][][] temp = new double[3][n][W];
        int c0 = 0; int c1 = 0; int c2 = 0;

        for(int i = 0; i < n; i++){
            if(min(ds[i]) == ds[i][0]) {
               temp[0][c0] = data[i];
               c0 += 1;
            }
            if(min(ds[i]) == ds[i][1]) {
               temp[1][c1] = data[i];
               c1 += 1;
            }
            if(min(ds[i]) == ds[i][2]) {
               temp[2][c2] = data[i];
               c2 += 1;
            }
        }
        return temp;
    }

    
    
    /* Calculate distances of points 
     * to centres */
    public static double[][] distances_to_centres(double[][] points, double[] individual) {
        int no_of_points = points.length;
        double[][] distances = new double[160][3];
        double d; 
        for(int i = 0; i < no_of_points; i++) 
            for(int j = 1; j < 3; j++) {
                d = 0.0;
                for(int k = 0; k < 13; k++)
                    d += Math.pow(individual[13*j+k]-points[i][k], 2);
                distances[i][j] = Math.sqrt(d);
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
    public static double[][] distances(double[][] points) {
        int n = points.length;
        double[][] distances = new double[n][n];
        double d; 
        for(int i = 0; i < n; i++) 
            for(int j = 1; j < n; j++) {
                distances[i][j] = distance(points[i], points[j]);
            }
        return distances;
    }

    // Calculate V(C)
    public static double v(double[][] cluster) {
        int n = cluster.length;
        double[] ds = distances(cluster);
        double v_of_cluster = 0.0;
        double avgmind = average_min_distance(ds);

        for(int i = 0; i < n; i++)
            v_of_cluster += Math.pow(pmin(ds[i], i)-avgmind[i], 2);
        return v_of_cluster/(n-1);
    }

    public static double[] average_min_distances(double[][] ds) {
        int n = ds.length;
        double[] mins = new double[n];
        double avg = 0.0;

        for(int i = 0; i < n; i++)
            avg += pmin(ds[i], i);
        return avg/n;
    }

    // Calculate VNND of a clustering
    public static double vnnd(double[][][] clusters) {
        double s = 0.0;
        int nc = clusters.length;
        for(int k = 0, k < nc; k++){
            s += v(clusters[k]);
        }
        return s;
    }
    
    public static double individualFitness(double[] individual) {
        return vnnd(clustering(individual); 
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
            }
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
            if(arr[i] < min && i != j)
                min = arr[i];
        return min;
    }

    public static void main(String[] args){

        char[] bits = double_to_28bits(.1);
        System.out.println(bits);
        System.out.println(bits_to_double(bits));
        System.out.println(double_to_28bits(bits_to_double(bits)));
    }


}
