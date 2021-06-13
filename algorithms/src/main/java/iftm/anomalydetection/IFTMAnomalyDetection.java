package iftm.anomalydetection;

import iftm.errorfunction.ErrorFunction;
import iftm.errorfunction.L2NormModelResultError;
import iftm.identityfunction.CABirchIdentityFunction;
import iftm.identityfunction.IdentityFunction;
import iftm.thresholdmodel.CompleteHistoryAvgStd;
import iftm.thresholdmodel.OneSidedThreshold;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * uses identity function and average models to detect anomalies
 *
 * @author fschmidt
 */
public class IFTMAnomalyDetection extends AnomalyDetection {

    protected static final Logger logger = Logger.getLogger(IFTMAnomalyDetection.class.getName());

    private final OneSidedThreshold thresholdModel;
    private final IdentityFunction identityFunction;
    private final ErrorFunction efunction;
    private boolean warnedIllegalValues = false;

    public IFTMAnomalyDetection(){
        thresholdModel = new CompleteHistoryAvgStd();
        identityFunction = new CABirchIdentityFunction();
        efunction = new L2NormModelResultError();
    }

    public IFTMAnomalyDetection(IdentityFunction identityFunction, ErrorFunction efunction, OneSidedThreshold thresholdModel) {
        this.identityFunction = identityFunction;
        this.thresholdModel = thresholdModel;
        this.efunction = efunction;
    }

    @Override
    public DistancePredictionResult predict(double[] value) {
        double[] results = identityFunction.predict(value);
        double distance = efunction.calc(value, results);

        boolean isAnomaly = !thresholdModel.isIncluded(distance);

        double[] deltaVec = new double[value.length];
        for (int i = 0; i < value.length; i++) {
            deltaVec[i] = Math.abs(value[i] - results[i]);
        }

        return new DistancePredictionResult(isAnomaly, distance, deltaVec, thresholdModel.getThreshold());
    }

    @Override
    public void train(double[] value) {
        for (double v : value) {
            if (!Double.isFinite(v)) {
                logger.log(Level.WARNING, "Encountered input value of NaN or infinite. The model will not train on such values!");
                return;
            }
        }
        double[] results = identityFunction.predict(value);
        identityFunction.train(value);
        double distance = efunction.calc(value, results);
        if (Double.isFinite(distance)) {
            thresholdModel.addValue(distance);
        } else if (!warnedIllegalValues) {
            logger.log(Level.WARNING, "Encountered distance value of NaN or infinite. The threshold domain will not train on such values!");
            warnedIllegalValues = true;
        }
    }

    public OneSidedThreshold getThresholdModel() {
        return thresholdModel;
    }

    public ErrorFunction getErrorFunction() {
        return efunction;
    }

    public IdentityFunction getIdentityFunction() {
        return identityFunction;
    }

    @Override
    public String toString() {
        return "" + getId();
    }

    @Override
    public void saveModel(OutputStream outputStream) {
        try {
            ObjectOutputStream objectOut = new ObjectOutputStream(outputStream);
            objectOut.writeObject(this);
            objectOut.close();
            outputStream.close();
            logger.log(Level.FINE, "The anomaly detection domain was successfully saved.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, String.valueOf(e.getStackTrace()));
        }
    }

    @Override
    public AnomalyDetection loadModel(InputStream inputStream) throws IOException, ClassNotFoundException {
        ObjectInputStream objectIn = new ObjectInputStream(inputStream);
        IFTMAnomalyDetection savedObject = (IFTMAnomalyDetection) objectIn.readObject();
        logger.log(Level.INFO, "The anomaly detection domain was successfully loaded.");
        return savedObject;
    }

    @Override
    public Map<String, Double> getStatistics() {
        Map<String, Double> allStats = new HashMap<>();
        Map<String, Double> statsIdentityFunction = identityFunction.getStatistics();
        allStats.putAll(statsIdentityFunction);
        Map<String, Double> statsThresholdModel = thresholdModel.getStatistics();
        allStats.putAll(statsThresholdModel);
        return allStats;
    }
}
