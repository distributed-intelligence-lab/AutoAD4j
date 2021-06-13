package iftm.anomalydetection;

import iftm.aggregationfunction.AggregationFunction;
import iftm.errorfunction.ErrorFunction;
import iftm.identityfunction.IdentityFunction;
import iftm.thresholdmodel.OneSidedThreshold;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author fschmidt
 */
public class SingleLeadAnomalyDetection extends AnomalyDetection {

    private final OneSidedThreshold thresholdModel;
    private final IdentityFunction identityFunction;
    private final ErrorFunction efunction;
    private final List<OneSidedThreshold> thresholdModelList;
    private final List<IdentityFunction> identityFunctionList;
    private final List<ErrorFunction> efunctionList;
    private final AggregationFunction aggregationFunction;

    public SingleLeadAnomalyDetection(IdentityFunction identityFunction, ErrorFunction efunction, OneSidedThreshold thresholdModel,
            AggregationFunction aggregationFunction) {
        super();
        this.identityFunction = identityFunction;
        this.thresholdModel = thresholdModel;
        this.efunction = efunction;
        this.aggregationFunction = aggregationFunction;
        thresholdModelList = new ArrayList<>();
        identityFunctionList = new ArrayList<>();
        efunctionList = new ArrayList<>();
    }

    @Override
    public DistancePredictionResult predict(double[] value) {
        //init lists
        if (identityFunctionList.isEmpty()) {
            for (int i = 0; i < value.length; i++) {
                identityFunctionList.add(identityFunction.newInstance());
                thresholdModelList.add((OneSidedThreshold) thresholdModel.newInstance());
                efunctionList.add(efunction);
            }
        }
        List<DistancePredictionResult> results = new ArrayList<>();
        for (int i = 0; i < value.length; i++) {
            double[] currentValue = new double[]{value[i]};
            double[] ifResults = identityFunction.predict(currentValue);
            double distance = efunction.calc(currentValue, ifResults);

            boolean isAnomaly = !thresholdModel.isIncluded(distance);

            double[] deltaVec = new double[currentValue.length];
            for (int j = 0; j < currentValue.length; j++) {
                deltaVec[j] = Math.abs(currentValue[j] - ifResults[j]);
            }

            DistancePredictionResult result = new DistancePredictionResult(isAnomaly, distance, deltaVec, thresholdModel.getThreshold());
            results.add(result);
        }

        return aggregationFunction.predict(results);
    }

    @Override
    public void train(double[] value) {
        for (int i = 0; i < value.length; i++) {
            double[] currentValue = new double[]{value[i]};
            identityFunctionList.get(i).train(currentValue);
            double[] results = identityFunctionList.get(i).predict(currentValue);
            double distance = efunctionList.get(i).calc(currentValue, results);
            thresholdModelList.get(i).addValue(distance);
        }
    }

    public OneSidedThreshold getThresholdModel() {
        return thresholdModel;
    }

    public ErrorFunction getErrorfunction() {
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AnomalyDetection loadModel(InputStream inputStream) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, Double> getStatistics() {
        List<Map<String, Double>> allStats = new ArrayList<>();
        for (IdentityFunction identityFunction : identityFunctionList) {
            allStats.add(identityFunction.getStatistics());
        }
        for (OneSidedThreshold thresholdModel : thresholdModelList) {
            allStats.add(thresholdModel.getStatistics());
        }
        return mergeStatistics(allStats);
    }
}
