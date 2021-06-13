package iftm.errorfunction;

import org.apache.commons.math3.ml.distance.ChebyshevDistance;

/**
 *
 * @author fschmidt
 */
public class ChebyshevError implements ErrorFunction {

    private final ChebyshevDistance distance = new ChebyshevDistance();

    @Override
    public double calc(double[] actual, double[] prediction) {
        return distance.compute(actual, prediction);
    }
}
