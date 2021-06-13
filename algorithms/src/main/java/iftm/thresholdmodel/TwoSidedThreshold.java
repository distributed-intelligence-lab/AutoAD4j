package iftm.thresholdmodel;

/**
 *
 * @author fschmidt
 */
public abstract class TwoSidedThreshold implements ThresholdModel {

    public abstract double getUpperThreshold();

    public abstract double getLowerThreshold();

    @Override
    public boolean isIncluded(double error) {
        return getLowerThreshold() <= error && error <= getUpperThreshold();
    }

}
