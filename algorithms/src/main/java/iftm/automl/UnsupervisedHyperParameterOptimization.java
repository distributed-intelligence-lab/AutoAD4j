package iftm.automl;

import iftm.anomalydetection.AnomalyDetection;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import static java.util.stream.Collectors.toMap;

/**
 *
 * @author fschmidt
 */
public abstract class UnsupervisedHyperParameterOptimization implements Serializable {

    protected static final Logger logger = Logger.getLogger(UnsupervisedHyperParameterOptimization.class.getName());

    private final double percBest;
    private final double randomOld;
    private final double mutationProbability;
    private final int numPopulation;
    private final double scoreFactorError;
    private final double scoreFactorThreshold;
    private final double scoreFactorDistThErr;
    private final double scoreFactorFalseAlarms;
    private final double activationFalseAlarmBound;
    private final double activationDistBound;
    private static final Random random = new Random();

    public UnsupervisedHyperParameterOptimization() {
        this(200, 0.2, 0.05, 0.2);
    }

    public UnsupervisedHyperParameterOptimization(int numPopulation) {
        this(numPopulation, 0.2, 0.05, 0.2);
    }

    public UnsupervisedHyperParameterOptimization(int numPopulation, double percBest, double randomOld, double mutationProbability) {
        this(numPopulation, percBest, randomOld, mutationProbability, 2.0, 2.0, 1.0, 5.0, 0.1, 0.0);
    }

    public UnsupervisedHyperParameterOptimization(int numPopulation, double percBest, double randomOld, double mutationProbability, double scoreFactorError, double scoreFactorThreshold, double scoreFactorDistThErr, double scoreFactorFalseAlarms, double activationFalseAlarmBound, double activationDistBound) {
        this.percBest = percBest;
        this.randomOld = randomOld;
        this.mutationProbability = mutationProbability;
        this.numPopulation = numPopulation;
        this.scoreFactorError = scoreFactorError;
        this.scoreFactorThreshold = scoreFactorThreshold;
        this.scoreFactorDistThErr = scoreFactorDistThErr;
        this.scoreFactorFalseAlarms = scoreFactorFalseAlarms;
        this.activationFalseAlarmBound = activationFalseAlarmBound;
        this.activationDistBound = activationDistBound;
    }

    public List<AnomalyDetection> optimize(List<AnomalyDetectionPerformance> results) {
        logger.info("-----OPTIMIZATION START-----");
        logOptimizationScores(results);
        List<Map<String, Double>> resultModelStats = new ArrayList<>();
        Map<AnomalyDetectionPerformance, Double> mergedResults = new HashMap<>();
        for (int i = 0; i < results.size(); i++) {
            AnomalyDetectionPerformance result = results.get(i);
            mergedResults.put(result, getScore(result));
            resultModelStats.add(result.getDetector().getStatistics());
        }

        //take from old population
        Map<AnomalyDetectionPerformance, Double> sortedMap = mergedResults.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e2, LinkedHashMap::new));
        int numBestValues = (int) Math.ceil(((double) numPopulation) * percBest);
        List<AnomalyDetection> bestPercResults = new ArrayList<>();
        List<AnomalyDetectionPerformance> bestPercPerformance = new ArrayList<>();
        List<Map<String, Double>> bestModelStats = new ArrayList<>();
        for (Map.Entry<AnomalyDetectionPerformance, Double> sortedEntry : sortedMap.entrySet()) {
            bestPercResults.add(sortedEntry.getKey().getDetector());
            bestPercPerformance.add(sortedEntry.getKey());
            bestModelStats.add(sortedEntry.getKey().getDetector().getStatistics());
            if (bestPercResults.size() == numBestValues) {
                break;
            }
        }
        logger.info("-----BEST PERCENT-----");
        logOptimizationScores(bestPercPerformance);
        logger.info("-----MODEL STATS-----");
        logModels(resultModelStats);
        logger.info("-----MODEL STATS BEST PERCENT-----");
        logModels(bestModelStats);

        int numOfRandomValues;
        if (numPopulation > 1) {
            numOfRandomValues = (int) Math.ceil(numPopulation * randomOld);
            Set<AnomalyDetection> furtherRandomResults = new HashSet<>();
            AnomalyDetectionPerformance[] resultsArray = sortedMap.keySet().toArray(new AnomalyDetectionPerformance[sortedMap.size()]);
            for (int i = 0; i < numOfRandomValues; i++) {
                while (true) {
                    int randomNum = ThreadLocalRandom.current().nextInt(numBestValues, sortedMap.size() + 1);
                    if (-1 < randomNum && randomNum < resultsArray.length) {
                        AnomalyDetectionPerformance rR = resultsArray[randomNum];
                        furtherRandomResults.add(rR.getDetector());
                        break;
                    }
                }
            }
            bestPercResults.addAll(furtherRandomResults);
        } else {
            if (!bestPercResults.isEmpty()) {
                bestPercResults.add(bestPercResults.get(0));
            }
            numOfRandomValues = 1;
        }

        //breed
        List<AnomalyDetection> childs = new ArrayList<>();
        for (int i = numOfRandomValues + numBestValues - 1; i < numPopulation; i++) {
            //Select parents
            int index1 = random.nextInt(bestPercResults.size());
            int index2 = random.nextInt(bestPercResults.size());
            while (index1 == index2) {
                index2 = random.nextInt(bestPercResults.size());
            }
            AnomalyDetection parent1 = bestPercResults.get(index1);
            AnomalyDetection parent2 = bestPercResults.get(index2);

            //randomly combinate to child
            AnomalyDetection child = breed(parent1, parent2);
            childs.add(child);
        }

        //mutate childs
        List<Map<String, Double>> newModelStats = new ArrayList<>();
        List<AnomalyDetection> newPopulation = new ArrayList<>(childs);
        for (AnomalyDetection ad : bestPercResults) {
            AnomalyDetection adClone = copyAnomalyDetection(ad);//SerializationUtils.clone(ad);
            if (adClone != null) {
                newPopulation.add(adClone);
                newModelStats.add(adClone.getStatistics());
            }
        }
        logger.info("-----MODEL STATS NEXT ROUND-----");
        logModels(newModelStats);
        logger.info("-----OPTIMIZATION END-----");
        return newPopulation;
    }

    private AnomalyDetection copyAnomalyDetection(AnomalyDetection object) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            oos.flush();
            oos.close();
            bos.close();
            byte[] byteData = bos.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
            return (AnomalyDetection) new ObjectInputStream(bais).readObject();
        } catch (IOException e) {
            logger.severe(e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.severe(e.getMessage());
        }
        return null;
    }

    private void logOptimizationScores(List<AnomalyDetectionPerformance> results) {
        DescriptiveStatistics errorStats = new DescriptiveStatistics();
        DescriptiveStatistics thresholdStats = new DescriptiveStatistics();
        DescriptiveStatistics falseAlarmsStats = new DescriptiveStatistics();
        DescriptiveStatistics scoreStats = new DescriptiveStatistics();
        for (AnomalyDetectionPerformance performance : results) {
            errorStats.addValue(performance.getErrors());
            thresholdStats.addValue(performance.getThresholds());
            falseAlarmsStats.addValue(performance.getFalseAlarms());
            scoreStats.addValue(this.getScore(performance));
        }
        logger.info("Error has AVG:" + errorStats.getMean() + " STD:" + errorStats.getStandardDeviation() + " MIN:" + errorStats.getMin() + " MAX:" + errorStats.getMax());
        logger.info("Threshold has AVG:" + thresholdStats.getMean() + " STD:" + thresholdStats.getStandardDeviation() + " MIN:" + thresholdStats.getMin() + " MAX:" + thresholdStats.getMax());
        logger.info("Alarms has AVG:" + falseAlarmsStats.getMean() + " STD:" + falseAlarmsStats.getStandardDeviation() + " MIN:" + falseAlarmsStats.getMin() + " MAX:" + falseAlarmsStats.getMax());
        logger.info("Score has AVG:" + scoreStats.getMean() + " STD:" + scoreStats.getStandardDeviation() + " MIN:" + scoreStats.getMin() + " MAX:" + scoreStats.getMax());
    }

    private void logModels(List<Map<String, Double>> modelStats) {
        Map<String, DescriptiveStatistics> resultStats = new HashMap<>();
        for (Map<String, Double> stat : modelStats) {
            for (Map.Entry<String, Double> statEntry : stat.entrySet()) {
                if (resultStats.containsKey(statEntry.getKey())) {
                    resultStats.get(statEntry.getKey()).addValue(statEntry.getValue());
                } else {
                    DescriptiveStatistics aggStats = new DescriptiveStatistics();
                    aggStats.addValue(statEntry.getValue());
                    resultStats.put(statEntry.getKey(), aggStats);
                }
            }
        }
        for (Map.Entry<String, DescriptiveStatistics> statsEntry : resultStats.entrySet()) {
            logger.info(statsEntry.getKey() + " has AVG:" + statsEntry.getValue().getMean() + " STD:" + statsEntry.getValue().getStandardDeviation() + " MIN:" + statsEntry.getValue().getMin() + " MAX:" + statsEntry.getValue().getMax());
        }
    }

    protected abstract AnomalyDetection breed(AnomalyDetection parent1, AnomalyDetection parent2);

    protected boolean mutate() {
        return Math.random() < mutationProbability;
    }

    protected static AnomalyDetection selectParent(AnomalyDetection parent1, AnomalyDetection parent2) {
        boolean isParent1 = random.nextBoolean();
        if (isParent1) {
            return parent1;
        } else {
            return parent2;
        }
    }

    public double getScore(AnomalyDetectionPerformance result) {
        double e = 1.0 - Math.tanh(result.getErrors());
        double th = 1.0 - Math.tanh(result.getThresholds());
        double distThE = activationFunctionDistThresholdError(result.getThresholds() - result.getErrors(), result.getErrors());
        return scoreFactorError * e + scoreFactorThreshold * th + scoreFactorDistThErr * distThE + scoreFactorFalseAlarms * activationFunctionFalseAlarms(result.getFalseAlarms());
    }

    private double activationFunctionFalseAlarms(double falseAlarms) {
        if (falseAlarms > activationFalseAlarmBound) {
            return 0.0;
        }
        return (1.0 - falseAlarms);
    }

    private double activationFunctionDistThresholdError(double dist, double error) {
        double minThresholdBound = (1.0 + activationDistBound) * error;
        if (dist < minThresholdBound) {
            return 0.0;
        }
        return 1.0 - Math.tanh(dist);
    }

    public abstract List<AnomalyDetection> createRandomSet(int numDetectors);

    public int getNumPopulation() {
        return numPopulation;
    }
}
