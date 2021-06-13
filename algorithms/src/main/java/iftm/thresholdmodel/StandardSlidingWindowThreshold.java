package iftm.thresholdmodel;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author fschmidt
 */
public class StandardSlidingWindowThreshold extends AvgStdThresholdModel {

    private final DescriptiveStatistics stats;
    private final int windowSize;

    public StandardSlidingWindowThreshold(int windowSize) {
        this(windowSize, 1.0);
    }

    public StandardSlidingWindowThreshold(int windowSize, double sigma) {
        super(sigma);
        this.windowSize = windowSize;
        stats = new DescriptiveStatistics(windowSize);
    }

    @Override
    public void addValue(double value) {
        stats.addValue(value);
        this.avg = stats.getMean();
        this.std = stats.getStandardDeviation();
    }

    @Override
    public AvgStdThresholdModel newInstance() {
        return new StandardSlidingWindowThreshold(windowSize, getSigmaFactor());
    }

    public int getWindowSize() {
        return windowSize;
    }

    @Override
    public void clear() {
        super.clear();
        stats.clear();
    }
}
