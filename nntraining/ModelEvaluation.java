/*
 * Module for the evaluation of the trained neural network
 * Routines for predictions, classification errors, % of
 * errors, etc.
 */
class ModelEvaluation {
    public static double[] learnt_weights;
    public static double[][] test_data;
    public static int error_type;
    public static int no_of_test_samples;
    public static int I = 14;
    public static int W;

    public ModelEvaluation(double[] learnt_weights, double[][] test_data, int error_type) {
        this.learnt_weights = learnt_weights;
        this.test_data = test_data;
        this.error_type = error_type;
        this.no_of_test_samples = test_data.length;
        this.W = learnt_weights.length;
    }
   
    public static double output_for_sample(double[] sample) {
        double[] first_layer_output = new double[3];
        double output = 0.0;

        for(int j = 0; j < 3; j++){
            first_layer_output[j] += learnt_weights[I*j]; //Threshold
            for(int k = 1; k < I; k++)
                first_layer_output[j] += learnt_weights[I*j + k] * sample[k-1];
            first_layer_output[j] = activation_function(first_layer_output[j]);
        }
        output += learnt_weights[I*3];  // Threshold 
        for(int j = 1; j < 4; j++)
            output += learnt_weights[I*3 + j] * first_layer_output[j-1];
        return activation_function(output);
    }
    
    public static double[] outputs() {
        double[] outputs = new double[no_of_test_samples];
        for(int i = 0; i < no_of_test_samples; i++)
            outputs[i] = output_for_sample(test_data[i]);   
        System.out.print(outputs[0]);
        return outputs;
    }

    public static double predict_sample(double[] sample) {
        double output = output_for_sample(sample);
        if(output <= 0.25) return 0.0;
        if(0.25 < output && output <= 0.75) return 0.5;
        else return 1.0;
    }

    public static double[] predict() {
        double[] predictions = new double[no_of_test_samples];
        for(int i = 0; i < no_of_test_samples; i++)
            predictions[i] = predict_sample(test_data[i]);
        return predictions;
    }

    public static double activation_function(double x) {
    // return Math.tanh(x); // Hyperbolic tangent
    return 1/(1 + Math.exp((-1)*x)); // Sigmoid
    }

    public static double sum_of_errors(){
    double e = 0.0;
    double[] outputs = outputs();
    if(error_type == 0){
    // Sum L_1 errors over all samples
    for(int k = 0; k < no_of_test_samples; k++)
    e += Math.abs(outputs[k] - test_data[k][13]);
    }
    else{
    // Sum L_error_type errors over all samples
    for(int k = 0; k < no_of_test_samples; k++)
    e += Math.pow(outputs[k] - test_data[k][13], error_type);
    }
    return e;
    }//END of error

    public static void main(String [] args){
     
    }
}

