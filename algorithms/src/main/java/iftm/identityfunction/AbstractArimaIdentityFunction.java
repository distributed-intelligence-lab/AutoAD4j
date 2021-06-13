package iftm.identityfunction;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractArimaIdentityFunction implements IdentityFunction {

    private final int linearComponents;
    private final int integralDepth;

    public AbstractArimaIdentityFunction(int mk, int d) {
        this.integralDepth = d;
        this.linearComponents = mk;
    }

    public int getLinearComponents() {
        return linearComponents;
    }

    public int getIntegralDepth() {
        return integralDepth;
    }

    @Override
    public Map<String, Double> getStatistics() {
        Map<String, Double> stats = new HashMap<>();
        stats.put("identityfunction.hyperparameter.D", (double) integralDepth);
        stats.put("identityfunction.hyperparameter.Mk", (double) linearComponents);
        return stats;
    }
}
