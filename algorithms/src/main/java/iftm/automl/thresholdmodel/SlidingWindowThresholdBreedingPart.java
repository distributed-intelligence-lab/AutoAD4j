package iftm.automl.thresholdmodel;

import iftm.anomalydetection.IFTMAnomalyDetection;
import iftm.thresholdmodel.OneSidedThreshold;
import iftm.thresholdmodel.StandardSlidingWindowThreshold;

import java.util.Random;

public class SlidingWindowThresholdBreedingPart implements ThresholdBreedingPart {

    private final Random random = new Random();
    private final double minSigma;
    private final double maxSigma;
    private final int maxWindowSize;

    public SlidingWindowThresholdBreedingPart(int maxWindowSize, double maxSigma) {
        this(maxWindowSize, 0.0, maxSigma);
    }

    public SlidingWindowThresholdBreedingPart(int maxWindowSize, double minSigma, double maxSigma) {
        this.maxSigma = maxSigma;
        this.maxWindowSize = maxWindowSize;
        this.minSigma = minSigma;
    }

    @Override
    public OneSidedThreshold get(IFTMAnomalyDetection detector, boolean mutate) {
        StandardSlidingWindowThreshold thresholdModel = (StandardSlidingWindowThreshold) detector.getThresholdModel();
        double sigma = thresholdModel.getSigmaFactor();
        if (mutate) {
            sigma = minSigma + (maxSigma - minSigma) * random.nextDouble();
        }
        int windowSize = thresholdModel.getWindowSize();
        if (mutate) {
            windowSize = random.nextInt(maxWindowSize) + 1;
        }
        return new StandardSlidingWindowThreshold(windowSize, sigma);
    }

    @Override
    public OneSidedThreshold random() {
        double sigma = minSigma + (maxSigma - minSigma) * random.nextDouble();
        int windowSize = random.nextInt(maxWindowSize) + 1;
        return new StandardSlidingWindowThreshold(windowSize, sigma);
    }
}
