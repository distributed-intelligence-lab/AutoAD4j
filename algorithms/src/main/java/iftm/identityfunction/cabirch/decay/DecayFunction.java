package iftm.identityfunction.cabirch.decay;

import java.io.Serializable;

/**
 *
 * @author fschmidt
 */
public interface DecayFunction extends Serializable {

    double getValue(int t, double... parameters);
}
