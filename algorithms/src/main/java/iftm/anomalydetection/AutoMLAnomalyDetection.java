package iftm.anomalydetection;

import iftm.automl.AnomalyDetectionPerformance;
import iftm.automl.UnsupervisedHyperParameterOptimization;
import iftm.automl.identitifunction.CABIRCHBreeding;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author fschmidt
 */
public class AutoMLAnomalyDetection extends AnomalyDetection {

    protected static final Logger logger = Logger.getLogger(AutoMLAnomalyDetection.class.getName());

    private final Map<AnomalyDetection, AnomalyDetectionPerformance> anomalyDetectionModels;
    private Map<AnomalyDetection, AnomalyDetectionPerformance> anomalyDetectionModelsOld;
    private final UnsupervisedHyperParameterOptimization optimizer;
    private final boolean ensemblerMode;
    private int optimizationInterval;
    private int countInterval = 0;
    private static final double MAX_DIFF = 0.00000001;
    private double oldScore = Double.MIN_VALUE;
    private boolean optimizationFinished = false;
    private AnomalyDetection bestDetector;
    private Map<AnomalyDetection, Double> detectors;
    private double overallScore;
    private double overallScoreOld;
    private final boolean stopTrainingAfterOptimization;
    private final List<double[]> sampleBuffer = new ArrayList<>();

    public AutoMLAnomalyDetection() {
        this(new CABIRCHBreeding(), 1000, false, false, new ArrayList<>());
    }

    public AutoMLAnomalyDetection(UnsupervisedHyperParameterOptimization optimizer, int optimizationInterval,
            boolean ensemblerMode) {
        this(optimizer, optimizationInterval, ensemblerMode, false, new ArrayList<>());
    }

    public AutoMLAnomalyDetection(UnsupervisedHyperParameterOptimization optimizer, int optimizationInterval,
                                  boolean ensemblerMode, boolean stopTrainingAfterOptimization) {
        this(optimizer, optimizationInterval, ensemblerMode, stopTrainingAfterOptimization, new ArrayList<>());
    }

    public AutoMLAnomalyDetection(UnsupervisedHyperParameterOptimization optimizer, int optimizationInterval,
            boolean ensemblerMode, boolean stopTrainingAfterOptimization, List<AnomalyDetection> initialDetectors) {
        super();
        if (optimizer.getNumPopulation() < initialDetectors.size()) {
            throw new IllegalArgumentException("Number of max models smaller than initial detectors given.");
        }
        this.optimizer = optimizer;
        //create numMaxModels times random models
        anomalyDetectionModels = new HashMap<>();
        anomalyDetectionModelsOld = new HashMap<>();
        for (AnomalyDetection detector : initialDetectors) {
            anomalyDetectionModels.put(detector, new AnomalyDetectionPerformance(detector));
        }
        if (initialDetectors.size() != optimizer.getNumPopulation()) {
            for (AnomalyDetection detector : createRandomModels(optimizer.getNumPopulation() - initialDetectors.size())) {
                anomalyDetectionModels.put(detector, new AnomalyDetectionPerformance(detector));
            }
        }
        this.ensemblerMode = ensemblerMode;
        this.optimizationInterval = optimizationInterval;
        this.stopTrainingAfterOptimization = stopTrainingAfterOptimization;
    }

    private List<AnomalyDetection> createRandomModels(int numMaxModels) {
        return optimizer.createRandomSet(numMaxModels);
    }

    @Override
    public DistancePredictionResult predict(double[] value) {
        if (optimizationFinished) {
            if (ensemblerMode) {
                return predictEnsemblerMode(value, true, false);
            } else {
                return bestDetector.predict(value);
            }
        } else {
            return predictEnsemblerMode(value, true, false);
        }
    }

    private DistancePredictionResult predictEnsemblerMode(double[] value, boolean equalWeight, boolean backgroundModel) {
        double countIsAnomaly = 0;
        double errorSum = 0;
        double thresholdSum = 0;
        double[] distancesSum = new double[value.length];
        Map<AnomalyDetection, AnomalyDetectionPerformance> detectorz = anomalyDetectionModelsOld;
        if (anomalyDetectionModelsOld.isEmpty() || backgroundModel) {
            detectorz = anomalyDetectionModels;
        }
        List<DistancePredictionResult> ensemblerResults = new ArrayList<>();
        boolean brokeWeight = false;
        List<AnomalyDetection> brokeAnomalyDetectors = new ArrayList<>();
        for (Map.Entry<AnomalyDetection, AnomalyDetectionPerformance> entry : detectorz.entrySet()) {
            double weight = 1.0 / detectorz.size();
            if (detectors != null && detectors.containsKey(entry.getKey()) && !equalWeight) {
                weight = detectors.get(entry.getKey()) / overallScoreOld;
            }
            if (!Double.isFinite(weight)) {
                brokeWeight = true;
                break;
            }
            DistancePredictionResult result = entry.getKey().predict(value);
            ensemblerResults.add(result);
            if (result.isAnomaly()) {
                countIsAnomaly += 1 * weight;
            }
            if (!Double.isFinite(result.getError()) || !Double.isFinite(result.getThreshold())) {
                brokeAnomalyDetectors.add(entry.getKey());
                logger.warning("Anomaly detector detected, which has not finite distance or threshold!");
                continue;
            }
            errorSum += result.getError() * weight;
            thresholdSum += result.getThreshold()  * weight;
            for (int i = 0; i < distancesSum.length; i++) {
                distancesSum[i] += result.getDistance()[i]  * weight;
                if (!Double.isFinite(result.getDistance()[i])) {
                    brokeAnomalyDetectors.add(entry.getKey());
                    logger.warning("Anomaly detector detected, which has not finite distance entry!");
                    continue;
                }
            }
        }
        if (brokeWeight) {
            logger.warning("Weight is not finite! Running equal weight mode: " + equalWeight);
            anomalyDetectionModels.clear();
            anomalyDetectionModelsOld.clear();
            optimizationFinished = false;
            for (AnomalyDetection detector : optimizer.createRandomSet(this.optimizer.getNumPopulation())) {
                anomalyDetectionModels.put(detector, new AnomalyDetectionPerformance(detector));
            }
            return new DistancePredictionResult(false, Double.NaN, new double[value.length], Double.NaN);
        }
        //Remove broken anomaly detectors
        if (!brokeAnomalyDetectors.isEmpty() && this.countInterval != 0) {
            logger.warning("At least " + brokeAnomalyDetectors.size() + " anomaly detectors are broke! They will be removed!");
            if (anomalyDetectionModelsOld.isEmpty()) {
                for (AnomalyDetection anomalyDetector : brokeAnomalyDetectors) {
                    anomalyDetectionModels.remove(anomalyDetector);
                }
            } else {
                for (AnomalyDetection anomalyDetector : brokeAnomalyDetectors) {
                    anomalyDetectionModelsOld.remove(anomalyDetector);
                }
            }
            anomalyDetectionModels.clear();
            anomalyDetectionModelsOld.clear();
            optimizationFinished = false;
            for (AnomalyDetection detector : optimizer.createRandomSet(brokeAnomalyDetectors.size())) {
                anomalyDetectionModels.put(detector, new AnomalyDetectionPerformance(detector));
            }
            return new DistancePredictionResult(false, Double.NaN, new double[value.length], Double.NaN);
        }

        boolean isAnomaly = false;
        if (countIsAnomaly > 0.5) {
            isAnomaly = true;
        }
        DistancePredictionResult overallResult = new DistancePredictionResult(isAnomaly, errorSum, distancesSum, thresholdSum);
        overallResult.addEnsemblerResult(ensemblerResults);
        return overallResult;
    }

    @Override
    public void train(double[] value) {
        sampleBuffer.add(value);
        if (optimizationFinished) {
            if(!stopTrainingAfterOptimization){
                if (ensemblerMode) {
                    trainEnsembler(value);
                } else {
                    bestDetector.train(value);
                }
            }
        } else {
            trainEnsembler(value);
            countInterval++;
            if (countInterval >= optimizationInterval) {
                for (AnomalyDetection ad : anomalyDetectionModels.keySet()) {
                    anomalyDetectionModels.get(ad).clear();
                }
                resetThresholdModels();
                for (double[] v : sampleBuffer) {
                    retrainThresholdModels(v);
                }
                if (!anomalyDetectionModels.isEmpty()) {
                    anomalyDetectionModelsOld = new HashMap<>(anomalyDetectionModels);
                    overallScoreOld = overallScore;
                }else{
                    anomalyDetectionModelsOld = new HashMap<>();
                }
                double currentScore = getAvgScore();
                if (Math.abs(currentScore - oldScore) < MAX_DIFF) {
                    optimizationFinished = true;
                    detectors = new HashMap<>();
                    double bestScore = Double.MIN_VALUE;
                    for (Map.Entry<AnomalyDetection, AnomalyDetectionPerformance> entry : anomalyDetectionModels.entrySet()) {
                        double score = optimizer.getScore(entry.getValue());
                        if(!ensemblerMode && score > bestScore){
                            bestScore = score;
                            bestDetector = entry.getKey();
                        }
                        overallScore += score;
                        detectors.put(entry.getKey(), score);
                    }
                    return;
                }
                oldScore = currentScore;
                countInterval = 0;
                sampleBuffer.clear();

                List<AnomalyDetection> optimizedDetectorSet = optimizer.optimize(new ArrayList<>(anomalyDetectionModels.values()));
                anomalyDetectionModels.clear();
                for (AnomalyDetection detector : optimizedDetectorSet) {
                    anomalyDetectionModels.put(detector, new AnomalyDetectionPerformance(detector));
                }
            }
        }
    }

    public DistancePredictionResult trainAndPredict(double[] values) {
        DistancePredictionResult results = predictBackground(values);
        train(values);
        return results;
    }

    private DistancePredictionResult predictBackground(double[] values) {
        return predictEnsemblerMode(values, true, true);
    }

    private double getAvgScore() {
        double score = 0;
        int count = anomalyDetectionModels.values().size();
        for (AnomalyDetectionPerformance result : anomalyDetectionModels.values()) {
            score += optimizer.getScore(result) / (double) count;
        }
        return score;
    }

    private void trainEnsembler(double[] value){
        for (Map.Entry<AnomalyDetection, AnomalyDetectionPerformance> entry : anomalyDetectionModels.entrySet()) {
            entry.getKey().train(value);
        }
    }

    private void resetThresholdModels() {
        for (Map.Entry<AnomalyDetection, AnomalyDetectionPerformance> entry : anomalyDetectionModels.entrySet()) {
            ((IFTMAnomalyDetection) entry.getKey()).getThresholdModel().clear();
        }
    }

    private void retrainThresholdModels(double[] value) {
        for (Map.Entry<AnomalyDetection, AnomalyDetectionPerformance> entry : anomalyDetectionModels.entrySet()) {
            IFTMAnomalyDetection iftmModel = ((IFTMAnomalyDetection) entry.getKey());
            double[] resultIF = iftmModel.getIdentityFunction().predict(value);
            double iftmError = iftmModel.getErrorFunction().calc(value, resultIF);
            iftmModel.getThresholdModel().addValue(iftmError);
            DistancePredictionResult result = iftmModel.predict(value);
            entry.getValue().addResults(result.getError() / (double) result.getDistance().length, result.getThreshold() / (double) result.getDistance().length, result.isAnomaly());
        }
    }

    @Override
    public void saveModel(OutputStream outputStream) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AnomalyDetection loadModel(InputStream inputStream) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getCountInterval() {
        return countInterval;
    }

    public int getOptimizationInterval() {
        return optimizationInterval;
    }

    public void setOptimizationInterval(int optimizationInterval) {
        this.optimizationInterval = optimizationInterval;
    }

    @Override
    public Map<String, Double> getStatistics() {
        List<Map<String, Double>> allStats = new ArrayList<>();
        if (anomalyDetectionModelsOld.isEmpty()) {
            for (Map.Entry<AnomalyDetection, AnomalyDetectionPerformance> detector : anomalyDetectionModels.entrySet()) {
                allStats.add(detector.getKey().getStatistics());
            }
        } else {
            for (Map.Entry<AnomalyDetection, AnomalyDetectionPerformance> detector : anomalyDetectionModelsOld.entrySet()) {
                allStats.add(detector.getKey().getStatistics());
            }
        }
        return mergeStatistics(allStats);
    }
}
