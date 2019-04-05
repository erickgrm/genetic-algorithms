/*
 * Implementation of auxiliary routines
 *@author Erick García Ramírez
 *
 */

public class NNbase{
    public static double[][] data;
    public static int initial; // 168
    public static int hidden; // 13
    public static int errtype; // p =>0 even 

    public void NNbase(double[][] data, int errtype){
        NNbase.data = data;
        NNbase.errtype = errtype;
    }
    
    /* Transform an individual to the array of weights it encodes
     */
    public static double[] transform(char[] ind){
        double[] weights = new double[14];

        for(int i = 0; i < 14; i++){
            // Calculate the integer part of wi
                if(ind[i*32 + 1] == '1')
                    weights[i] += 4;
                if(ind[i*32 + 2] == '1')
                    weights[i] += 2;
                if(ind[i*32 + 3] == '1')
                    weights[i] += 1;

            // Calculate the decimal part of wi
            // We can handle 7 decimal places, allowing 4 bits to encode 
            //  a single digit
            for(int j = 1; j < 7; j++){
                int dig = 0;
                if(ind[i*32 + j*4] == '1')
                    dig += 8;
                if(ind[i*32 + j*4 +1] == '1')
                    dig += 4;
                if(ind[i*32 + j*4 +2] == '1')
                    dig += 2;
                if(ind[i*32 + j*4 +3] == '1')
                    dig += 1;
                if(dig < 10)
                    weights[i] += dig * (1/Math.pow(10,j));
            }
            if(ind[i*32] == '1')
                weights[i] *= -1;
        }
        return weights;
    }

    /*
     * Transform a given array of 14 numbers to the corresponding 
     * individual
     */
    public static char[] inv_transform(double[] weights){
        char[] individual = new char[14*32];

        for(int i = 0; i < 14; i++){
            int ifloor = (int) (Math.floor(Math.abs(weights[i])));
            int idec = (int) (Math.pow(10,7)*Math.abs(weights[i] - ifloor));
            // Sign of wi
            if(weights[i] < 0) 
                individual[i*32] = '1';

            // Integer part of wi
            char[] aux = ToBinary(ifloor);
            for(int j = 0; j < aux.length; j++)
                individual[i*32 + j + 1] = aux[j];

            // Decimal part of wi
            for(int d = 0; d < 7; d++){
                int dig = (int) (Math.floor(idec/Math.pow(10,7-d)));
                aux = ToBinary(dig);
                for(int j = 0; j < aux.length; j++)
                    individual[i*32 + (d+1)*4 + j] = aux[j];
            }
        }
            return individual;
    }

    /* Fitness of a given individual
     */
    public static double nnfitness(char[] individual){
        double error = 0.0;
        double[] ws = new double[14];
        ws = transform(individual); // array of weights encoded by individual

        // Neural network output for given individual (weights)
        double[] firstLayer = new double[initial];
        double[] secondLayer = new double[initial];

        for(int k = 0; k < 168; k++){
            // First layer outputs
            for(int i = 0; i < 14; i++){
                firstLayer[k] += ws[i+1] * data[k][i];
            }
            firstLayer[k] += ws[0];
            // Second layer outputs
            secondLayer[k] = 1/(1+Math.exp(firstLayer[k]));
        }
        // Return error for individual according to assumed norm
        return error(secondLayer);
    }

    public static double error(double[] arr){
        double e = 0.0;
        if(errtype == 0){
            for(int i = 0; i < initial; i++)
                e += Math.abs(arr[i] - data[i][13]);
        }
        else{
            for(int i = 0; i < initial; i++)
                e += Math.pow(arr[i] - data[i][13], errtype);
        }
        return e;
    }

    /*
     * Evaluate the fitness of N individuals under SGA
     * @returns an array of N doubles
     */
    public static double[] sgafitnessEvaluation(char[][] toEvaluate){
        int n = toEvaluate.length;
        double[] v = new double[n];
        v = fitnessEvaluation(toEvaluate);
        double[] vsga = new double[n];
        
        double mean = 0.0;
        double min = v[0];

            for(int i = 0; i < n; i++){
                mean += v[i];
                if(v[i] < min)
                    min = v[i];
            }
            mean /= n;
            
            for(int i = 0; i < n; i++)
                vsga[i] = v[i] + mean + min; 

        return vsga;
    }

    /*
     * Evaluate the fitness of N individuals
     * @returns an array of N doubles
     */
    public static double[] fitnessEvaluation(char[][] toEvaluate){
        int n = toEvaluate.length;
        int l = toEvaluate[0].length;
        double[] values = new double[n];

        char genome[];
        genome = new char[32*14];

        //string genome = null;
            for(int i = 0; i < n; i++){
                // Extract the genome of the individual i
                for(int j = 0; j < l; j++)
                    genome[j] = toEvaluate[i][j];

                values[i] = nnfitness(genome);
            }
        return values;
    }

    /*
     * Calculate  relative fitness of the individuals
     * @returns an array of N doubles
     */
    public static double[] relFitness(double[] values){
        double maxValue = 0.0;
        int n = values.length;
        double[] relValues = new double[n];
        
        for(int i = 0; i < n; i++)
            maxValue += values[i];

        for(int i = 0; i < n; i++){
            if(maxValue != 0.0) 
                relValues[i] = values[i]/maxValue;
            else 
                relValues[i] = 0.0;
        }
        return relValues;
    }


    public static char[] best(char[][] population, double[] fitness){
        int n = population.length;
        int l = population[0].length;
        char[] best = new char[l];

        int index = 0;
        double max = 0.0;
        for(int i = 0; i < n; i++){
            if(max < fitness[i]){
                max = fitness[i];
                index = i;
            }
        }

        if(0.0 < max) 
            for(int j = 0; j < l; j++)
                best[j] = population[index][j];
        else{
            index = (int) (n * Math.random());
            for(int j = 0; j < l; j++)
                best[j] = population[index][j];
        }
    return best;
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
        double max = 0.0;
        for(int i = 0; i < arr.length; i++)
            if(max < arr[i])
                max = arr[i];
        return max;
    }

    public static char[] ToBinary(int n){
        char[] bin = new char[4];
        bin[0]=bin[1]=bin[2]=bin[3] = '0';
        int i = 0;

        while(0 < n && i < 4 ){
            if(n % 2 == 0)
                bin[i] = '0';
            else 
                bin[i] = '1';
            n = (int) Math.floor(n/2);
            i++;
        }
        char temp = bin[0];
        bin[0] = bin[3];
        bin [3] = temp;
        temp = bin[1];
        bin[1] = bin[2];
        bin[2] = temp;

        return bin;
    }
         
               
    public static void main(String[] args){
    
    }

}
