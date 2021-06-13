package iftm.identityfunction;

import iftm.identityfunction.onlinearima.EfficientModelONSmulti;

public class OnlineArimaMultiIdentityFunction extends AbstractArimaIdentityFunction {

    private EfficientModelONSmulti multiModelONS;
    private final double initialLearnRate;
    private final int numSamplesLearningRateAdaption;

    public OnlineArimaMultiIdentityFunction(int mk, int d) {
        this(mk, d, 1, 1);
    }

    public OnlineArimaMultiIdentityFunction(int mk, int d, double initialLearnRate, int numSamplesLearningRateAdaption) {
        super(mk, d);
        this.initialLearnRate = initialLearnRate;
        this.numSamplesLearningRateAdaption = numSamplesLearningRateAdaption;
    }

    /**
     * make a prediction for each metric
     *
     * @param values
     * @return
     */
    @Override
    public double[] predict(double[] values) {
        if (multiModelONS == null) {
            multiModelONS = new EfficientModelONSmulti(getLinearComponents(), getIntegralDepth(), values.length, initialLearnRate, numSamplesLearningRateAdaption);
        }
        return multiModelONS.predict();
    }

    @Override
    public void train(double[] values) {
        if (multiModelONS == null) {
            multiModelONS = new EfficientModelONSmulti(getLinearComponents(), getIntegralDepth(), values.length, initialLearnRate, numSamplesLearningRateAdaption);
        }
        multiModelONS.train(values);
    }

    @Override
    public IdentityFunction newInstance() {
        return new OnlineArimaMultiIdentityFunction(getLinearComponents(), getIntegralDepth());
    }

    public EfficientModelONSmulti getMultiModelONS(){
        return multiModelONS;
    }
}
