package iftm.automl.identitifunction;

import iftm.anomalydetection.AnomalyDetection;
import iftm.anomalydetection.IFTMAnomalyDetection;
import iftm.automl.UnsupervisedHyperParameterOptimization;
import iftm.automl.thresholdmodel.ThresholdBreedingPart;
import iftm.errorfunction.EuclideanError;
import iftm.identityfunction.AbstractArimaIdentityFunction;
import iftm.identityfunction.IdentityFunction;
import iftm.thresholdmodel.OneSidedThreshold;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class AbstractArimaBreeding extends UnsupervisedHyperParameterOptimization {

    private final Random random = new Random();
    private final int maxD;
    private final int maxMK;
    private final ThresholdBreedingPart thresholdBreeding;

    public AbstractArimaBreeding(int numPopulation, int maxD, int maxMK, ThresholdBreedingPart thresholdBreeding) {
        super(numPopulation);
        this.maxD = maxD;
        this.maxMK = maxMK;
        this.thresholdBreeding = thresholdBreeding;
    }

    public AbstractArimaBreeding(int numPopulation, int maxD, int maxMK, ThresholdBreedingPart thresholdBreeding, double percBest, double randomOld, double mutationProbability, double scoreFactorError, double scoreFactorThreshold, double scoreFactorDistThErr, double scoreFactorFalseAlarms, double activationFalseAlarmBound, double activationDistBound) {
        super(numPopulation, percBest, randomOld, mutationProbability, scoreFactorError, scoreFactorThreshold, scoreFactorDistThErr, scoreFactorFalseAlarms, activationFalseAlarmBound, activationDistBound);
        this.maxD = maxD;
        this.maxMK = maxMK;
        this.thresholdBreeding = thresholdBreeding;
    }

    abstract IdentityFunction getArimaModel(int d, int mk);

    @Override
    protected AnomalyDetection breed(AnomalyDetection parent1, AnomalyDetection parent2) {
        //d
        int d = getD(parent1, parent2);
        //mk
        int mk = getMk(parent1, parent2);
        //tm parameters
        OneSidedThreshold tm = getTM(parent1, parent2);
        return new IFTMAnomalyDetection(getArimaModel(d, mk), new EuclideanError(), tm);
    }


    protected int getD(AnomalyDetection parent1, AnomalyDetection parent2){
        AnomalyDetection detector = selectParent(parent1, parent2);
        int d = ((AbstractArimaIdentityFunction) ((IFTMAnomalyDetection) detector).getIdentityFunction()).getIntegralDepth();
        if (mutate()) {
            d = random.nextInt(maxD - 1 + 1) + 1;
        }
        return d;
    }

    protected int getMk(AnomalyDetection parent1, AnomalyDetection parent2){
        AnomalyDetection detector = selectParent(parent1, parent2);
        int d = ((AbstractArimaIdentityFunction) ((IFTMAnomalyDetection) detector).getIdentityFunction()).getIntegralDepth();
        if (mutate()) {
            d = random.nextInt(maxD - 1 + 1) + 1;
        }
        return d;
    }

    protected OneSidedThreshold getTM(AnomalyDetection parent1, AnomalyDetection parent2) {
        AnomalyDetection detector = selectParent(parent1, parent2);
        OneSidedThreshold tm = thresholdBreeding.get((IFTMAnomalyDetection) detector, mutate());
        return tm;
    }

    @Override
    public List<AnomalyDetection> createRandomSet(int numDetectors) {
        List<AnomalyDetection> detectors = new ArrayList<>();
        for (int i = 0; i < numDetectors; i++) {
            int d = random.nextInt(maxD - 1 + 1) + 1;
            int mk = random.nextInt(maxMK - 2 + 1) + 2;
            detectors.add(new IFTMAnomalyDetection(getArimaModel(d, mk),
                    new EuclideanError(), thresholdBreeding.random()));
        }
        return detectors;
    }
}
