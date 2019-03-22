/*
 * Implementation of Eclectic Genetic Algorithm (EGA)
 *@author Erick García Ramírez
 * Algoritmos Genéticos, MCIC 2019-2
 */
public class EGA{

    // Population parameters
    public static int N; // size of population
    public static int L; // length of genome

    // Other
    public static char[][] population;
    public static char[][] tempPopulation; 
    //public static double[] fitness; // length 2*N

    public static double pc;  // Crossover probability
    public static double pm; // Mutation probability

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
    
    /*
     * 1. Order population according to fitness
     */
    public static void orderByFitness(){

    }

    /*
     * 2. Crossover routine, deterministic selection
     */
    public static void anularCrossover(){
        int x;
        int halfring;
        char[] new1 = new char[L];
        char[] new2 = new char[L];
        
        
        for(int i = 0; i < (int) N/2; i++){
            // If probability of population crossover > pc, do the crossover

            if(pc < pcPopulation){
                
                // Crossover i and N-i, generating ndPopulation descendants
                for(int k = 0; k < ndPopulation; k++){
                    // Get length of the half-ring to be exchanged
                    halfring = (int) (L*Math.random());
                    // Get start position for the half-ring
                    x = (int) (L*Math.random());
                    for(int j = 0; j < x; j++)
                        new1[j] = tempPopulation[i][j];
                    for(int j = x; j < halfring; j++)
                        new1[j % L] = tempPopulation[N-i-1][j % L];
                    for(int j = 0; j < x; j++)
                        new2[j] = tempPopulation[N-i-1][j];
                    for(int j = x; j < halfring; j++)
                        new2[j % L] = tempPopulation[i][j % L];

                    // Copy new individuals to tempPopulation
                    for(int j = 0; j < L; j++){
                        tempPopulation[i][j] = new1[j];
                        tempPopulation[N-i-1][j] = new2[j];
                    }
                }
            }
        }
    }

    /* 
     * 3. Mutation routine
     * Mutate the bits of each element in the population after crossover
     * TGA: Keep best of old population
     */
    public static void mutation(){
        double q;

        for(int i=0; i < N; i++){
            for(int j=0; j < L; j++){ 
                q = Math.random();
                // If probability of mutation > q, swap the bit
                if(q < pm){
                    if(tempPopulation[i][j] == '1')
                        tempPopulation[i][j] = '0';
                    else 
                        tempPopulation[i][j] = '1';
                }
                else;
                    // No mutation takes place, leave bits unchanged
            }
        }
    }

     /*
      * 3. Selects P(t+1)
      * Ensure we keep best so far
      */
    public static void survival(){
        char[] oldBest;
        oldBest = new char[L];
        oldBest = Base.best(population, fitness);

        // Find index of worst individual in tempPopulation
        double[] tempFitness = new double[N];
        tempFitness = Base.fitnessEvaluation(tempPopulation); 
        double min = 0;
        int index = 0;
        for(int i = 0; i< N; i++){
            if(tempFitness[i] < min){
                min = tempFitness[i];
                index = i;
            }
        }
        // Swap worst in tempPopulation with oldBest, generating new population
        for(int j = 0; j < L; j++)
            tempPopulation[index][j] = oldBest[j];

        Base.hardcopy(tempPopulation, population);
    }

    /*
     * MAIN method
     * Creat a new object of class TGA
     */
    public static double TGA(int N, int L, double pc, double pm, int G){

       TGA.N = N;
       TGA.L = L;
       TGA.pc = pc;
       TGA.pm = pm;

       //Start with random population P(0)
       startPopulation();

       // Temporary population to move from P(t) to P(t+1)
       tempPopulation = new char[N][L];

       fitness = new double[N];

       for(int t = 0; t < G; t++){
            // Evaluation of fitness
            fitness = Base.fitnessEvaluation(population);

            // Selection for crossover
            crossoverSelection();
           
            // Crossover: Produces a temporary new population tempPopulation
            crossover();

            // Mutation: Produces a temporary new population after reproduction
            mutation();

            // Generate P(t+1) and allocates it to population
            // Previous best is kept
            survival();
       }
       return Base.max(fitness);
    }

    public static void main(String[] args){
        System.out.println(TGA(70, 64, 0.9, 0.05, 500));

        //double sum =0.0;
        //Scanner sc = new Scanner(System.in);
        //int rep = sc.nextInt();

        //for(int i = 0; i < rep;  i++)
        //    sum += TGA(70, 64, 0.9, 0.05, 500);
        //System.out.println(sum/rep);
    }

}
