package iftm.thresholdmodel.window;

import iftm.thresholdmodel.ThresholdModel;


public class ExponentialMovingAverage extends BatchWindowThreshold {

    private final boolean normalsOnly; // only use normal scores for weighted average if true
    private double weightedAverageNormal; // keeps the weighted average of Normal samples.
    private final double weightMostRecent; // how strongly the most recent normal value is supposed to be weighted.
    private final double percentage; // Percentage under normal average that we consider anomaly.


    public ExponentialMovingAverage(int windowSize, double weightMostRecent, double percentage, boolean normalsOnly){
        super(windowSize);
        this.weightMostRecent = weightMostRecent;
        this.percentage = percentage;
        this.normalsOnly = normalsOnly;
        weightedAverageNormal = 0;
    }

    @Override
    public double getThreshold() {
        return percentage * weightedAverageNormal;
    }

    @Override
    public void addValue(double value) {
        if(!normalsOnly || isIncluded(value)) {
            weightedAverageNormal = ((1 - weightMostRecent) * weightedAverageNormal + weightMostRecent * value);
        }
    }

    @Override
    public ThresholdModel newInstance() {
        return new ExponentialMovingAverage(getWindowSize(), weightMostRecent, percentage, normalsOnly);
    }

    @Override
    public void clear() {
        super.clear();
        weightedAverageNormal = 0;
    }
}
