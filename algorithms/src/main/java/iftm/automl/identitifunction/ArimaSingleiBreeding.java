package iftm.automl.identitifunction;

import iftm.automl.thresholdmodel.ThresholdBreedingPart;
import iftm.identityfunction.IdentityFunction;
import iftm.identityfunction.OnlineArimaIdentityFunction;

public class ArimaSingleiBreeding extends AbstractArimaBreeding {

    public ArimaSingleiBreeding(int numPopulation, int maxD, int maxMK, ThresholdBreedingPart thresholdBreeding) {
        super(numPopulation, maxD, maxMK, thresholdBreeding);
    }

    @Override
    IdentityFunction getArimaModel(int d, int mk) {
        return new OnlineArimaIdentityFunction(mk, d);
    }
}
