package iftm.automl.identitifunction;

import iftm.anomalydetection.AnomalyDetection;
import iftm.anomalydetection.IFTMAnomalyDetection;
import iftm.automl.thresholdmodel.ThresholdBreedingPart;
import iftm.errorfunction.EuclideanError;
import iftm.identityfunction.IdentityFunction;
import iftm.identityfunction.onlinearima.AbstractModelArima;
import iftm.thresholdmodel.OneSidedThreshold;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class AbstractArimaBreedingExpo extends AbstractArimaBreeding {

    private final Random random = new Random();
    private final int maxD;
    private final int maxMK;
    private final double maxInitialLearningRate;
    private final int maxNumSamples;
    private final ThresholdBreedingPart thresholdBreeding;

    public AbstractArimaBreedingExpo(int numPopulation, int maxD, int maxMK, ThresholdBreedingPart thresholdBreeding, double maxInitialLearningRate, int maxNumSamples) {
        super(numPopulation, maxD, maxMK, thresholdBreeding);
        this.maxD = maxD;
        this.maxMK = maxMK;
        this.maxInitialLearningRate = maxInitialLearningRate;
        this.maxNumSamples = maxNumSamples;
        this.thresholdBreeding = thresholdBreeding;
    }

    abstract IdentityFunction getArimaModel(int d, int mk, double initialLearningRate, int numSamples);

    @Override
    protected AnomalyDetection breed(AnomalyDetection parent1, AnomalyDetection parent2) {
        //d
        int d = getD(parent1, parent2);
        //mk
        int mk = getMk(parent1, parent2);
        //initialLearningRate
        AnomalyDetection detector = selectParent(parent1, parent2);
        double learningRate = ((AbstractModelArima) ((IFTMAnomalyDetection) detector).getIdentityFunction()).getLearningRateAdaptizer().getInitialLearnRate();
        if (mutate()) {
            learningRate = 1.0 / (random.nextDouble() * maxInitialLearningRate);
        }
        //numSamples
        detector = selectParent(parent1, parent2);
        int numSamples = ((AbstractModelArima) ((IFTMAnomalyDetection) detector).getIdentityFunction()).getLearningRateAdaptizer().getNumSamplesLearningRateAdaption();
        if (mutate()) {
            numSamples = random.nextInt(maxNumSamples - 2 + 1) + 2;
        }
        //tm parameters
        OneSidedThreshold tm = getTM(parent1, parent2);
        return new IFTMAnomalyDetection(getArimaModel(d, mk, learningRate, numSamples), new EuclideanError(), tm);
    }

    @Override
    public List<AnomalyDetection> createRandomSet(int numDetectors) {
        List<AnomalyDetection> detectors = new ArrayList<>();
        for (int i = 0; i < numDetectors; i++) {
            int d = random.nextInt(maxD - 1 + 1) + 1;
            int mk = random.nextInt(maxMK - 2 + 1) + 2;
            int numSamples = random.nextInt(maxNumSamples - 2 + 1) + 2;
            double learningRate = 1.0 / (random.nextDouble() * maxInitialLearningRate);
            detectors.add(new IFTMAnomalyDetection(getArimaModel(d, mk, learningRate, numSamples),
                    new EuclideanError(), thresholdBreeding.random()));
        }
        return detectors;
    }
}
