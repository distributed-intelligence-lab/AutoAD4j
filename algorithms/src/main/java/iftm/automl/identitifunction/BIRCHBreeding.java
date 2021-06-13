package iftm.automl.identitifunction;

import iftm.anomalydetection.AnomalyDetection;
import iftm.anomalydetection.IFTMAnomalyDetection;
import iftm.automl.UnsupervisedHyperParameterOptimization;
import iftm.errorfunction.L2NormModelResultError;
import iftm.identityfunction.BirchIdentityFunction;
import iftm.thresholdmodel.AvgStdThresholdModel;
import iftm.thresholdmodel.CompleteHistoryAvgStd;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BIRCHBreeding extends UnsupervisedHyperParameterOptimization {
    //Parameters: nodeEntries, maxDecay, decayGrowth, maxSigma
    private final int maxNodeEntries;
    private final double maxSigma;
    private final Random random = new Random();

    public BIRCHBreeding(int numPopulation, int maxNodeEntries, double maxSigma) {
        super(numPopulation);
        this.maxNodeEntries = maxNodeEntries;
        this.maxSigma = maxSigma;
    }

    public BIRCHBreeding() {
        this(100, 20, 5);
    }

    @Override
    protected AnomalyDetection breed(AnomalyDetection parent1, AnomalyDetection parent2) {
        //if parameters
        AnomalyDetection detector = selectParent(parent1, parent2);
        int nodeEntries = ((BirchIdentityFunction) ((IFTMAnomalyDetection)detector).getIdentityFunction()).getModel().getCfTree().getMaxNodeEntries();
        if (mutate()) {
            nodeEntries = random.nextInt(maxNodeEntries + 1 - 1) + 4;
        }
        //tm parameters
        detector = selectParent(parent1, parent2);
        double sigma = ((AvgStdThresholdModel) ((IFTMAnomalyDetection)detector).getThresholdModel()).getSigmaFactor();
        if (mutate()) {
            sigma = maxSigma * random.nextDouble();
        }
        return new IFTMAnomalyDetection(new BirchIdentityFunction(nodeEntries, 0),
                new L2NormModelResultError(), new CompleteHistoryAvgStd(sigma));
    }

    @Override
    public List<AnomalyDetection> createRandomSet(int numDetectors) {
        List<AnomalyDetection> randomSet = new ArrayList<>(numDetectors);
        for (int i = 0; i < numDetectors; i++) {
            int nodeEntries = random.nextInt(maxNodeEntries + 1 - 1) + 4;
            double sigma = maxSigma * random.nextDouble();
            randomSet.add(new IFTMAnomalyDetection(new BirchIdentityFunction(nodeEntries, 0),
                    new L2NormModelResultError(), new CompleteHistoryAvgStd(sigma)));
        }
        return randomSet;
    }
}
