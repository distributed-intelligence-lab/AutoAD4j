package iftm.thresholdmodel;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author fschmidt
 */
public abstract class TwoSidedAvgStdThresholdModel extends TwoSidedThreshold {

    protected final double sigmaFactor;
    protected double avg;
    protected double std;

    public TwoSidedAvgStdThresholdModel(double sigmaFactor) {
        this.sigmaFactor = sigmaFactor;
    }

    public TwoSidedAvgStdThresholdModel() {
        this(1);
    }

    @Override
    public double getLowerThreshold() {
        return avg - (sigmaFactor * std);
    }

    @Override
    public double getUpperThreshold() {
        return avg + (sigmaFactor * std);
    }

    public double getSigmaFactor() {
        return sigmaFactor;
    }

    @Override
    public Map<String, Double> getStatistics() {
        Map<String, Double> stats = new HashMap<>();
        stats.put("threshold.hyperparameter.sigmaFactor", sigmaFactor);
        stats.put("threshold.avg", avg);
        stats.put("threshold.std", std);
        stats.put("threshold.lowerThreshold", getLowerThreshold());
        stats.put("threshold.upperThreshold", getUpperThreshold());
        return stats;
    }
}
