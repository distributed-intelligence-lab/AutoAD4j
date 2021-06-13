package iftm.thresholdmodel;

/**
 *
 * @author fschmidt
 */
public class ExponentialMovingAvgStd extends AvgStdThresholdModel {

    private final double alpha;
    private Double var = null;

    public ExponentialMovingAvgStd(){
        this(0.1);
    }

    public ExponentialMovingAvgStd(double alpha) {
        this.avg = 0.0;
        this.std = 0.0;
        this.alpha = alpha;
    }
    
    public ExponentialMovingAvgStd(double alpha, double sigma) {
        super(sigma);
        this.avg = 0.0;
        this.std = 0.0;
        this.alpha = alpha;
    }

    public ExponentialMovingAvgStd(double alpha, double sigma, double initalAvg) {
        super(sigma);
        this.avg = initalAvg;
        this.std = 0.0;
        this.alpha = alpha;
    }

    public ExponentialMovingAvgStd(double alpha, double sigma, ExponentialMovingAvgStd copy) {
        super(sigma);
        this.avg = copy.getAvg();
        this.std = copy.getStd();
        this.var = copy.getVar();
        this.alpha = alpha;
    }

    @Override
    public void addValue(double value) {
        if (var == null) {
            var = 0.0;
            avg = value;
        }
        double diffMean = value - avg;
        double increment = alpha * diffMean;
        avg = avg + increment;
        var = (1 - alpha) * (var + diffMean * increment);
        std = Math.sqrt(var);
    }

    @Override
    public AvgStdThresholdModel newInstance() {
        return new ExponentialMovingAvgStd(alpha);
    }

    public double getAlpha() {
        return alpha;
    }

    public Double getVar() {
        return var;
    }

    @Override
    public void clear() {
        super.clear();
        var = null;
    }
}
