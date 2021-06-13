package iftm.identityfunction.cabirch;

import iftm.identityfunction.cabirch.decay.DecayFunction;

/**
 *
 * @author fschmidt
 */
public class ConceptAdaptingBIRCH extends BIRCH {

    public ConceptAdaptingBIRCH(){}

    public ConceptAdaptingBIRCH(DecayFunction decayFunction, int maxNodeEntries, double initialThreshold) {
        super(maxNodeEntries, initialThreshold);
        getCfTree().useDecay(decayFunction);
    }
}
