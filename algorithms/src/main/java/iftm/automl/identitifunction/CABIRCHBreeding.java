package iftm.automl.identitifunction;

import iftm.anomalydetection.AnomalyDetection;
import iftm.anomalydetection.IFTMAnomalyDetection;
import iftm.automl.UnsupervisedHyperParameterOptimization;
import iftm.automl.thresholdmodel.ExponentialMovingThresholdBreedingPart;
import iftm.automl.thresholdmodel.ThresholdBreedingPart;
import iftm.errorfunction.L2NormModelResultError;
import iftm.identityfunction.CABirchIdentityFunction;
import iftm.identityfunction.cabirch.decay.StaticLogisticDecay;
import iftm.thresholdmodel.OneSidedThreshold;
import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author fschmidt
 */
public class CABIRCHBreeding extends UnsupervisedHyperParameterOptimization {

    //Parameters: nodeEntries, maxDecay, decayGrowth, maxSigma
    private final int maxNodeEntries;
    private final double maxDecay;
    private final double maxDecayPercent;
    private final Random random = new Random();
    private final ThresholdBreedingPart thresholdBreeding;

    public CABIRCHBreeding(int numPopulation, int maxNodeEntries, double maxDecay, double maxDecayPercent, ThresholdBreedingPart thresholdBreeding, double percBest, double randomOld, double mutationProbability, double scoreFactorError, double scoreFactorThreshold, double scoreFactorDistThErr, double scoreFactorFalseAlarms, double activationFalseAlarmBound, double activationDistBound) {
        super(numPopulation, percBest, randomOld, mutationProbability, scoreFactorError, scoreFactorThreshold, scoreFactorDistThErr, scoreFactorFalseAlarms, activationFalseAlarmBound, activationDistBound);
        this.maxNodeEntries = maxNodeEntries;
        this.maxDecay = maxDecay;
        this.thresholdBreeding = thresholdBreeding;
        this.maxDecayPercent = maxDecayPercent;
    }

    public CABIRCHBreeding(int numPopulation, int maxNodeEntries, double maxDecay, double maxDecayPercent, ThresholdBreedingPart thresholdBreeding) {
        super(numPopulation);
        this.maxNodeEntries = maxNodeEntries;
        this.maxDecay = maxDecay;
        this.thresholdBreeding = thresholdBreeding;
        this.maxDecayPercent = maxDecayPercent;
    }

    public CABIRCHBreeding() {
        this(100, 21, 1.0, 0.08, new ExponentialMovingThresholdBreedingPart(5, 0.001));
    }

    @Override
    protected AnomalyDetection breed(AnomalyDetection parent1, AnomalyDetection parent2) {
        //if parameters
        AnomalyDetection detector = selectParent(parent1, parent2);
        int nodeEntries = ((CABirchIdentityFunction) ((IFTMAnomalyDetection)detector).getIdentityFunction()).getModel().getCfTree().getMaxNodeEntries();
        if (mutate()) {
            nodeEntries = random.nextInt(maxNodeEntries + 1 - 1) + 4;
        }
        detector = selectParent(parent1, parent2);
        double maxDecayFactor = ((StaticLogisticDecay) ((CABirchIdentityFunction) ((IFTMAnomalyDetection)detector).getIdentityFunction()).getModel().getCfTree()
                .getDecayType()).getMaximumValue();
        if (mutate()) {
            maxDecayFactor = 0.01 + (maxDecay - 0.01) * random.nextDouble();
        }
        detector = selectParent(parent1, parent2);
        double decayGrowth = ((StaticLogisticDecay) ((CABirchIdentityFunction) ((IFTMAnomalyDetection)detector).getIdentityFunction()).getModel().getCfTree()
                .getDecayType()).getGrowthRate();
        if (mutate()) {
            decayGrowth = 0.001 + (maxDecayPercent - 0.001) * random.nextDouble();
        }
        //tm parameters
        detector = selectParent(parent1, parent2);
        OneSidedThreshold tm = thresholdBreeding.get((IFTMAnomalyDetection) detector, mutate());
        CABirchIdentityFunction detect = new CABirchIdentityFunction(nodeEntries, 0, new StaticLogisticDecay(maxDecayFactor,
                decayGrowth));
        return new IFTMAnomalyDetection(detect, new L2NormModelResultError(), SerializationUtils.clone(tm));
    }

    @Override
    public List<AnomalyDetection> createRandomSet(int numDetectors) {
        List<AnomalyDetection> randomSet = new ArrayList<>(numDetectors);
        for (int i = 0; i < numDetectors; i++) {
            int nodeEntries = random.nextInt(maxNodeEntries + 1 - 1) + 4;
            double maxDecayFactor = 0.01 + (maxDecay - 0.01) * random.nextDouble();
            double decayGrowth = 0.001 + (maxDecayPercent - 0.001) * random.nextDouble();
            CABirchIdentityFunction detect = new CABirchIdentityFunction(nodeEntries, 0, new StaticLogisticDecay(maxDecayFactor,
                    decayGrowth));
            randomSet.add(new IFTMAnomalyDetection(detect,
                    new L2NormModelResultError(), thresholdBreeding.random()));
        }
        return randomSet;
    }

}
