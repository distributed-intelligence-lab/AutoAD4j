package iftm.automl.thresholdmodel;

import iftm.anomalydetection.IFTMAnomalyDetection;
import iftm.thresholdmodel.AvgStdThresholdModel;
import iftm.thresholdmodel.CompleteHistoryAvgStd;
import iftm.thresholdmodel.OneSidedThreshold;

import java.util.Random;

public class CompleteHistoryThresholdBreedingPart implements ThresholdBreedingPart {
    private final Random random = new Random();
    private final double maxSigma;
    private final double minSigma;

    public CompleteHistoryThresholdBreedingPart(double maxSigma) {
        this(0.0, maxSigma);
    }

    public CompleteHistoryThresholdBreedingPart(double minSigma, double maxSigma) {
        this.maxSigma = maxSigma;
        this.minSigma = minSigma;
    }

    @Override
    public OneSidedThreshold get(IFTMAnomalyDetection detector, boolean mutate) {
        double sigma = ((AvgStdThresholdModel) detector.getThresholdModel()).getSigmaFactor();
        if (mutate) {
            sigma = minSigma + (maxSigma - minSigma) * random.nextDouble();
        }
        return new CompleteHistoryAvgStd(sigma);
    }

    @Override
    public OneSidedThreshold random() {
        double sigma = minSigma + (maxSigma - minSigma) * random.nextDouble();
        return new CompleteHistoryAvgStd(sigma);
    }
}
