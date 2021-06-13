package iftm.errorfunction;

import org.apache.commons.math3.ml.distance.ManhattanDistance;

/**
 *
 * @author fschmidt
 */
public class ManhattanError implements ErrorFunction {

    private final ManhattanDistance distance = new ManhattanDistance();

    @Override
    public double calc(double[] actual, double[] prediction) {
        return distance.compute(actual, prediction);
    }

}
