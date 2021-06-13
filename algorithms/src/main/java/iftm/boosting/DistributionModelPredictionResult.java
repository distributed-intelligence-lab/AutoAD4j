package iftm.boosting;

import java.io.Serializable;

/**
 *
 * @author fschmidt
 */
public class DistributionModelPredictionResult implements Serializable{

    private final double error;
    private final double[] diffVec;
    private final double[] actual;
    private final double[] prediction;
    private final boolean isIncluded;

    public DistributionModelPredictionResult(double error, double[] diffVec, double[] actual, double[] prediction, boolean isIncluded) {
        this.error = error;
        this.diffVec = diffVec;
        this.actual = actual;
        this.prediction = prediction;
        this.isIncluded = isIncluded;
    }

    public double getError() {
        return error;
    }

    public double[] getDiffVec() {
        return diffVec;
    }

    public double[] getActual() {
        return actual;
    }

    public double[] getPrediction() {
        return prediction;
    }

    public boolean isIsIncluded() {
        return isIncluded;
    }

}
