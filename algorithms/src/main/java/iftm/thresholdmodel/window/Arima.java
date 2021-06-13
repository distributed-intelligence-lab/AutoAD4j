package iftm.thresholdmodel.window;

import iftm.identityfunction.onlinearima.EfficientModelONS;
import iftm.thresholdmodel.ThresholdModel;

public class Arima extends BatchWindowThreshold {
    
    private final double percentageBelow; // How much below arima is Anomaly?
    private final EfficientModelONS model;
    private final int arimaWindow;
    private final int differentiation;
    
    public Arima(int windowSize, int arimaWindow, int differentiation, double percentageBelow) {
        super(windowSize);
        this.percentageBelow = percentageBelow;
        model = new EfficientModelONS(arimaWindow, differentiation); // alle benutzen maximal 5. Typisch ist 3.
        this.arimaWindow = arimaWindow;
        this.differentiation = differentiation;
    }
    
    @Override
    public double getThreshold() {
        return Math.max((1-percentageBelow) * (double) model.predict(),0);
    }
    
    @Override
    public void addValue(double value) {
        model.train(value);
    }
    
    @Override
    public ThresholdModel newInstance() {
        return new Arima(getWindowSize(), arimaWindow, differentiation, percentageBelow);
    }
    
}
