package iftm.thresholdmodel.window;

import iftm.thresholdmodel.OneSidedThreshold;

import java.util.HashMap;
import java.util.Map;


public abstract class BatchWindowThreshold extends OneSidedThreshold{

    private final int windowSize;
    private int counter;

    // initialise this threshold.
    public BatchWindowThreshold(int windowSize) {
        this.windowSize = windowSize;
        counter = 0;
    }

    // returns false if Sample is recognised as an anomaly, true if it's recognised as a normal sample.
    @Override
    public boolean isIncluded(double anomalyScore) {
        if (!referenceCreated()) {
            return true;
        }
        return anomalyScore > getThreshold();
    }

    public int getWindowSize() {
        return windowSize;
    }

    public boolean referenceCreated() {
        if (counter < windowSize) {
            counter++;
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Map<String, Double> getStatistics() {
        return new HashMap<>();
    }

    @Override
    public void clear() {
        counter = 0;
    }
}
