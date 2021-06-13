package iftm;

import iftm.identityfunction.onlinearima.*;
import org.junit.Assert;
import org.junit.Test;

public class ArimaTests {

    @Test
    public void testODG() {
        ModelODG model = new ModelODG(2,2);
        runModel(model);
    }

    @Test
    public void testONS() {
        ModelONS model = new ModelONS(2,2);
        runModel(model);
    }

    @Test
    public void testEfficientONS() {
        EfficientModelONS model = new EfficientModelONS(2,2);
        runModel(model);
    }

    @Test
    public void testEfficientONSmulti() {
        EfficientModelONSmulti model = new EfficientModelONSmulti(2,2, 2);
        runModel(model);
    }

    @Test
    public void testEfficientONSmultiSimple() {
        EfficientModelONSmulti model = new EfficientModelONSmulti(2,2, 2);
        runModel(model);
    }

    private void runModel(AbstractModelArima model){
        for(int i = 0; i < 10; i++){
            model.predict();
            double point = i;
            model.train(point);
        }
        double point = 10;
        Assert.assertEquals(point, (double) model.predict(), 0.0001);
    }

    private void runModel(EfficientModelONSmulti model){
        for(int i = 0; i < 10; i++){
            model.predict();
            double[] point = new double[]{i,0.1*i};
            model.train(point);
        }
        double[] point = new double[]{10,1.0};
        Assert.assertArrayEquals(point, model.predict(), 0.0001);
    }

}
