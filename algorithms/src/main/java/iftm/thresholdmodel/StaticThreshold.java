package iftm.thresholdmodel;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author fschmidt
 */
public class StaticThreshold extends OneSidedThreshold {

    private final double staticThresholdValue;

    public StaticThreshold(double staticThresholdValue) {
        this.staticThresholdValue = staticThresholdValue;
    }

    @Override
    public double getThreshold() {
        return staticThresholdValue;
    }

    @Override
    public void addValue(double value) {
        //Method does not use input value, as it uses the staticly defined threshold, which is not based on the input value.
    }

    @Override
    public ThresholdModel newInstance() {
        return new StaticThreshold(getThreshold());
    }

    @Override
    public Map<String, Double> getStatistics() {
        Map<String, Double> stats = new HashMap<>();
        stats.put("threshold.hyperparameter.staticThresholdValue", staticThresholdValue);
        return stats;
    }

    @Override
    public void clear() {
    }

}
