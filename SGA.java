/*
 * Implementation of Statistical Genetic Algorithm (SGA o STAUN)
 *@author Erick García Ramírez
 * Algoritmos Genéticos, MCIC 2019-2
 */
public class SGA{

    // Population parameters
    public static int N; // size of population
    public static int L; // length of genome

    // Other
    public static char[][] population;
    public static double[] fitness;
    public static double[] relFitness;
    public static double[] bitsProbabilities;

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

    /*
     * Calculate the probability of each bit
     */
    public static void calcBitsProbabilities(){
        for(int k = 0; k < L; k++){
                bitsProbabilities[k] = 0;
            }

        for(int k = 0; k < L; k++)
            for(int i = 0; i < N; i++){
                bitsProbabilities[k] += relFitness[i]*population[i][k];
            }
    }

    /*
     * Generate the new population using the probabilities calculated for each bit
     */
    public static void generateNewPopulation(){
        double dj = 0.0;
        double dk = 0.0;

        for(int i = 0; i < N; i++){
            for(int j = 0; j < L; j++){
                
                while(dj == 0.0 || dk == 0){
                    dk = Math.random();
                    dj = Math.random();
                }

                // Select bit
                if(0.005 < dj){
                    if(bitsProbabilities[j] < dk)
                        population[i][j] = '0';
                    else 
                        population[i][j] = '1';
                }
                else{
                    if(bitsProbabilities[j] < dk)
                        population[i][j] = '1';
                    else 
                        population[i][j] = '0';
                }
            }
        }
    }

    /*
     * MAIN method
     * Creat a new object of class SGA
     */
    public static double SGA(int N, int L, int G){

       SGA.N = N;
       SGA.L = L;

       //Start with random population P(0)
       startPopulation();

       fitness = new double[N];
       relFitness = new double[N];
       bitsProbabilities = new double[L];

       for(int t = 0; t < G; t++){
            // Evaluation of fitness
            fitness = Base.fitnessEvaluation(population);

            // Calculate Relative fitness
            relFitness = Base.relFitness(fitness);
            
            // Calculate the probability of each bit
            calcBitsProbabilities(); 

            // Generate individuals
            generateNewPopulation();
       }
       // Calculate fitness of the last generation
       fitness = Base.fitnessEvaluation(population);

       return Base.max(fitness);
    }

    public static void main(String[] args){
        System.out.println(SGA(70, 64, 500));

        double sum = 0.0;
        //Scanner sc = new Scanner(System.in);
        //int rep = sc.nextInt();

       // for(int i = 0; i < 1000;  i++){
       //     //System.out.println(SGA(70,64,500));
       //     sum += SGA(70, 64, 500);
       // }
       //     System.out.println(sum/1000);
        
    }

}
