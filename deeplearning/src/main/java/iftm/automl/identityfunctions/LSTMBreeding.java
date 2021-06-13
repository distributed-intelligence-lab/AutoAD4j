package iftm.automl.identityfunctions;

import iftm.automl.thresholdmodel.ThresholdBreedingPart;
import iftm.identityfunction.DL4JIdentityFunction;
import iftm.identityfunction.LSTMIdentityFunction;

public class LSTMBreeding extends AbstractDL4JBreeding {

    public LSTMBreeding(int numPopulation, int maxLayers, ThresholdBreedingPart thresholdBreeding) {
        super(numPopulation, maxLayers, thresholdBreeding);
    }

    public LSTMBreeding(int numPopulation, int maxLayers, ThresholdBreedingPart thresholdBreeding, double percBest, double randomOld, double mutationProbability, double scoreFactorError, double scoreFactorThreshold, double scoreFactorDistThErr, double scoreFactorFalseAlarms, double activationFalseAlarmBound, double activationDistBound) {
        super(numPopulation, maxLayers, thresholdBreeding, percBest, randomOld, mutationProbability, scoreFactorError, scoreFactorThreshold, scoreFactorDistThErr, scoreFactorFalseAlarms, activationFalseAlarmBound, activationDistBound);
    }

    @Override
    DL4JIdentityFunction getModel(int layers, double learningRate) {
        return new LSTMIdentityFunction(layers, learningRate);
    }
}
