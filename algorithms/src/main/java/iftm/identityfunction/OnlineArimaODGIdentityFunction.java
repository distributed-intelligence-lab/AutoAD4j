package iftm.identityfunction;

import iftm.identityfunction.onlinearima.AbstractModelArima;
import iftm.identityfunction.onlinearima.ModelODG;
import iftm.identityfunction.onlinearima.learningoptimizer.LearningRateOptimizers;

/**
 * @author kevinstyp
 */
public class OnlineArimaODGIdentityFunction extends AbstractOnlineArimaIdentityFunction {
    private LearningRateOptimizers learnMode;
    private double[] args;


    public OnlineArimaODGIdentityFunction(int mk, int d) {
        super(mk, d);
        this.learnMode = LearningRateOptimizers.SIMPLE;
        this.args = new double[0];
    }

    public OnlineArimaODGIdentityFunction(int mk, int d, double initialLearnRate, int numSamplesLearningRateAdaption,
                                          LearningRateOptimizers learnMode, double... args) {
        super(mk, d, initialLearnRate, numSamplesLearningRateAdaption);
        this.learnMode = learnMode;
        this.args = args;
    }

    @Override
    public IdentityFunction newInstance() {
        return new OnlineArimaODGIdentityFunction(getLinearComponents(), getIntegralDepth(), getInitialLearnRate(),
                getNumSamplesLearningRateAdaption(), learnMode);
    }

    @Override
    public AbstractModelArima newModel() {
        AbstractModelArima newModel = new ModelODG(getLinearComponents(), getIntegralDepth(), getInitialLearnRate(),
                getNumSamplesLearningRateAdaption(), learnMode, args);
        if (gammaValues != null) {
            // If initial gamma values are defined, set them to new models.
            newModel.setGamma(gammaValues);
        }
        return newModel;
    }

    public void setLearnMode(LearningRateOptimizers learnMode, double... args){
        for (AbstractModelArima model : models) {
            ((ModelODG) model).setMode(learnMode, args);
        }
    }

    public void setLearnRate(double learnRate) {
        if(models == null) return;
        for (AbstractModelArima model : models) {
            ((ModelODG) model).setLearningRate(learnRate);
        }
    }
}
