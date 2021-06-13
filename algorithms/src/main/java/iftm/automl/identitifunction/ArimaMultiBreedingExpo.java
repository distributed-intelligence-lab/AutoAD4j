package iftm.automl.identitifunction;

import iftm.automl.thresholdmodel.ThresholdBreedingPart;
import iftm.identityfunction.IdentityFunction;
import iftm.identityfunction.OnlineArimaMultiIdentityFunction;

public class ArimaMultiBreedingExpo extends AbstractArimaBreedingExpo {

    public ArimaMultiBreedingExpo(int numPopulation, int maxD, int maxMK, ThresholdBreedingPart thresholdBreeding, double maxInitialLearningRate, int maxNumSamples) {
        super(numPopulation, maxD, maxMK, thresholdBreeding, maxInitialLearningRate, maxNumSamples);
    }

    @Override
    IdentityFunction getArimaModel(int d, int mk, double initialLearningRate, int numSamples) {
        return new OnlineArimaMultiIdentityFunction(mk, d, initialLearningRate, numSamples);
    }

    @Override
    IdentityFunction getArimaModel(int d, int mk) {
        return null;
    }
}
