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
     
        double[] test0 = { 4.53063231, -6.02064764,  2.36022613, -1.77151078, -7.18699502, -2.56922006,
              6.02355566,  7.32490527, -2.74016777,  0.16231994, -4.45277301,  7.87290986,
              6.96582016,  6.29056579,  5.7838739, -5.51852382 ,  1.35930151,  2.828317,
              6.51897966,  3.31846584, -4.08310833,  6.7564768 , -2.13893918, -6.8791071,
              0.99704654, -3.74595576, -0.87885024, -6.21099484, -2.14855632,  1.47861821,
              3.44633935,  0.12745952, -5.50564254,  5.68733655,  4.38602469, -4.40532936,
             -7.5366947,  -1.53861526, -0.9789137,  2.45781573,  -5.05202148,  5.64797431,
              1.6280419,  -7.70964169,  6.38621169, -5.07904509};
        double[] test2 = {5.886559288526175, -6.3606461598003134, -0.4778085070766826,
                         -1.2213288591106566, 7.799555811284318, -5.683192397218916,
                         4.570792185406359, -7.8540068598613395, -1.3535432307181627,
                         -1.5489421507304242, 7.206183661543517, -5.078299764835461,
                         0.17656307733268692, -5.0419066400897, -6.8128469020606826,
                         -0.2919334146824979, 3.001791890717268, 7.600663165005533,
                         -1.4150079802237747, 1.5086708832855185, 5.1322022606887, 6.607929910003878,
                         -3.779199297648666, 0.037419252237004236, -6.5061887037239545,
                         4.2077468678643815, 7.009147271548015, 1.360460707397985, 5.154495727846383,
                         -0.6901533182343592, 7.526095351301489, -6.374052421651976,
                         -6.445674573800246, 6.59183534455238, 3.4373878256879293, 1.491116261076615,
                         3.9478072931908335, 7.0448451937915575, -5.983155835357144,
                         1.364856028425902, 5.49784889630172, 2.1841199330393968, 4.0181865059516815,
                         3.2593271891002624, -6.31469991175346, -0.39524301661269};
        double[] test4 = {3.314548646340328, 6.790738045389721, 0.02210402496942887,
                         -3.4874338414051897, 6.572712170231014, 0.9954333901235216,
                         0.016580723287838408, 2.373460640659409, 4.68080180391968,
                         0.6483452493263232, 3.7132167805478598, 6.518206598304983,
                         -5.638449127370302, -5.2258175731667045, 1.1079092402305801,
                         -7.4495635421930375, 5.667584861321691, -4.995542619360768,
                         5.274235920139536, -2.6941968749992435, 3.630854728187825,
                         7.4988952558446496, 2.8978842344056233, -0.21530570915082733,
                         -7.059770956858141, -2.5511957464784225, 5.019455432964323,
                         7.600009685754813, -6.797197009612608, 3.316461113529135, 1.5111399460998922,
                         -0.30162941404293997, -7.950512990171138, 4.993437930917136,
                         0.10206131675117207, 7.2689548889881195, 4.200584073366912,
                         -5.8152184144229375, -2.1723803027435404, 2.0093427002778004,
                         6.079004846807588, 4.222046536289328, 0.7771467334670824, 5.7999572969971505,
                         -5.224394754411262, -2.999450091270544};
        double[] test6 = {1.185309909974448, 6.724440163837523, 6.525961386136568, 6.086043693445785,
                         -3.011879529848246, 7.880196056813732, 7.748962181616434,
                         0.14185856708086494, 2.3961688183105316, -3.8758692475999488,
                         -7.778114671923647, 2.9449540188348067, 5.532726997631516, 2.29932570196437,
                         -1.7731977431967771, -7.136944607410372, -0.8258286968835767,
                         -3.3209436473285536, 1.6484794417339543, -1.764780647176432,
                         6.804553709196128, -2.8001041293148106, -0.4360763670357927,
                         3.6765713456145352, -6.4049622655099725, 2.9702952316786915,
                         6.459464745445047, 7.330322106668063, 6.5575075654592645, -6.333716732761697,
                         3.205617328009074, 2.6298109651722417, 5.809596120601878, -4.881475150143634,
                         -3.976559478702245, -5.950091395341199, 5.246822495932961,
                         -2.401303318892804, 4.946855895023257, -2.44597734304509, -0.779471322072563,
                         -6.658782100896469, -5.883887573644099, 6.184510578157419,
                         -2.2825260135625527, 1.7116273668096489};
        double[] test8 ={1.185309909974448, 6.724440163837523, 6.525961386136568, 6.086043693445785,
                         -3.011879529848246, 7.880196056813732, 7.748962181616434,
                         0.14185856708086494, 2.3961688183105316, -3.8758692475999488,
                         -7.778114671923647, 2.9449540188348067, 5.532726997631516, 2.29932570196437,
                         -1.7731977431967771, -7.136944607410372, -0.8258286968835767,
                         -3.3209436473285536, 1.6484794417339543, -1.764780647176432,
                         6.804553709196128, -2.8001041293148106, -0.4360763670357927,
                         3.6765713456145352, -6.4049622655099725, 2.9702952316786915,
                         6.459464745445047, 7.330322106668063, 6.5575075654592645, -6.333716732761697,
                         3.205617328009074, 2.6298109651722417, 5.809596120601878, -4.881475150143634,
                         -3.976559478702245, -5.950091395341199, 5.246822495932961,
                         -2.401303318892804, 4.946855895023257, -2.44597734304509, -0.779471322072563,
                         -6.658782100896469, -5.883887573644099, 6.184510578157419,
                         -2.2825260135625527, 1.7116273668096489};

            for(int i = 0; i < 160; i++){
                System.out.print(data[i][13]+", ");
            }
            System.out.println();
            ModelEvaluation model0 = new ModelEvaluation(test0,0);
            double[] predictions = model0.predict(data);
            double e = 0;
            for(int i = 0; i < predictions.length; i++){
                if(data[i][13] == predictions[i])  e += 1;
                
            }
            System.out.println(model0.error(data));
            System.out.println("Percentage of correct answers for best model for p=0: " + 100*e/data.length);
        
            ModelEvaluation model2 = new ModelEvaluation(test2,2);
            predictions = model2.predict(data);
            e = 0;
            for(int i = 0; i < predictions.length; i++){
                if(data[i][13] == predictions[i])  e += 1;
                System.out.print(predictions[i]+", ");
            }
            System.out.println(model2.error(data));
            System.out.println("Percentage of correct answers for best model for p=2: " + 100*e/data.length);

            ModelEvaluation model4 = new ModelEvaluation(test4,4);
            predictions = model4.predict(data);
            e = 0;
            for(int i = 0; i < predictions.length; i++){
                if(data[i][13] == predictions[i])  e += 1;
            }
            System.out.println(model4.error(data));
            System.out.println("Percentage of correct answers for best model for p=4: " + 100*e/data.length);

            ModelEvaluation model6 = new ModelEvaluation(test6,6);
            predictions = model6.predict(data);
            e = 0;
            for(int i = 0; i < predictions.length; i++)
                if(data[i][13] == predictions[i])  e += 1;
            System.out.println(model6.error(data));
            System.out.println("Percentage of correct answers for best model for p=6: " + 100*e/data.length);

            ModelEvaluation model8= new ModelEvaluation(test8,8);
            predictions = model8.predict(data);
            e = 0;
            for(int i = 0; i < predictions.length; i++)
                if(data[i][13] == predictions[i])  e += 1;
            System.out.println(model8.error(data));
            System.out.println("Percentage of correct answers for best model for p=8: " + 100*e/data.length);
    }
}

