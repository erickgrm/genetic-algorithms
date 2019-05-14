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
     * Order individuals according to fitness
     */
    public static char[][] orderByFitness(char[][] individuals){
        int n = individuals.length;
        int l = individuals[0].length;
        double temp;
        char swp;
        double[] fit = Base.fitnessEvaluation(individuals);

        for(int r = 0; r < n; r++){
            for(int i = 0; i < n-1; i++){
                if(fit[i] < fit[i+1]){
                    temp = fit[i];
                    fit[i] =  fit[i+1];
                    fit[i+1] = temp;
                    // Swap corresponding individuals
                    for(int j = 0; j < l; j++){
                        swp = individuals[i][j];
                        individuals[i][j] = individuals[i+1][j];
                        individuals[i+1][j] = swp;
                    }
                }
            }
        }//END of r for 
        return individuals;
    }

    /*
     * Anular crossover routine, deterministic selection
     */
    public static char[][] detCrossover(char[][] individuals){
        int x;
        int halfring;
        char[] new1 = new char[L];
        char[] new2 = new char[L];
        
        // Crossover i and N-i-1
        for(int i = 0; i < (int) N/2; i++){
            // Length of the half-ring to be exchanged
            halfring = (int) (L*Math.random());
            // Get start position for the half-ring
            x = (int) (L*Math.random());
            for(int j = 0; j < x; j++)
                new1[j] = individuals[i][j];
            for(int j = x; j < halfring; j++)
                new1[j % L] = individuals[N-i-1][j % L];
            for(int j = 0; j < x; j++)
                new2[j] = individuals[N-i-1][j];
            for(int j = x; j < halfring; j++)
                new2[j % L] = individuals[i][j % L];

            // Copy new individuals 
            for(int j = 0; j < L; j++){
                individuals[i][j] = new1[j];
                individuals[N-i-1][j] = new2[j];
            }
        }
        return individuals;
    }

    /* 
     * Uniform mutation 
     */
    public static char[][] mutation(char[][] individuals){

        for(int i=0; i < N; i++){
            for(int j=0; j < L; j++){ 
                // If probability of mutation > random number, swap the bit
                if(Math.random() < pm){
                    if(individuals[i][j] == '1')
                        individuals[i][j] = '0';
                    else 
                        individuals[i][j] = '1';
                }
            }
        }
        return individuals;
    }

     /*
      * Select P(t+1)
      * Ensure we keep the N best so far
      */
    public static char[][] generateNewPopulation(char[][] individuals){
        char[][] best = new char[N][L];
        char[][] ordIndividuals;
        ordIndividuals = orderByFitness(individuals);

        for(int i = 0; i < N; i++)
            for(int j = 0; j < L; j++)
                best[i][j] = ordIndividuals[i][j];

        return best;
    }

    /*
     * MAIN method
     * Creat a new object of class EGA
     */
    public static double EGA(int N, int L, double pm, int G){

       EGA.N = N;
       EGA.L = L;
       EGA.pm = pm;
       tempPopulation = new char[N][L];

       //Start with random population P(0)
       startPopulation();

       for(int t = 0; t < G; t++){
            Base.hardcopy(population,tempPopulation);

            // Order by fitness
            tempPopulation = orderByFitness(tempPopulation);

            // Deterministic crossover
            tempPopulation = detCrossover(tempPopulation);
            
            // Mutation 
            tempPopulation = mutation(tempPopulation);

            // Concatenate old population with tempPopulation
            char[][] aux = new char[2*N][L];
            for(int i = 0; i < N; i++)
                for(int j = 0; j < L; j++)
                    aux[i][j] = population[i][j];
            for(int i = 0; i < N; i++)
                for(int j = 0; j < L; j++)
                    aux[N+i][j] = tempPopulation[i][j];

            // Select N best
            population = generateNewPopulation(aux);
       }

       // Return maximum value in last generation
       return Base.max(Base.fitnessEvaluation(population));
    }

    public static void main(String[] args){
        for(int i = 0; i < 20; i++)
        System.out.println(EGA(100, 64, 0.05, 500));

   	//	// Several runs
    //    double  temp;
    //    double sum = 0.0;
    //    int[] freq = new int[9];
    //    for(int i = 0; i < 1000;  i++){
    //        temp = EGA(70, 64, 0.05, 500);
    //        sum += temp;
    //        freq[(int) temp / 8] ++;
    //    }
    //    for(int i = 0; i < 9; i++){
	//		System.out.print(i*8);
	//		System.out.print(" = ");
	//		System.out.println(freq[i]);
	//	}
	//	System.out.println(sum/1000);
    }

}
