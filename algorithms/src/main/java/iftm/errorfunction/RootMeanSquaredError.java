package iftm.errorfunction;

/**
 *
 * @author fschmidt
 */
public class RootMeanSquaredError implements ErrorFunction{

    @Override
    public double calc(double[] actual, double[] prediction) {
        int n = actual.length; 
        double sum = 0.0; 
        for (int i = 0; i < n; i++) {     
            double diff = actual[i] - prediction[i];     
            sum += diff * diff; 
        } 
        double mse = sum / (double)n;
        return Math.sqrt(mse);
    }
    
}
