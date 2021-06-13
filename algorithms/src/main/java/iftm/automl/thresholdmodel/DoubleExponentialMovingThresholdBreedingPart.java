package iftm.automl.thresholdmodel;

import iftm.anomalydetection.IFTMAnomalyDetection;
import iftm.thresholdmodel.DoubleEMAvgStd;
import iftm.thresholdmodel.OneSidedThreshold;

import java.util.Random;

public class DoubleExponentialMovingThresholdBreedingPart implements ThresholdBreedingPart {

    private final Random random = new Random();
    private final double levelSmoothing;
    private final double trendSmoothing;

    public DoubleExponentialMovingThresholdBreedingPart(double levelSmoothing, double trendSmoothing) {
        this.levelSmoothing = levelSmoothing;
        this.trendSmoothing = trendSmoothing;
    }

    @Override
    public OneSidedThreshold get(IFTMAnomalyDetection detector, boolean mutate) {
        DoubleEMAvgStd thresholdModel = (DoubleEMAvgStd) detector.getThresholdModel();
        double levelSmoothing = thresholdModel.getLevelSmoothing();
        if (mutate) {
            levelSmoothing = this.levelSmoothing * random.nextDouble();
        }
        double trendSmoothing = thresholdModel.getTrendSmoothing();
        if (mutate) {
            trendSmoothing = this.trendSmoothing * random.nextDouble();
        }
        return new DoubleEMAvgStd(levelSmoothing, trendSmoothing);
    }

    @Override
    public OneSidedThreshold random() {
        double levelSmoothing = this.levelSmoothing * random.nextDouble();
        double trendSmoothing = this.trendSmoothing * random.nextDouble();
        return new DoubleEMAvgStd(levelSmoothing, trendSmoothing);
    }
}
