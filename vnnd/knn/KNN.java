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
    public static int N;

    // Initialise centres
    public static double[][] startingCentres() {
        double[][] centres = new double[K][D];
        for(int k = 0; k < K; k++)
            for(int i = 0; i < D; i++)
                centres[k][i] = Math.random();
        return centres;
    }

    // dists is an arrayy of N x K
    public static double[][] newCentres(double[][] dists) {
        double[][] centres = new double[K][D];
        double jmean;
        int counter;
        for(int k = 0; k < K; k++)
            for(int j = 0; j < D; j++) {
                jmean = 0.0; counter = 0;
                for(int i = 0; i < N; i++) 
                    if(0 < dists[i][k]) {
                        jmean += dists[i][k];
                        counter += 1;
                    }
                centres[k][j] = jmean/counter;
            }
        return centres;
    }

    public static double[][] clustering(double[][] centres) {
        double[][] clusters = new double[N][K];
        for(int i = 0; i < N; i++)
            for(int k = 0; k < K; k++)
                clusters[i][k] = 0;
        double[][] dists = distances(centres); // dists[N][K]
        for(int i = 0; i < N; i++) {
            if(dists[i][0] <= dists[i][1] && dists[i][0] <= dists[i][2])
                clusters[i][0] = 1;
            if(dists[i][1] <= dists[i][0] && dists[i][1] <= dists[i][2])
                clusters[i][1] = 1;
            if(dists[i][2] <= dists[i][0] && dists[i][2] <= dists[i][1])
                clusters[i][2] = 1;
        }
        return clusters;
    }

    public static double[][] distances(double[][] centres) {
        double[][] dists = new double[N][K];
        for(int i = 0; i < N; i++)
            for(int k = 0; k < K; k++)
                dists[i][k] = distance(training_data[i], centres[k]);
        return dists;
    }
    
    public static double distance(double[] p, double[] q) {
        double d = 0.0;
        for(int j = 0; j < p.length; j++)
            d += Math.pow(p[j]-q[j], 2);
        return Math.sqrt(d);
    }

    // KNN main routine
    public static double[][] KNN(double[][] training_data, int K, int G){
        KNN.training_data = training_data;
        KNN.K = K; 
        KNN.N = training_data.length; 
        KNN.D = training_data[0].length;
        
        double[][] centres = startingCentres();
        double[][] clusters = new double[N][K]; 
        double[][] dists new double[N][K];
        for(int g = 0; g < G; g++) 
            centres = newCentres(distances(centres));
        return centres;
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
        double[][] data = new double[160][13];
        for(int i = 0; i < 160; i++)
            for(int j = 0; j < 13; j++)
                data[i][j] = temp[i][j];

       double[][] training_data = data;
       double[][] test_data = data; // No split yet
       double[][] centres = KNN(training_data, 3, 1);
       for(int i = 0; i < 3; i++) {
           for(int j = 0; j < 13; j++)
               System.out.print(centres[i][j]+" ");
           System.out.println();
       }
    }
}
