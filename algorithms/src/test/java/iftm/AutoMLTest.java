package iftm;

import iftm.anomalydetection.AutoMLAnomalyDetection;
import iftm.anomalydetection.DistancePredictionResult;
import iftm.automl.thresholdmodel.CompleteHistoryThresholdBreedingPart;
import iftm.automl.thresholdmodel.ExponentialMovingThresholdBreedingPart;
import iftm.automl.identitifunction.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class AutoMLTest {
    private final Random random = new Random(123);
    @Test
    public void testCABIRCH(){
        AutoMLAnomalyDetection detector = new AutoMLAnomalyDetection(new CABIRCHBreeding(10, 20, 2.0, 0.01, new ExponentialMovingThresholdBreedingPart(5.0, 0.01)), 100, false);
        runTest(detector);
    }

    @Test
    public void testCABIRCH2(){
        AutoMLAnomalyDetection detector = new AutoMLAnomalyDetection(new CABIRCHBreeding(10, 20, 2.0, 0.01, new ExponentialMovingThresholdBreedingPart(5.0, 0.01)), 100, false);
        for (int i = 0; i < 1000; i++) {
            detector.train(new double[]{1.0, 0.0, 0.0});
            detector.train(new double[]{0.0, 0.0, 0.0});
        }
        DistancePredictionResult result = detector.predict(new double[]{1.0, 2.0, 3.0});
        Assert.assertTrue(result.isAnomaly());
    }

    @Test
    public void testCABIRCHOld() {
        AutoMLAnomalyDetection detector = new AutoMLAnomalyDetection(new CABIRCHBreeding(10, 20, 2.0, 0.01, new CompleteHistoryThresholdBreedingPart(5.0)), 100, false);
        for(int i = 0; i < 1000;i++) {
            detector.train(new double[]{1.0, 0.0, 0.0});
            detector.train(new double[]{0.0, 0.0, 0.0});


        }
        DistancePredictionResult result = detector.predict(new double[]{1.0, 2.0, 3.0});
        Assert.assertTrue(result.isAnomaly());
    }

    @Test
    public void testArimaMulti(){
        AutoMLAnomalyDetection detector = new AutoMLAnomalyDetection(new ArimaMultiBreeding(10, 4, 10, new CompleteHistoryThresholdBreedingPart(5.0)), 100, false);
        runTest(detector);
    }

    @Test
    public void testArimaSingle(){
        AutoMLAnomalyDetection detector = new AutoMLAnomalyDetection(new ArimaSingleiBreeding(10, 4, 10, new CompleteHistoryThresholdBreedingPart(5.0)), 100, false);
        runTest(detector);
    }

    @Test
    public void testBIRCH(){
        AutoMLAnomalyDetection detector = new AutoMLAnomalyDetection(new BIRCHBreeding(10, 20, 5.0), 100, false);
        runTest(detector);
    }

    @Test
    public void testStreamingHSTrees(){
        AutoMLAnomalyDetection detector = new AutoMLAnomalyDetection(new StreamingHSTreesBreeding(10, 15, 10, 100, 1.0,5.0), 100, false);
        runTest(detector);
    }

    private void runTest(AutoMLAnomalyDetection detector){
        Exception ex = null;
        try {
            for(int i = 0; i < 1000;i++) {
                detector.predict(new double[]{100 * random.nextDouble(), 100 * random.nextDouble(), 100 * random.nextDouble()});
                detector.train(new double[]{100 * random.nextDouble(), 100 * random.nextDouble(), 100 * random.nextDouble()});
            }
        } catch (Exception e) {
            ex = e;
        }
        Assert.assertNull(ex);
    }
}
