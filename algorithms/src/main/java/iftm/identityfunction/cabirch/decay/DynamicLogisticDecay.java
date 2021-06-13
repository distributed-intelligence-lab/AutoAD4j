package iftm.identityfunction.cabirch.decay;

/**
 *
 * @author fschmidt
 */
public class DynamicLogisticDecay implements DecayFunction {

    private final double growthRate;

    public DynamicLogisticDecay(){
        growthRate = 0.01;
    }

    public DynamicLogisticDecay(double growthRate) {
        this.growthRate = growthRate;
    }

    @Override
    public double getValue(int t, double... parameters) {
        LogisticFunction logistics = new LogisticFunction(parameters[0], 1, growthRate, 1, 0, 0.1);
        return logistics.value(t);
    }

    public double getGrowthRate() {
        return growthRate;
    }
}
