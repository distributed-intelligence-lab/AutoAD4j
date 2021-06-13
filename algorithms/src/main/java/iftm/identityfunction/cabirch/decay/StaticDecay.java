package iftm.identityfunction.cabirch.decay;

/**
 *
 * @author fschmidt
 */
public class StaticDecay implements DecayFunction {

    private final double value;

    public StaticDecay(){
        value = 1.0;
    }

    public StaticDecay(double value) {
        this.value = value;
    }

    @Override
    public double getValue(int t, double... parameters) {
        return value;
    }
}
