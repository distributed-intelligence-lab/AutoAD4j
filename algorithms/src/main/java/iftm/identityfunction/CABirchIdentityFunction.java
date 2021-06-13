package iftm.identityfunction;

import iftm.identityfunction.cabirch.ConceptAdaptingBIRCH;
import iftm.identityfunction.cabirch.decay.DecayFunction;
import iftm.identityfunction.cabirch.decay.DynamicLogisticDecay;
import iftm.identityfunction.cabirch.decay.StaticLogisticDecay;

import java.util.Map;

/**
 * @author Florian Schmidt
 */
public class CABirchIdentityFunction extends AbstractBirchIdentityFunction {

    private final int branchingFactor;
    private final double initialThreshold;
    private final DecayFunction decay;

    public CABirchIdentityFunction(int maxNodeEntries, double initalThreshold, DecayFunction decay) {
        super(new ConceptAdaptingBIRCH(decay, maxNodeEntries, initalThreshold));
        this.branchingFactor = maxNodeEntries;
        this.initialThreshold = initalThreshold;
        this.decay = decay;
    }

    public CABirchIdentityFunction(){
        super();
        this.branchingFactor = 20;
        this.initialThreshold = 0.0;
        this.decay = new StaticLogisticDecay(1,0.01);
    }

    @Override
    public IdentityFunction newInstance() {
        return new CABirchIdentityFunction(branchingFactor, initialThreshold, decay);
    }

    @Override
    public Map<String, Double> getStatistics() {
        Map<String, Double> stats = super.getStatistics();
        stats.put("identityfunction.hyperparameter.initialThreshold", initialThreshold);
        stats.put("identityfunction.hyperparameter.branchingFactor", (double) branchingFactor);
        String decayName = String.valueOf(decay.getClass().getSimpleName());
        switch (decayName) {
            case "StaticLogisticDecay":
                stats.put("identityfunction.hyperparameter.decay.maxValue", ((StaticLogisticDecay) decay).getMaximumValue());
                stats.put("identityfunction.hyperparameter.decay.growthRate", ((StaticLogisticDecay) decay).getGrowthRate());
                break;
            case "DynamicLogisticDecay":
                stats.put("identityfunction.hyperparameter.decay.growthRate", ((DynamicLogisticDecay) decay).getGrowthRate());
                break;
            default:
                break;
        }
        return stats;
    }
}
