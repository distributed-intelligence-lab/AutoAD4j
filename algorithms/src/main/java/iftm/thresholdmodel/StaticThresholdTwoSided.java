package iftm.thresholdmodel;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author fschmidt
 */
public class StaticThresholdTwoSided extends TwoSidedThreshold {

    private final double staticLowerBound;
    private final double staticUpperBound;

    public StaticThresholdTwoSided(double staticLowerBound, double staticUpperBound) {
        this.staticLowerBound = staticLowerBound;
        this.staticUpperBound = staticUpperBound;
    }

    @Override
    public double getUpperThreshold() {
        return staticUpperBound;
    }

    @Override
    public double getLowerThreshold() {
        return staticLowerBound;
    }

    @Override
    public void addValue(double value) {
        //The value does not influence the user set thresholds.
    }

    @Override
    public ThresholdModel newInstance() {
        return new StaticThresholdTwoSided(getLowerThreshold(), getUpperThreshold());
    }

    @Override
    public Map<String, Double> getStatistics() {
        Map<String, Double> stats = new HashMap<>();
        stats.put("threshold.hyperparameter.staticLowerBound", staticLowerBound);
        stats.put("threshold.hyperparameter.staticUpperBound", staticUpperBound);
        return stats;
    }

}
