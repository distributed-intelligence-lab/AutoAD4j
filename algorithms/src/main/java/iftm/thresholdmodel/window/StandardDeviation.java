package iftm.thresholdmodel.window;

import iftm.thresholdmodel.ThresholdModel;

public class StandardDeviation extends BatchWindowThreshold {

    private double runningAverage;
    private int counter;
    private double squaredDistances;
    private double std;
    private final double sigma;
    private boolean started;

    public StandardDeviation(int windowSize, double sigma) {
        super(windowSize);
        counter = 0;
        std = 0;
        this.sigma = sigma;
        started = false;
    }

    @Override
    public void addValue(double value) {
        if (!started) { // insert first scored value into running average.
            runningAverage = value;
            counter++;
            started = true;
        } else {
            runningAverage = (counter * runningAverage + value) / (counter + 1);
            squaredDistances += Math.pow(value - runningAverage, 2);
            std = Math.sqrt((squaredDistances / counter));
            counter++;
        }

    }

    @Override
    public ThresholdModel newInstance() {
        return new StandardDeviation(this.getWindowSize(), sigma);
    }

    @Override
    public double getThreshold() {
        return runningAverage - sigma * std;
    }

    @Override
    public void clear() {
        super.clear();
        counter = 0;
        std = 0;
        started = false;
        squaredDistances = 0;
        runningAverage = 0;
    }
}
