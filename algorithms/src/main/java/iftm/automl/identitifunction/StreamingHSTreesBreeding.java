package iftm.automl.identitifunction;

import iftm.anomalydetection.AnomalyDetection;
import iftm.anomalydetection.IFTMAnomalyDetection;
import iftm.automl.UnsupervisedHyperParameterOptimization;
import iftm.errorfunction.L2NormModelResultError;
import iftm.identityfunction.StreamingHSTreesIdentityFunction;
import iftm.thresholdmodel.window.ExponentialStandardDeviation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StreamingHSTreesBreeding extends UnsupervisedHyperParameterOptimization {

    //Parameters: nodeEntries, maxDecay, decayGrowth, maxSigma
    private final int maxNumTrees;
    private final int maxMaxDepth;
    private final int maxWindowSize;
    private final double maxSigma;
    private final double maxWeightMostRecent;
    private final Random random = new Random();

    public StreamingHSTreesBreeding(int numPopulation, int maxNumTrees, int maxMaxDepth, int maxWindowSize, double maxWeightMostRecent, double maxSigma) {
        super(numPopulation);
        this.maxNumTrees = maxNumTrees;
        this.maxMaxDepth = maxMaxDepth;
        this.maxWindowSize = maxWindowSize;
        this.maxSigma = maxSigma;
        this.maxWeightMostRecent = maxWeightMostRecent;
    }

    @Override
    protected AnomalyDetection breed(AnomalyDetection parent1, AnomalyDetection parent2) {
        //if parameters
        AnomalyDetection detector = selectParent(parent1, parent2);
        int nrOfTrees = ((StreamingHSTreesIdentityFunction) ((IFTMAnomalyDetection)detector).getIdentityFunction()).getNrOfTrees();
        if (mutate()) {
            nrOfTrees = random.nextInt(maxNumTrees - 2 + 1) + 2;
        }
        detector = selectParent(parent1, parent2);
        int maxDepth = ((StreamingHSTreesIdentityFunction) ((IFTMAnomalyDetection)detector).getIdentityFunction()).getMaxDepth();
        if (mutate()) {
            maxDepth = random.nextInt(maxMaxDepth - 2 + 1) + 2;
        }
        detector = selectParent(parent1, parent2);
        int windowSize = ((StreamingHSTreesIdentityFunction) ((IFTMAnomalyDetection)detector).getIdentityFunction()).getWindowSize();
        if (mutate()) {
            windowSize = random.nextInt(maxWindowSize - 2 + 1) + 2;
        }
        //tm parameters
        detector = selectParent(parent1, parent2);
        double sigma = ((ExponentialStandardDeviation) ((IFTMAnomalyDetection)detector).getThresholdModel()).getSigma();
        if (mutate()) {
            sigma = maxSigma * random.nextDouble();
        }
        detector = selectParent(parent1, parent2);
        double weightMostRecent = ((ExponentialStandardDeviation) ((IFTMAnomalyDetection)detector).getThresholdModel()).getWeightMostRecent();
        if (mutate()) {
            weightMostRecent = maxWeightMostRecent * random.nextDouble();
        }
        return new IFTMAnomalyDetection(new StreamingHSTreesIdentityFunction(nrOfTrees, maxDepth, windowSize),
                new L2NormModelResultError(), new ExponentialStandardDeviation(windowSize, weightMostRecent, sigma));
    }

    @Override
    public List<AnomalyDetection> createRandomSet(int numDetectors) {
        List<AnomalyDetection> randomSet = new ArrayList<>(numDetectors);
        for (int i = 0; i < numDetectors; i++) {
            int nrOfTrees = random.nextInt(maxNumTrees - 2 + 1) + 2;
            int maxDepth = random.nextInt(maxMaxDepth - 2 + 1) + 2;
            int windowSize = random.nextInt(maxWindowSize - 2 + 1) + 2;
            double sigma = maxSigma * random.nextDouble();
            double weightMostRecent = maxWeightMostRecent * random.nextDouble();
            randomSet.add(new IFTMAnomalyDetection(new StreamingHSTreesIdentityFunction(nrOfTrees, maxDepth, windowSize),
                    new L2NormModelResultError(), new ExponentialStandardDeviation(windowSize, weightMostRecent, sigma)));
        }
        return randomSet;
    }
}
