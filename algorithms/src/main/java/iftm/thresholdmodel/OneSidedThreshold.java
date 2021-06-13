package iftm.thresholdmodel;

/**
 *
 * @author fschmidt
 */
public abstract class OneSidedThreshold implements ThresholdModel {

    public abstract double getThreshold();

    public abstract void clear();

    @Override
    public boolean isIncluded(double error) {
        return error <= getThreshold();
    }
}
