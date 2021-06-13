package iftm.boosting;

import iftm.anomalydetection.AnomalyDetection;
import iftm.errorfunction.ErrorFunction;
import iftm.identityfunction.IdentityFunction;
import iftm.thresholdmodel.OneSidedThreshold;
import java.io.File;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author fschmidt
 */
public class DistributionModel implements Serializable {

    private static final AtomicInteger count = new AtomicInteger(0);
    private final int id;
    private final OneSidedThreshold thresholdModel;
    private final IdentityFunction identityFunction;
    private final ErrorFunction efunction;

    public DistributionModel(IdentityFunction identityFunction, ErrorFunction efunction, OneSidedThreshold avgStdModel) {
        this.identityFunction = identityFunction;
        this.thresholdModel = avgStdModel;
        id = count.incrementAndGet();
        this.efunction = efunction;
    }

    public DistributionModelPredictionResult predict(double[] value) {
        double[] results = identityFunction.predict(value);
        double distance = efunction.calc(value, results);

        boolean isIncluded = thresholdModel.isIncluded(distance);

        double[] deltaVec = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            deltaVec[i] = Math.abs(value[i] - results[i]);
        }

        return new DistributionModelPredictionResult(distance, deltaVec, value, results, isIncluded);
    }

    public void train(double[] value) {
        identityFunction.train(value);
        double[] results = identityFunction.predict(value);
        double distance = efunction.calc(value, results);
        thresholdModel.addValue(distance);
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "" + getId();
    }

    public void saveModel(File file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public AnomalyDetection loadModel(File file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
