import iftm.aggregationfunction.AtLeastOneAnomalyAggregation;
import iftm.anomalydetection.AnomalyDetection;
import iftm.anomalydetection.DistancePredictionResult;
import iftm.anomalydetection.IFTMAnomalyDetection;
import iftm.anomalydetection.SingleLeadAnomalyDetection;
import iftm.errorfunction.EuclideanError;
import iftm.identityfunction.*;
import iftm.thresholdmodel.CompleteHistoryAvgStd;
import org.junit.Assert;
import org.junit.Test;

public class BasicIdentityFunctionTest {

    @Test
    public void testLSTM() {
        Exception ex = null;
        try {
            AnomalyDetection detector = new IFTMAnomalyDetection(new LSTMIdentityFunction(), new EuclideanError(), new CompleteHistoryAvgStd(2.0));
            runDetector(detector);
            AnomalyDetection singleDetector = new SingleLeadAnomalyDetection(new LSTMIdentityFunction(), new EuclideanError(), new CompleteHistoryAvgStd(2.0), new AtLeastOneAnomalyAggregation());
            runDetector(singleDetector);
        } catch (Exception e) {
            ex = e;
        }
        Assert.assertEquals(null,ex);
    }

    @Test
    public void testAutoencoder() {
        Exception ex = null;
        try {
            AnomalyDetection detector = new IFTMAnomalyDetection(new AutoencoderIdentityFunction(), new EuclideanError(), new CompleteHistoryAvgStd(2.0));
            runDetector(detector);
            AnomalyDetection singleDetector = new SingleLeadAnomalyDetection(new AutoencoderIdentityFunction(), new EuclideanError(), new CompleteHistoryAvgStd(2.0), new AtLeastOneAnomalyAggregation());
            runDetector(singleDetector);
        } catch (Exception e) {
            ex = e;
        }
        Assert.assertEquals(null,ex);
    }

    @Test
    public void testVariationalAutoencoder() {
        Exception ex = null;
        try {
            AnomalyDetection detector = new IFTMAnomalyDetection(new VariationalAutoencoderIdentityFunction(1,0.01), new EuclideanError(), new CompleteHistoryAvgStd(2.0));
            runDetector(detector);
            AnomalyDetection singleDetector = new SingleLeadAnomalyDetection(new VariationalAutoencoderIdentityFunction(1,0.01), new EuclideanError(), new CompleteHistoryAvgStd(2.0), new AtLeastOneAnomalyAggregation());
            runDetector(singleDetector);
        } catch (Exception e) {
            ex = e;
        }
        Assert.assertEquals(null,ex);
    }

    @Test
    public void testAutoencoderLSTM() {
        Exception ex = null;
        try {
            AnomalyDetection detector = new IFTMAnomalyDetection(new AutoencoderLSTMIdentityFunction(), new EuclideanError(), new CompleteHistoryAvgStd(2.0));
            runDetector(detector);
            AnomalyDetection singleDetector = new SingleLeadAnomalyDetection(new AutoencoderLSTMIdentityFunction(), new EuclideanError(), new CompleteHistoryAvgStd(2.0), new AtLeastOneAnomalyAggregation());
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
