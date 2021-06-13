package iftm.thresholdmodel;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author fschmidt
 */
public abstract class AvgStdThresholdModel extends OneSidedThreshold {

    protected final double sigmaFactor;
    protected double avg;
    protected double std;

    public AvgStdThresholdModel(double sigmaFactor) {
        this.sigmaFactor = sigmaFactor;
    }

    public AvgStdThresholdModel() {
        this(1);
    }

    @Override
    public double getThreshold() {
        return avg + (sigmaFactor * std);
    }

    public double getSigmaFactor() {
        return sigmaFactor;
    }

    public double getAvg() {
        return avg;
    }

    public double getStd() {
        return std;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    public void setStd(double std) {
        this.std = std;
    }

    @Override
    public Map<String, Double> getStatistics() {
        Map<String, Double> stats = new HashMap<>();
        stats.put("threshold.hyperparameter.sigmaFactor", sigmaFactor);
        stats.put("threshold.avg", avg);
        stats.put("threshold.std", std);
        stats.put("threshold.threshold", getThreshold());
        return stats;
    }

    @Override
    public void clear() {
        this.avg = 0.0;
        this.std = 0.0;
    }
}
