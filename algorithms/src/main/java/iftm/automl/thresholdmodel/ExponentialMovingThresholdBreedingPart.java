package iftm.automl.thresholdmodel;

import iftm.anomalydetection.IFTMAnomalyDetection;
import iftm.thresholdmodel.ExponentialMovingAvgStd;
import iftm.thresholdmodel.OneSidedThreshold;

import java.util.Random;

public class ExponentialMovingThresholdBreedingPart implements ThresholdBreedingPart {

    private final Random random = new Random();
    private final double minSigma;
    private final double maxSigma;
    private final double maxAlpha;

    public ExponentialMovingThresholdBreedingPart(double maxSigma, double maxAlpha) {
        this(0.0, maxSigma, maxAlpha);
    }

    public ExponentialMovingThresholdBreedingPart(double minSigma, double maxSigma, double maxAlpha) {
        this.maxAlpha = maxAlpha;
        this.maxSigma = maxSigma;
        this.minSigma = minSigma;
    }

    @Override
    public OneSidedThreshold get(IFTMAnomalyDetection detector, boolean mutate) {
        ExponentialMovingAvgStd thresholdModel = (ExponentialMovingAvgStd) detector.getThresholdModel();
        double sigma = thresholdModel.getSigmaFactor();
        if (mutate) {
            sigma = minSigma + (maxSigma - minSigma) * random.nextDouble();
        }
        double alpha = thresholdModel.getAlpha();
        if (mutate) {
            alpha = maxAlpha * random.nextDouble();
        }
        return new ExponentialMovingAvgStd(alpha, sigma);
    }

    @Override
    public OneSidedThreshold random() {
        double sigma = minSigma + (maxSigma - minSigma) * random.nextDouble();
        double alpha = maxAlpha * random.nextDouble();
        return new ExponentialMovingAvgStd(alpha, sigma);
    }

}
