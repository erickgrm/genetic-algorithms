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
    public static int L; // Length of genome
    public static double[][] data;
    public static int data_size;
    public Aux(double[][] data){
        this.data = data;
        this.data_size = data.length;
        this.L = data_size;
    }

    // Find clustering for given individual
    public static double[][] clustering(double[] individual) {
        double[][] clusters = new double[3][data_size];
        for(int k = 0; k < 3; k++)
            for(int i = 0; i < data_size; i++)
                clusters[k][i] = 0;
        System.out.println(individual.length);
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
    
    public static double distance(double[] p, double[] q) {
        double dist_pq = 0.0;
        for(int j = 0; j < p.length; j++)
            dist_pq += Math.pow(p[j]-q[j],2);
        return dist_pq;
    }

    // Distances between each pair of points in a set
    public static double[][] distances(double[] points) {
        double[][] dists = new double[data_size][data_size];
        for(int i = 0; i < data_size; i++) 
            for(int j = i; j < data_size; j++) {
                if(points[i] == 1 && points[j] ==1) {
                    dists[i][j] = distance(data[i], data[j]);
                    dists[j][i] = dists[i][j];
                }
                else {dists[i][j] = -1; dists[j][i] = -1;}
            }
        return dists;
    }

    // Calculate v(cluster)
    public static double v(double[] cluster) {
        double v_of_cluster = 0.0;
        double[] ds = dmins(cluster);
        double avgmin = dmean(ds);
        for(int i = 0; i < data_size; i++)
            if(cluster[i] == 1)
                v_of_cluster += Math.pow(ds[i] - avgmin, 2);
        return v_of_cluster/(data_size - 1);
    }

    public static double[] dmins(double[] cluster) {
        double[][] dists = distances(cluster);
        double[] ds = new double[data_size];
        for(int i = 0; i < data_size; i++) {
            if(cluster[i] == 1)
                ds[i] = dmin(i, dists);
            else 
                ds[i] = 0;
        }
        return ds;
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

    public static double dmean(double[] array) { 
        double avg = 0.0;
        int c = 0;
        for(int i = 0; i < array.length; i++)
            if(0 < array[i]) {
                avg += array[i];
                c += 1;
            }
        if(c == 0) return 0; 
        else return avg/c;
    }
    
    public static double individualFitness(double[] individual) {
        return vnnd(clustering(individual)); 
    }
    
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

        double[] loner = new double[160];
        double rand;
        for(int j = 0; j < 160; j++) {
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
        double[][] clusters = aux.clustering(loner);
        int counter = 0;
        for(int k = 0; k < 3; k++) {
            for(int i = 0; i < data_size; i ++)
                if(clusters[k][i] == 1) {
                    System.out.print(i+" ");
                    counter += 1;
                }
            System.out.println("\n");
        }

        System.out.println(v(clusters[0]));
        System.out.println(v(clusters[1]));
        System.out.println(v(clusters[2]));
        System.out.println(vnnd(clusters));
    }
}
