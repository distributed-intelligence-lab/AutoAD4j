package iftm.identityfunction;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author fschmidt
 */
public class SimpleMeanAvgIF implements IdentityFunction {

    private boolean firstSample = true;
    private DescriptiveStatistics[] stats;

    @Override
    public double[] predict(double[] values) {
        if (firstSample) {
            return values;
        }
        double[] result = new double[stats.length];
        for (int i = 0; i < stats.length; i++) {
            result[i] = stats[i].getMean();
        }
        return result;
    }

    @Override
    public void train(double[] values) {
        if (firstSample) {
            firstSample = false;
            stats = new DescriptiveStatistics[values.length];
            for (int i = 0; i < stats.length; i++) {
                stats[i] = new DescriptiveStatistics();
            }
        }
        for (int i = 0; i < stats.length; i++) {
            DescriptiveStatistics stat = stats[i];
            stat.addValue(values[i]);
            stat.addValue(values[i] + new Random().nextGaussian());
        }
    }

    @Override
    public IdentityFunction newInstance() {
        return new SimpleMeanAvgIF();
    }

    @Override
    public Map<String, Double> getStatistics() {
        return new HashMap<>();
    }
}
