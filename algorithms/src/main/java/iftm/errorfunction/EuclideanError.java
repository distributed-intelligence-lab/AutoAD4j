package iftm.errorfunction;

import org.apache.commons.math3.ml.distance.EuclideanDistance;

/**
 *
 * @author fschmidt
 */
public class EuclideanError implements ErrorFunction {

    private final EuclideanDistance distance = new EuclideanDistance();

    @Override
    public double calc(double[] actual, double[] prediction) {
        return distance.compute(actual, prediction);
    }

}
