package iftm.identityfunction;

import iftm.identityfunction.onlinearima.AbstractModelArima;
import iftm.identityfunction.onlinearima.EfficientModelONS;

public class OnlineArimaIdentityFunction extends AbstractOnlineArimaIdentityFunction {

    public OnlineArimaIdentityFunction(int mk, int d) {
        super(mk, d);
    }

    public OnlineArimaIdentityFunction(int mk, int d, double initialLearnRate, int numSamplesLearningRateAdaption) {
        super(mk, d, initialLearnRate, numSamplesLearningRateAdaption);
    }

    @Override
    public IdentityFunction newInstance() {
        return new OnlineArimaIdentityFunction(getLinearComponents(), getIntegralDepth(), getInitialLearnRate(), getNumSamplesLearningRateAdaption());
    }

    @Override
    public AbstractModelArima newModel() {
        AbstractModelArima newModel = new EfficientModelONS(getLinearComponents(), getIntegralDepth(), getInitialLearnRate(), getNumSamplesLearningRateAdaption());
        if (gammaValues != null) {
            // If initial gamma values are defined, set them to new models.
            newModel.setGamma(gammaValues);
        }
        return newModel;
    }

}
