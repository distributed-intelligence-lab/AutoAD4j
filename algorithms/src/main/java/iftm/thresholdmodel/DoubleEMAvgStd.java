package iftm.thresholdmodel;

/**
 *
 * @author fschmidt
 */
public class DoubleEMAvgStd extends AvgStdThresholdModel {

    private double level;
    private double trend;
    private final double levelSmoothing;
    private final double trendSmoothing;
    private double var;
    
    public DoubleEMAvgStd(double levelSmoothing, double trendSmoothing) {
        avg = 0.0;
        std = 0.0;
        level = 0.0;
        trend = 0.0;
        var = 0.0;
        this.levelSmoothing = levelSmoothing;
        this.trendSmoothing = trendSmoothing;
    }

    @Override
    public void addValue(double value) {
        double oldLevel = level;
        level = levelSmoothing * value + (1 - levelSmoothing) * (trend + level);
        double levelDiff = level - oldLevel;
        trend = trendSmoothing * levelDiff + (1 - trendSmoothing) * trend;
        avg = trend + level;
        
        double diffMean = value - avg;
        double increment = trendSmoothing * diffMean;
        var = (1 - trendSmoothing) * (var + diffMean * increment);
        std = Math.sqrt(var);
    }

    @Override
    public AvgStdThresholdModel newInstance() {
        return new DoubleEMAvgStd(levelSmoothing, trendSmoothing);
    }

    public double getLevelSmoothing() {
        return levelSmoothing;
    }

    public double getTrendSmoothing() {
        return trendSmoothing;
    }

    @Override
    public void clear() {
        super.clear();
        level = 0;
        trend = 0;
        var = 0;
    }
}
