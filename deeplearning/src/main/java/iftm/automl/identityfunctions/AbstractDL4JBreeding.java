package iftm.automl.identityfunctions;

import iftm.anomalydetection.AnomalyDetection;
import iftm.anomalydetection.IFTMAnomalyDetection;
import iftm.automl.UnsupervisedHyperParameterOptimization;
import iftm.automl.thresholdmodel.ThresholdBreedingPart;
import iftm.errorfunction.EuclideanError;
import iftm.identityfunction.DL4JIdentityFunction;
import iftm.thresholdmodel.OneSidedThreshold;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class AbstractDL4JBreeding extends UnsupervisedHyperParameterOptimization {

    private final int maxLayers;
    private final Random random = new Random();
    private final ThresholdBreedingPart thresholdBreeding;

    public AbstractDL4JBreeding(int numPopulation, int maxLayers, ThresholdBreedingPart thresholdBreeding) {
        super(numPopulation);
        this.maxLayers = maxLayers;
        this.thresholdBreeding = thresholdBreeding;
    }

    public AbstractDL4JBreeding(int numPopulation, int maxLayers, ThresholdBreedingPart thresholdBreeding, double percBest, double randomOld, double mutationProbability, double scoreFactorError, double scoreFactorThreshold, double scoreFactorDistThErr, double scoreFactorFalseAlarms, double activationFalseAlarmBound, double activationDistBound) {
        super(numPopulation, percBest, randomOld, mutationProbability, scoreFactorError, scoreFactorThreshold, scoreFactorDistThErr, scoreFactorFalseAlarms, activationFalseAlarmBound, activationDistBound);
        this.maxLayers = maxLayers;
        this.thresholdBreeding = thresholdBreeding;
    }

    @Override
    protected AnomalyDetection breed(AnomalyDetection parent1, AnomalyDetection parent2) {
        AnomalyDetection detector = selectParent(parent1, parent2);
        int layers = ((DL4JIdentityFunction) ((IFTMAnomalyDetection)detector).getIdentityFunction()).getLayers();
        if (mutate()) {
            layers = random.nextInt(maxLayers - 1 + 1) + 1;
        }
        detector = selectParent(parent1, parent2);
        double learningRate = ((DL4JIdentityFunction) ((IFTMAnomalyDetection)detector).getIdentityFunction()).getLearningRate();
        if (mutate()) {
            learningRate = random.nextDouble();
        }
        //tm parameters
        detector = selectParent(parent1, parent2);
        OneSidedThreshold tm = thresholdBreeding.get((IFTMAnomalyDetection) detector, mutate());
        return new IFTMAnomalyDetection(getModel(layers, learningRate), new EuclideanError(), tm);
    }

    @Override
    public List<AnomalyDetection> createRandomSet(int numDetectors) {
        List<AnomalyDetection> randomSet = new ArrayList<>(numDetectors);
        for (int i = 0; i < numDetectors; i++) {
            int layers = random.nextInt(maxLayers - 1 + 1) + 1;
            double learningRate = random.nextDouble();
            randomSet.add(new IFTMAnomalyDetection(getModel(layers, learningRate),
                    new EuclideanError(), thresholdBreeding.random()));
        }
        return randomSet;
    }

    abstract DL4JIdentityFunction getModel(int layers, double learningRate);
}
