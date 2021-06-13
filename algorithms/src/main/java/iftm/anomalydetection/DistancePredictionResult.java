package iftm.anomalydetection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DistancePredictionResult implements Serializable{

    private final boolean isAnomaly;
    private final double error;
    private final double[] distance;
    private final double threshold;
    private final List<DistancePredictionResult> ensemblerResults;

    public DistancePredictionResult(boolean isAnomaly, double error, double[] distance, double threshold) {
        this.isAnomaly = isAnomaly;
        this.error = error;
        this.distance = distance;
        this.threshold = threshold;
        ensemblerResults = new ArrayList<>();
    }

    public boolean isAnomaly() {
        return isAnomaly;
    }

    public double getError() {
        return error;
    }

    public double[] getDistance() {
        return distance;
    }

    public double getThreshold() {
        return threshold;
    }

    public List<DistancePredictionResult> getEnsemblerResults() {
        return ensemblerResults;
    }

    public void addEnsemblerResult(List<DistancePredictionResult> ensemblerResults) {
        this.ensemblerResults.addAll(ensemblerResults);
    }
}