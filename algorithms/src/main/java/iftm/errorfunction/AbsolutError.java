package iftm.errorfunction;

/**
 *
 * @author fschmidt
 */
public class AbsolutError implements ErrorFunction {

    @Override
    public double calc(double[] actual, double[] prediction) {
        double sum = 0.0;
        for (int i = 0; i < actual.length; i++) {
            sum += Math.abs(actual[i] - prediction[i]);
        }
        return sum;
    }

}
