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
    public static int L = W*28; // Length of genome

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
            if(individual[i] <= 0.25) 
                clusters[0][i] = 1;
            if(0.25 < individual[i] && individual[i] < 0.75) 
                clusters[1][i] = 1;
            if(0.75 <= individual[i])
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
            //System.out.println(v(clusters[k])+" s "+ s);
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

        Aux aux = new Aux(data);
       
        char[] loner =  new char[L];
        for(int j = 0; j < L; j++){
                if(Math.random() < 0.5)
                    loner[j] = '1';
                else
                    loner[j] = '0';
            }
        double[] lonerdouble = {0.1589018075127222, 0.01722788817147869, 0.3858882650207291, 0.6309147165377241, 0.866037770606718, 0.7054201539807773, 0.12927951339363872, 0.2477610940030258, 0.5446178858899247, 0.49508409386532043, 0.9254886020924471, 0.7790790415520931, 0.613490181466528, 0.39036093797669164, 0.5120944660607519, 0.2741008448381008, 0.4219300613624232, 0.6421400854071233, 0.47019517969412794, 0.38257085301939714, 0.2201317556952378, 0.7499241931361116, 0.9983684830306786, 0.791831690042584, 0.1044209901408143, 0.32158341751092456, 0.0908840041268021, 0.4905192199741275, 0.7133012999344666, 0.1092820395129995, 0.758730533565322, 0.2621822366944784, 0.3718450940096568, 0.9619070550870413, 0.41583028590615945, 0.5879913106113348, 0.4572287554190634, 0.018296614357443952, 0.4075479038340893, 0.9000924598429071, 0.02432153755546189, 0.5589596836230147, 0.06770221914240054, 0.42049359314327533, 0.8293528662225338, 0.29996045045539904, 0.03139877703561923, 0.0022096261464417954, 0.6183251426306559, 0.7441791398233888, 0.6973733592680594, 0.8529631117469189, 0.20437199698527156, 0.5062100459121542, 0.36583221094992835, 0.6187535025878008, 0.04056961849544055, 0.9980356283412711, 0.46152701773318283, 0.025161355827604814, 0.9471401309487973, 0.9836039468035249, 0.5486777147228931, 0.28584054591447317, 0.05555786213114061, 0.38173682384839963, 0.7873874261505434, 0.16356998742956663, 0.27852502569006765, 0.10695722739010016, 0.2237896294287951, 0.7012461934285097, 0.7545485897159151, 0.8480459483267588, 0.6993994701631348, 0.2589366594662393, 0.3272162352771172, 0.06171611346943719, 0.5893268122871473, 0.5856077320337584, 0.04314548538306909, 0.7875909611120483, 0.9180095602497814, 0.13714139959641322, 0.42471009278561955, 0.7861054494459385, 0.933358005931072, 0.4636156688020217, 0.06115071498286245, 0.3913753121769999, 0.6587591940863401, 0.011921878948516693, 0.9195066873710852, 0.9990914277698525, 0.2288648792686495, 0.7449638014471672, 0.8589259120036882, 0.21576295500905424, 0.2955694321377927, 0.43599654524027015, 0.060702637064094234, 0.9681177808646775, 0.6156051852390363, 0.016515467377437157, 0.851179006886404, 0.797097637493527, 0.7213393178632085, 0.9410667528996868, 0.40034553557763075, 0.9970070160813891, 0.46072945542905275, 0.7682605712423495, 0.25117674190989414, 0.6656885320905169, 0.4240651444497151, 0.625634277707466, 0.5390932393785314, 0.7157293696542433, 0.5305232797955098, 0.170200762041661, 0.3982030056350045, 0.3010891314636511, 0.0924764688777792, 0.5788010343119541, 0.6632866064581521, 0.3417796654320496, 0.6243872814788941, 0.38273015760902374, 0.6617376344715715, 0.7111997817128889, 0.327263881740212, 0.2876917395282229, 0.578808250199289, 0.5857144690517875, 0.31889407455509183, 0.7496039522797017, 0.05122110266693347, 0.2730566757658745, 0.1692734963047262, 0.7575704893379304, 0.27657871051348265, 0.841378218834766, 0.20037316605587738, 0.7199192707237574, 0.39298893285166075, 0.019683975799694567, 0.06482668617675709, 0.44363729448481387, 0.23263748076795593, 0.0351121017154757, 0.04298919455330519, 0.9325394963195156, 0.4932044017806813, 0.45566689020271184, 0.5471977984428323, 0.5776659309032035, 0.1362117384978076, 0.010178603269825143, 0.026862707089121293, 0.6251449906272627};
        /*
        genome_to_individual(loner);
        System.out.println("\n"+lonerdouble.length);
        for(int i = 0; i < data_size; i++)
            System.out.print(lonerdouble[i]+", ");
        
        /*
        char[] loner2 = individual_to_genome(lonerdouble);
        System.out.println("\n"+loner2.length+" " +L);
        for(int i = 0; i < L; i++)
            System.out.print(loner2[i]);
        */
        double[][] clusters = clustering(lonerdouble);
        int counter = 0;
        System.out.println();
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
