package iftm.errorfunction;

import org.apache.commons.math3.ml.distance.CanberraDistance;

/**
 *
 * @author fschmidt
 */
public class CanberraError implements ErrorFunction {

    private final CanberraDistance distance = new CanberraDistance();

    @Override
    public double calc(double[] actual, double[] prediction) {
        return distance.compute(actual, prediction);
    }
}
