package iftm.automl;

import iftm.anomalydetection.AnomalyDetection;
import iftm.anomalydetection.IFTMAnomalyDetection;

import java.io.Serializable;

/**
 * Exponential Moving Average to update the results of iftm models
 *
 * @author fschmidt
 */
public class AnomalyDetectionPerformance implements Serializable {

    private final AnomalyDetection detector;
    private double errors = Double.NaN;
    private double thresholds = Double.NaN;
    private int falseAlarms;
    private int countResults;
    public static final double ALPHA = 0.01;

    public AnomalyDetectionPerformance(){
        detector = new IFTMAnomalyDetection();
    }

    public AnomalyDetectionPerformance(AnomalyDetection detector) {
        this.detector = detector;
    }

    public void addResults(double error, double threshold, boolean isFalseAlarm) {
        countResults++;
        errors += error;//exponentialMovingAverage(errors, error);
        thresholds += thresholds;//exponentialMovingAverage(thresholds, threshold);
        if (isFalseAlarm) {
            falseAlarms++;
        }
    }

    public AnomalyDetection getDetector() {
        return detector;
    }

    public double getErrors() {
        return errors / (double) countResults;
    }

    public double getThresholds() {
        return thresholds / (double) countResults;
    }

    public double getFalseAlarms() {
        return (double) falseAlarms / (double) countResults;
    }

    private static double exponentialMovingAverage(double oldValue, double newValue) {
        if (Double.isNaN(oldValue)) {
            oldValue = newValue;
            return oldValue;
        }
        double diffMean = newValue - oldValue;
        double increment = ALPHA * diffMean;
        oldValue = oldValue + increment;
        return oldValue;
    }

    public void clear() {
        errors = Double.NaN;
        thresholds = Double.NaN;
        falseAlarms = 0;
        countResults = 0;
    }

}
