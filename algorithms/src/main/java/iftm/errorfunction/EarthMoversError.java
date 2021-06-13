package iftm.errorfunction;

import org.apache.commons.math3.ml.distance.EarthMoversDistance;

/**
 *
 * @author fschmidt
 */
public class EarthMoversError implements ErrorFunction {

    private final EarthMoversDistance distance = new EarthMoversDistance();

    @Override
    public double calc(double[] actual, double[] prediction) {
        return distance.compute(actual, prediction);
    }

}
