package iftm.thresholdmodel.window;

import iftm.thresholdmodel.ThresholdModel;

public class StaticThreshold extends BatchWindowThreshold {

    private final double threshold;

    public StaticThreshold(int windowSize, double threshold) {
        super(windowSize);
        this.threshold = threshold;
    }

    @Override
    public ThresholdModel newInstance() {
        return new StaticThreshold(getWindowSize(), threshold);
    }

    @Override
    public void addValue(double value) {
        //Value never used as the threshold stays at it is defined by the user.
    }

    @Override
    public double getThreshold() {
        return threshold;
    }

}
