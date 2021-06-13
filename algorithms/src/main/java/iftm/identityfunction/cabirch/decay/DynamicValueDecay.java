package iftm.identityfunction.cabirch.decay;

/**
 *
 * @author fschmidt
 */
public class DynamicValueDecay implements DecayFunction {

    @Override
    public double getValue(int t, double... parameters) {
        return parameters[0];
    }
}
