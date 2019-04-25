/*
 * Module for the evaluation of the trained neural network
 * Routines for predictions, classification errors, % of
 * errors, etc.
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

class ModelEvaluation {
    public static double[] learnt_weights;
    public static double[][] test_data;
    public static int error_type;
    public static int I = 14;
    public static int W;

    public ModelEvaluation(double[] learnt_weights, int error_type) {
        this.learnt_weights = learnt_weights;
        this.error_type = error_type;
        this.W = learnt_weights.length;
    }
   
    public static double output_for_sample(double[] sample) {
        double[] first_layer_output = new double[3];
        double output = 0.0;

        for(int j = 0; j < 3; j++){
            first_layer_output[j] += learnt_weights[I*j]; //Threshold
            for(int i = 1; i < I; i++)
                first_layer_output[j] += learnt_weights[I*j + i] * sample[i-1];
            first_layer_output[j] = activation_function(first_layer_output[j]);
        }
        output += learnt_weights[I*3];  // Threshold 
        for(int j = 1; j < 4; j++)
            output += learnt_weights[I*3 + j] * first_layer_output[j-1];
        return activation_function(output);
    }

    public static double[] outputs(double[][] samples) {
        int no_of_samples = samples.length;
        double[] outs = new double[no_of_samples];
        for(int i = 0; i < no_of_samples; i++)
            outs[i] = output_for_sample(samples[i]);
        return outs;
    }
    
    public static double predict_sample(double[] sample) {
        double output = output_for_sample(sample);
        if(output <= 0.25) return 0.0;
        if(0.25 < output && output <= 0.75) return 0.5;
        else return 1.0;
    }

    public static double[] predict(double[][] samples) {
        int no_of_samples = samples.length;
        double[] predictions = new double[no_of_samples];
        for(int i = 0; i < no_of_samples; i++)
            predictions[i] = predict_sample(samples[i]);
        return predictions;
    }

    public static double activation_function(double x) {
    // return Math.tanh(x); // Hyperbolic tangent
    return 1/(1 + Math.exp((-1)*x)); // Sigmoid
    }

    public static double error(double[][] samples){
        int no_of_samples = samples.length;
        double e = 0.0;
        double[] outs = outputs(samples);
        if(error_type == 0){
            // Sum L_1 errors over all samples
            for(int k = 0; k < no_of_samples; k++)
            e += Math.abs(outs[k] - samples[k][13]);
        }
        else{
            // Sum L_error_type errors over all samples
            for(int k = 0; k < no_of_samples; k++)
            e += Math.pow(outs[k] - samples[k][13], error_type);
        }
        return e;
    }//END of error

    public static void main(String [] args){
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
        
        double[][] data = new double[160][14];
        for(int i = 0; i < 160; i++)
            for(int j = 0; j < 13; j++)
                data[i][j] = temp[i][j];
        for(int i = 0; i < 160; i++){
            if(temp[i][13] == 1)
                data[i][13] = 0;
            if(temp[i][14] == 1)
                data[i][13] = 0.5;
            if(temp[i][15] == 1)
                data[i][13] = 1.0;
        }
     
        double[] test1 = {-6.9212331917927905, 4.975263796654581, 2.1709515645017907, 1.9605649037680213, -5.298618884006958,
                       1.8390734413231664, 1.6444330947266261, -6.164485715942404, -7.3173027162153375, -2.3831983967989623,
                       7.107841708167798, 3.061134323705488, 3.9605423173328576, 4.128684502574371, -4.392061927885048,
                       -3.3146594103971845, 2.4606614912325946, -5.699560421331079, 2.620204361603425, -3.5403910262152216,
                       -0.3195712093992949, 4.368724943580943, 5.680647394361523, -1.7392366071762018, -1.8556219296739322,
                       5.812468375312046, 7.722556746462572, 5.1228791405367815, -2.2657293314700175, -2.894701398516824,
                       -1.375545473305678, -1.4149910301528537, -4.57113654751754, 5.30610372240135, -3.960992965701941,
                       -1.5208740812572616, 0.7650020933337588, -4.142044380836354, -5.107502021295957, 2.471704704581591,
                       1.5321072397087039, -5.217774187094622, 7.129913799948669, -6.296319560320375, -7.401475784933105,
                       -5.365189922471307};//Best over 100 runs, 50 individuals, norm 2, 200 Generations

        double[] test2 = {2.6149340183099135, 3.8026464983919506, -2.804258506015906, 3.1776181391537865, -7.489649118817035, -7.04649413021838,
                       4.19928878992531, 2.361245912914149, -4.753656952655527, 3.6565516801795055, -0.6862781669433347,
                       -1.232332327337311, -7.656653406682064, -5.637212554504024, -0.6346878395776743, -3.263567739216863,
                       1.4486650878513794, 4.682604170153305, -0.9574550723934735, -5.718792463536532, 2.3777913241751167,
                       7.2911996815025795, -3.5864782653245264, 0.21423287769493787, -6.876020937696177, 6.469244805981385,
                       3.8405842402599166, -0.37215659906028437, 6.341977385215377, -1.9417415184592512, 3.8329056979451543,
                       -2.723536009801686, 6.331876849129337, -5.567621467886945, -5.440292658061879, -0.561689036196802,
                       0.3746128953047577, -6.496322447420368, 5.723167295467732, -5.4471063928570835, 6.512650432112256,
                       -6.597126128513835, 0.9691267869216457, 1.0181241110642407, -3.381394018908568,
                       2.4996854756015745};//Best over 100 runs, 70 Individuals, norm 2, 400 Generations

        ModelEvaluation model1 = new ModelEvaluation(test1,2);

        double[] predictions1 = model1.predict(data);
        double e1 = 0;
        for(int i = 0; i < predictions1.length; i++)
            if(data[i][13] == predictions1[i])  e1 += 1;
           // if(data[i][13] == predictions[i]) str = "\u2713";
           //   System.out.println(data[i][13] +" "+ predictions[i] + " " + str);
           //   str = "\u274C";
        System.out.println(model1.error(data));
        System.out.println("Percentage of correct answers for model1: " + 100*e1/data.length);


        ModelEvaluation model2 = new ModelEvaluation(test2,2);
        double[] predictions2 = model2.predict(data);
        double e2 = 0;
        for(int i = 0; i < predictions2.length; i++)
            if(data[i][13] == predictions2[i])  e2 += 1;
        System.out.println(model2.error(data));
        System.out.println("Percentage of correct answers for model2: " + 100*e2/data.length);
    }
}

