import iftm.anomalydetection.AutoMLAnomalyDetection;
import iftm.automl.identityfunctions.*;
import iftm.automl.identityfunctions.AutoencoderBreeding;
import iftm.automl.identityfunctions.AutoencoderLSTMBreeding;
import iftm.automl.identityfunctions.LSTMBreeding;
import iftm.automl.identityfunctions.VariationalAutoencoderBreeding;
import iftm.automl.thresholdmodel.CompleteHistoryThresholdBreedingPart;
import org.junit.Assert;
import org.junit.Test;

public class AutoMLTest {

    @Test
    public void testLSTM(){
        AutoMLAnomalyDetection detector = new AutoMLAnomalyDetection(new LSTMBreeding(10, 5, new CompleteHistoryThresholdBreedingPart(5.0)), 100, false);
        runTest(detector);
    }

    @Test
    public void testAE(){
        AutoMLAnomalyDetection detector = new AutoMLAnomalyDetection(new AutoencoderBreeding(10, 5, new CompleteHistoryThresholdBreedingPart(5.0)), 100, false);
        runTest(detector);
    }

    @Test
    public void testVAE(){
        AutoMLAnomalyDetection detector = new AutoMLAnomalyDetection(new VariationalAutoencoderBreeding(10, 5, new CompleteHistoryThresholdBreedingPart(5.0)), 100, false);
        runTest(detector);
    }

    @Test
    public void testAELSTM(){
        AutoMLAnomalyDetection detector = new AutoMLAnomalyDetection(new AutoencoderLSTMBreeding(10, 5, new CompleteHistoryThresholdBreedingPart(5.0)), 100, false);
        runTest(detector);
    }

    private void runTest(AutoMLAnomalyDetection detector){
        Exception ex = null;
        try {
            for(int i = 0; i < 1000;i++) {
                detector.predict(new double[]{100*Math.random(), 100*Math.random(), 100*Math.random()});
                detector.train(new double[]{100*Math.random(), 100*Math.random(), 100*Math.random()});
            }
        } catch (Exception e) {
            ex = e;
        }
        Assert.assertNull(ex);
    }
}
