package iftm.identityfunction.cabirch.decay;

/**
 *
 * @author fschmidt
 */
public class StaticLogisticDecay implements DecayFunction {

    private final LogisticFunction logistics;
    private final double maximumValue;
    private final double growthRate;

    public StaticLogisticDecay(){
        this.maximumValue = 1.0;
        this.growthRate = 0.01;
        logistics = new LogisticFunction(maximumValue, 1, growthRate, 1, 0, 0.1);
    }

    public StaticLogisticDecay(double maximumValue, double growthRate) {
        logistics = new LogisticFunction(maximumValue, 1, growthRate, 1, 0, 0.1);
        this.maximumValue = maximumValue;
        this.growthRate = growthRate;
    }

    @Override
    public double getValue(int t, double... parameters) {
        return logistics.value(t);
    }

    public double getMaximumValue() {
        return maximumValue;
    }

    public double getGrowthRate() {
        return growthRate;
    }

}
