package iftm.anomalydetection.automl.identityfunctions;

import iftm.identityfunction.AutoencoderLSTMIdentityFunction;
import iftm.identityfunction.DL4JIdentityFunction;

public class AutoencoderLSTMBreeding extends AbstractDL4JBreeding {

    public AutoencoderLSTMBreeding(int numPopulation, int maxLayers, ThresholdBreedingPart thresholdBreeding) {
        super(numPopulation, maxLayers, thresholdBreeding);
    }

    public AutoencoderLSTMBreeding(int numPopulation, int maxLayers, ThresholdBreedingPart thresholdBreeding, double percBest, double randomOld, double mutationProbability, double scoreFactorError, double scoreFactorThreshold, double scoreFactorDistThErr, double scoreFactorFalseAlarms, double activationFalseAlarmBound, double activationDistBound) {
        super(numPopulation, maxLayers, thresholdBreeding, percBest, randomOld, mutationProbability, scoreFactorError, scoreFactorThreshold, scoreFactorDistThErr, scoreFactorFalseAlarms, activationFalseAlarmBound, activationDistBound);
    }

    @Override
    DL4JIdentityFunction getModel(int layers, double learningRate) {
        return new AutoencoderLSTMIdentityFunction(layers, learningRate);
    }
}
