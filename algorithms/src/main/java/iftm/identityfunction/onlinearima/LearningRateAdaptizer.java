package iftm.identityfunction.onlinearima;

import java.io.Serializable;

/**
 * @author kevinstyp
 */
public class LearningRateAdaptizer implements Serializable {
    private final double factor;
    private double baseRate;
    private final double initialLearnRate;
    private final int numSamplesLearningRateAdaption;
    private double rate;

    LearningRateAdaptizer(double baseRate, double initialLearnRate, int numSamplesLearningRateAdaption) {
        this.baseRate = baseRate;
        factor = Math.pow((1.0 / initialLearnRate), (1.0 / numSamplesLearningRateAdaption));
        rate = baseRate * initialLearnRate;
        this.numSamplesLearningRateAdaption = numSamplesLearningRateAdaption;
        this.initialLearnRate = initialLearnRate;
    }

    public double getRate() {
        rate = rate * factor;
        if (factor > 1.0 && rate > baseRate) {
            rate = baseRate;
        }
        return rate;
    }

    public void setBaseRate(double baseRate) {
        this.baseRate = baseRate;
    }

    public double getBaseRate() {
        return baseRate;
    }

    public int getNumSamplesLearningRateAdaption() {
        return numSamplesLearningRateAdaption;
    }

    public double getInitialLearnRate() {
        return initialLearnRate;
    }

    public void setRate(double rate) {
        this.rate = rate;
        this.baseRate = rate;
    }

}
