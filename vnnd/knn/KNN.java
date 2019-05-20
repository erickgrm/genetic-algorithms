/*
 * Implementation of K Nearest-Neighbours
 *@author Erick García Ramírez
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import java.io.DataOutputStream;
import java.io.FileOutputStream;

public class KNN{
    public static int K; // number of centres
    public static int D; // dimension
    public static int W; // length of centres 
    public static double[][] training_data;
    public static double[][] centres;
    public static int N;

    // Initialise centres
    public static double[][] startingCentres() {
        double[][] centres = new double[K][D];
        //for(int k = 0; k < K; k++)
        //    for(int j = 0; j < D; j++)
        //        centres[k][j] = Math.random();
        //return centres;
        for(int k = 0; k < K; k++)
            centres[k] = training_data[(int) (N*Math.random())];
        return centres;
    }

    public static double[][] newCentres(double[][] centres) {
        double[][] clusters = clustering(centres); 
        double[][] new_centres = new double[K][D];
        int counter;
        for(int k = 0; k < K; k++)
            for(int j = 0; j < D; j++) {
                counter = 0;
                for(int i = 0; i < N; i++) 
                    if(clusters[i][k] == 1) {
                        new_centres[k][j] += distance(training_data[i], centres[k]);
                        counter += 1;
                    }
                new_centres[k][j] += centres[k][j];
                new_centres[k][j] /= (counter + 2);
            }
        return new_centres;
    }

    public static double[][] clustering(double[][] centres) {
        double[][] clusters = new double[N][K];
        for(int i = 0; i < N; i++)
            for(int k = 0; k < K; k++)
                clusters[i][k] = 0;
        double[][] dists = distances(centres); // dists[N][K]
        for(int i = 0; i < N; i++) {
            if(dists[i][0] <= dists[i][1]) {
                if(dists[i][0] <= dists[i][2])
                    clusters[i][0] = 1;
                else 
                    clusters[i][2] = 1;
            } 
            else {
                if(dists[i][1] <= dists[i][2])
                    clusters[i][1] = 1;
                else
                    clusters[i][2] = 1;
            }
        }
        return clusters;
    }

    public static double[][] distances(double[][] centres) {
        double[][] dists = new double[N][K];
        for(int i = 0; i < N; i++) {
            System.out.println("Distances to centres");
            for(int k = 0; k < K; k++) {
                dists[i][k] = distance(training_data[i], centres[k]);
                System.out.print(dists[i][k]+" ");
            }
        }
        return dists;
    }
    
    public static double distance(double[] p, double[] q) {
        if(p.length != q.length) {
            System.out.println("Different size vectors");
            return -1;
        }
        double d = 0.0;
        for(int j = 0; j < p.length; j++)
            d += Math.pow(p[j]-q[j], 2);
        return d;
    }

    // KNN main routine
    public KNN(double[][] training_data, int K) {
        KNN.training_data = training_data;
        KNN.K = K; 
        KNN.N = training_data.length; 
        KNN.D = training_data[0].length;
    }
    public static void fit(int G) {
        double[][] cens = startingCentres();
        for(int g = 0; g < G; g++) 
            cens = newCentres(cens);
        KNN.centres = cens;
    }

    public static void main(String[] args) {
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
        catch(IOException e) {
            System.out.println("Couldn't read file :(");
        }//END read original data

        // Build data array, ignore labels
        double[][] data = new double[10][13];
        for(int i = 0; i < 10; i++)
            for(int j = 0; j < 13; j++)
                data[i][j] = temp[i][j];
        double[][] training_data = data;
        double[][] test_data = data; // No split yet

        KNN knn = new KNN(training_data, 3);
        knn.fit(100);
        double[][] centres = knn.centres;
        for(int k = 0; k < 3; k++) {
            for(int j = 0; j < 13; j++)
                System.out.print(centres[k][j]+" ");
            System.out.println();
        }
        double[][] clusters = clustering(centres);
        System.out.println("Clusters");
        int[] counters = new int[K];
            for(int i = 0; i < 10; i++) {
                for(int k = 0; k < K; k++) {
                    System.out.print(clusters[i][k]+" ");
                }
                System.out.println();
            }
    }
}
