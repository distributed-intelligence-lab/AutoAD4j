package iftm.automl.identitifunction;

import iftm.automl.thresholdmodel.ThresholdBreedingPart;
import iftm.identityfunction.IdentityFunction;
import iftm.identityfunction.OnlineArimaMultiIdentityFunction;

public class ArimaMultiBreeding extends AbstractArimaBreeding {

    public ArimaMultiBreeding(int numPopulation, int maxD, int maxMK, ThresholdBreedingPart thresholdBreeding) {
        super(numPopulation, maxD, maxMK, thresholdBreeding);
    }

    public ArimaMultiBreeding(int numPopulation, int maxD, int maxMK, ThresholdBreedingPart thresholdBreeding, double percBest, double randomOld, double mutationProbability, double scoreFactorError, double scoreFactorThreshold, double scoreFactorDistThErr, double scoreFactorFalseAlarms, double activationFalseAlarmBound, double activationDistBound) {
        super(numPopulation, maxD, maxMK, thresholdBreeding, percBest, randomOld, mutationProbability, scoreFactorError, scoreFactorThreshold, scoreFactorDistThErr, scoreFactorFalseAlarms, activationFalseAlarmBound, activationDistBound);
    }

    @Override
    IdentityFunction getArimaModel(int d, int mk) {
        return new OnlineArimaMultiIdentityFunction(mk, d);
    }

}
