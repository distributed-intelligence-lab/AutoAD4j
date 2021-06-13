package iftm.errorfunction;

/**
 *
 * @author fschmidt
 */
public class L2NormModelResultError implements ErrorFunction {

    @Override
    public double calc(double[] actual, double[] prediction) {
        double norm = 0;
        for (double p : prediction) {
            norm += (p * p);
        }
        return Math.sqrt(norm);
    }

}
