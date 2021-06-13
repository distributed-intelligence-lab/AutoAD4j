package iftm.thresholdmodel;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Holt-Winter method
 *
 * @author fschmidt
 */
public class TripleEMAvgStd extends AvgStdThresholdModel {

    private double level;
    private double trend;
    private final Queue<Double> seasonal;
    private final double levelSmoothing;
    private final double trendSmoothing;
    private final double seasonalSmoothing;
    private double var;
    private final double seasonLength;

    public TripleEMAvgStd(double levelSmoothing, double trendSmoothing, double seasonalSmoothing, double seasonLength) {
        avg = 0.0;
        std = 0.0;
        level = 0.0;
        trend = 0.0;
        var = 0.0;
        this.levelSmoothing = levelSmoothing;
        this.trendSmoothing = trendSmoothing;
        this.seasonalSmoothing = seasonalSmoothing;
        this.seasonLength = seasonLength;
        seasonal = new LinkedList<>();
        seasonal.add(0.0);
    }

    @Override
    public void addValue(double value) {
        if (seasonal.size() > seasonLength) {
            seasonal.remove();
        }
        double oldLevel = level;
        level = levelSmoothing * (value - seasonal.peek()) + (1 - levelSmoothing) * (level + trend);
        trend = trendSmoothing * (level - oldLevel) + (1 - trendSmoothing) * trend;
        double newSeasonal = seasonalSmoothing * (value - level) + (1 - seasonalSmoothing) * seasonal.peek();
        seasonal.add(newSeasonal);
        avg = level + trend + seasonal.poll();

        double diffMean = value - avg;
        double increment = trendSmoothing * diffMean;
        var = (1 - trendSmoothing) * (var + diffMean * increment);
        std = Math.sqrt(var);
    }

    @Override
    public AvgStdThresholdModel newInstance() {
        return new TripleEMAvgStd(levelSmoothing, trendSmoothing, seasonalSmoothing, seasonLength);
    }

    @Override
    public void clear() {
        super.clear();
        level = 0.0;
        trend = 0.0;
        var = 0.0;
        seasonal.clear();
        seasonal.add(0.0);
    }

}
