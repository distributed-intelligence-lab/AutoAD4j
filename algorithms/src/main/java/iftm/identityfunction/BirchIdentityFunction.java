package iftm.identityfunction;

import iftm.identityfunction.cabirch.BIRCH;

import java.util.Map;

/**
 * @author Florian Schmidt
 */
public class BirchIdentityFunction extends AbstractBirchIdentityFunction {

    private final int branchingFactor;
    private final double initialThreshold;

    public BirchIdentityFunction(int branchingFactor, double initalThreshold) {
        super(new BIRCH(branchingFactor, initalThreshold));
        this.branchingFactor = branchingFactor;
        this.initialThreshold = initalThreshold;
    }

    @Override
    public IdentityFunction newInstance() {
        return new BirchIdentityFunction(branchingFactor, initialThreshold);
    }

    @Override
    public Map<String, Double> getStatistics() {
        Map<String, Double> stats = super.getStatistics();
        stats.put("identityfunction.hyperparameter.initialThreshold", initialThreshold);
        stats.put("identityfunction.hyperparameter.branchingFactor", (double) branchingFactor);
        return stats;
    }
}
