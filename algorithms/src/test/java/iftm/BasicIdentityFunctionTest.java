package iftm;

import iftm.aggregationfunction.AtLeastOneAnomalyAggregation;
import iftm.anomalydetection.AnomalyDetection;
import iftm.anomalydetection.DistancePredictionResult;
import iftm.anomalydetection.IFTMAnomalyDetection;
import iftm.anomalydetection.SingleLeadAnomalyDetection;
import iftm.errorfunction.EuclideanError;
import iftm.errorfunction.L2NormModelResultError;
import iftm.identityfunction.*;
import iftm.identityfunction.cabirch.decay.StaticLogisticDecay;
import iftm.thresholdmodel.CompleteHistoryAvgStd;
import iftm.thresholdmodel.window.ExponentialStandardDeviation;
import org.junit.Assert;
import org.junit.Test;

public class BasicIdentityFunctionTest {

    @Test
    public void testBIRCH() {
        Exception ex = null;
        try {
            AnomalyDetection detector = new IFTMAnomalyDetection(new BirchIdentityFunction(10,0.0), new EuclideanError(), new CompleteHistoryAvgStd(2.0));
            runDetector(detector);
            AnomalyDetection singleDetector = new SingleLeadAnomalyDetection(new BirchIdentityFunction(10,0.0), new EuclideanError(), new CompleteHistoryAvgStd(2.0), new AtLeastOneAnomalyAggregation());
            runDetector(singleDetector);
        } catch (Exception e) {
            ex = e;
        }
        Assert.assertEquals(null,ex);
    }

    @Test
    public void testOnlineArima() {
        Exception ex = null;
        try {
            AnomalyDetection detector = new IFTMAnomalyDetection(new OnlineArimaIdentityFunction(2,2), new EuclideanError(), new CompleteHistoryAvgStd(2.0));
            runDetector(detector);
            AnomalyDetection singleDetector = new SingleLeadAnomalyDetection(new OnlineArimaIdentityFunction(2,2), new EuclideanError(), new CompleteHistoryAvgStd(2.0), new AtLeastOneAnomalyAggregation());
            runDetector(singleDetector);
        } catch (Exception e) {
            ex = e;
        }
        Assert.assertEquals(null,ex);
    }

    @Test
    public void testMultiOnlineArima() {
        Exception ex = null;
        try {
            AnomalyDetection detector = new IFTMAnomalyDetection(new OnlineArimaMultiIdentityFunction(2,2), new EuclideanError(), new CompleteHistoryAvgStd(2.0));
            runDetector(detector);
            AnomalyDetection singleDetector = new SingleLeadAnomalyDetection(new OnlineArimaMultiIdentityFunction(2,2), new EuclideanError(), new CompleteHistoryAvgStd(2.0), new AtLeastOneAnomalyAggregation());
            runDetector(singleDetector);
        } catch (Exception e) {
            ex = e;
        }
        Assert.assertEquals(null,ex);
    }

    @Test
    public void testCABIRCH() {
        Exception ex = null;
        try {
            AnomalyDetection detector = new IFTMAnomalyDetection(new CABirchIdentityFunction(10,0.0, new StaticLogisticDecay(0.1,0.1)), new EuclideanError(), new CompleteHistoryAvgStd(2.0));
            runDetector(detector);
            AnomalyDetection singleDetector = new SingleLeadAnomalyDetection(new CABirchIdentityFunction(10,0.0, new StaticLogisticDecay(0.1,0.1)), new EuclideanError(), new CompleteHistoryAvgStd(2.0), new AtLeastOneAnomalyAggregation());
            runDetector(singleDetector);
        } catch (Exception e) {
            ex = e;
        }
        Assert.assertEquals(null,ex);
    }

    @Test
    public void testStreamingHSTrees() {
        Exception ex = null;
        try {
            AnomalyDetection detector = new IFTMAnomalyDetection(new StreamingHSTreesIdentityFunction(15, 5, 10), new L2NormModelResultError(), new ExponentialStandardDeviation(10, 0.14, 1.3));
            runDetector(detector);
        } catch (Exception e) {
            ex = e;
        }
        Assert.assertEquals(null,ex);
    }

    @Test
    public void testSimpleMeanAvg() {
        Exception ex = null;
        try {
            AnomalyDetection detector = new IFTMAnomalyDetection(new SimpleMeanAvgIF(), new EuclideanError(), new CompleteHistoryAvgStd(2.0));
            runDetector(detector);
            AnomalyDetection singleDetector = new SingleLeadAnomalyDetection(new SimpleMeanAvgIF(), new EuclideanError(), new CompleteHistoryAvgStd(2.0), new AtLeastOneAnomalyAggregation());
            runDetector(singleDetector);
        } catch (Exception e) {
            ex = e;
        }
        Assert.assertEquals(null,ex);
    }

    private void runDetector(AnomalyDetection detector){
        for(int i = 0; i< 100;i++){
            double[] point = new double[]{Math.random(), Math.random()+1,Math.random(),Math.random()+2};
            DistancePredictionResult result = detector.predict(point);
            detector.train(point);
        }
    }
}
