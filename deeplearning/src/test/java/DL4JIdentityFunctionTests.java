import iftm.identityfunction.*;
import org.junit.Assert;
import org.junit.Test;

public class DL4JIdentityFunctionTests {

    @Test
    public void testLSTM(){
        LSTMIdentityFunction model = new LSTMIdentityFunction(1,0.001);
        runTimeSeries(model);
    }

    @Test
    public void testLSTM2(){
        LSTMIdentityFunction model = new LSTMIdentityFunction(2,0.001);
        runTimeSeries(model);
    }

    @Test
    public void testLSTM3(){
        LSTMIdentityFunction model = new LSTMIdentityFunction(3,0.001);
        runTimeSeries(model);
    }

    @Test
    public void testLSTM4(){
        LSTMIdentityFunction model = new LSTMIdentityFunction(4,0.001);
        runTimeSeries(model);
    }

    @Test
    public void testAE(){
        AutoencoderIdentityFunction model = new AutoencoderIdentityFunction(3,0.001);
        runDistributions(model);
    }

    @Test
    public void testAELSTMDist(){
        AutoencoderLSTMIdentityFunction model = new AutoencoderLSTMIdentityFunction(2,0.001);
        runDistributions(model);
    }

    @Test
    public void testAELSTMTime(){
        AutoencoderLSTMIdentityFunction model = new AutoencoderLSTMIdentityFunction(2,0.001);
        runTimeSeries(model);
    }

    @Test
    public void testVAE(){
        VariationalAutoencoderIdentityFunction model = new VariationalAutoencoderIdentityFunction(1,0.001);
        runDistributions(model);
    }

    private void runTimeSeries(IdentityFunction model){
        for(int j = 0; j < 20; j++) {
            for (int i = 0; i < 100; i++) {
                double[] point = new double[]{i, 0.1 * i,10,5};
                model.train(point);
            }
        }
        double[] point = new double[]{100,10,10,5};
        Assert.assertArrayEquals(point, model.predict(point), 2.0);
    }

    private void runDistributions(IdentityFunction model){
        for (int i = 0; i < 1000; i++) {
            double[] point = new double[]{Math.random()*0.1, Math.random()*0.1+1,2,0};
            if(Math.random()<0.5){
                point = new double[]{Math.random()*0.1+1, Math.random()*0.1,0,2};
            }

            model.train(point);
        }
        double[] point1 = new double[]{Math.random()*0.1, Math.random()*0.1+1,2,0};
        Assert.assertArrayEquals(point1, model.predict(point1), 0.5);
        double[] point2 = new double[]{Math.random()*0.1+1, Math.random()*0.1,0,2};
        Assert.assertArrayEquals(point2, model.predict(point2), 0.5);
    }
}
