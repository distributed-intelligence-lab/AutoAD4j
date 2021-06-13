package iftm.thresholdmodel;

/**
 *
 * @author fschmidt
 */
public class CompleteHistoryAvgStd extends AvgStdThresholdModel {

    private double currentS = 0;
    private int sampleCount = 0;

    public CompleteHistoryAvgStd(){
        super();
    }

    public CompleteHistoryAvgStd(double sigma){
        super(sigma);
    }

    public CompleteHistoryAvgStd(double sigma, CompleteHistoryAvgStd copy){
        super(sigma);
        this.currentS = copy.currentS;
        this.sampleCount = copy.sampleCount;
        this.avg = copy.avg;
        this.std = copy.std;
    }

    @Override
    public void addValue(double value) {
        sampleCount++;
        if (sampleCount == 1) {
            avg = value;
            std = 0.0;
            currentS = 0.0;
        } else {
            double prevAvg = avg;
            avg = (avg * ((double) sampleCount - 1.0) + value) / (double) sampleCount;
            currentS = currentS + (value - avg) * (value - prevAvg);
            std = Math.sqrt(currentS / ((double) sampleCount - 1.0));
        }
    }

    @Override
    public AvgStdThresholdModel newInstance() {
        return new CompleteHistoryAvgStd(this.sigmaFactor);
    }

    @Override
    public void clear() {
        super.clear();
        currentS = 0;
        sampleCount = 0;
    }
}
