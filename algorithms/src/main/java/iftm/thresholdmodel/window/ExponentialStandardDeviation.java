package iftm.thresholdmodel.window;

import iftm.thresholdmodel.ThresholdModel;

public class ExponentialStandardDeviation extends BatchWindowThreshold {

    private double emAverage;
    private double emVar;
    private double emStdDev;
    private final double weightMostRecent;
    private final double sigma;
    private boolean started;

    public ExponentialStandardDeviation(int windowSize, double weightMostRecent, double sigma) {
        super(windowSize);
        emStdDev = 0;
        this.weightMostRecent = weightMostRecent;
        this.sigma = sigma;
        started = false;
    }

    @Override
    public double getThreshold() {
        return Math.max(0, (emAverage - sigma * emStdDev));
    }

    @Override
    public void addValue(double value) {
        if (!started) { // insert first scored value into running average.
            emAverage = value;
            emVar = 0;
            started = true;
        } else {
            double delta = value - emAverage;
            emAverage = emAverage + weightMostRecent * delta;
            emVar = (1 - weightMostRecent) * (emVar + weightMostRecent * Math.pow(delta, 2));
            emStdDev = Math.sqrt(emVar);
        }
    }

    @Override
    public ThresholdModel newInstance() {
        return new ExponentialStandardDeviation(getWindowSize(), weightMostRecent, sigma);
    }

    public double getWeightMostRecent() {
        return weightMostRecent;
    }

    public double getSigma() {
        return sigma;
    }

    @Override
    public void clear() {
        super.clear();
        emAverage = 0;
        emVar = 0;
        emStdDev = 0;
        started = false;
    }
}
