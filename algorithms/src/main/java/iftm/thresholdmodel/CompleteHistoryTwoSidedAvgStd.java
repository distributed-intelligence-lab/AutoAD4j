package iftm.thresholdmodel;

/**
 *
 * @author fschmidt
 */
public class CompleteHistoryTwoSidedAvgStd extends TwoSidedAvgStdThresholdModel {

    private double currentS = 0;
    private int sampleCount = 0;

    public CompleteHistoryTwoSidedAvgStd(double sigmaFactor){
        super(sigmaFactor);
    }

    public CompleteHistoryTwoSidedAvgStd(){
        super();
    }

    @Override
    public void addValue(double value) {
        sampleCount++;
        double prevAvg = avg;
        avg = (avg * (sampleCount - 1) + value) / (double) sampleCount;
        currentS = currentS + (value - avg) * (value - prevAvg);
        std = Math.sqrt(currentS / (double) sampleCount);
    }

    @Override
    public ThresholdModel newInstance() {
        return new CompleteHistoryTwoSidedAvgStd(getSigmaFactor());
    }

}
