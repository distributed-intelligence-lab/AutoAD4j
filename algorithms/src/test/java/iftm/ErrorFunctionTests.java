package iftm;

import iftm.errorfunction.*;
import org.junit.Assert;
import org.junit.Test;

public class ErrorFunctionTests {

    @Test
    public void testEuclidean(){
        EuclideanError error = new EuclideanError();
        double result = error.calc(new double[]{1.0,2.0}, new double[]{3.0,2.0});
        Assert.assertEquals(2.0, result, 0.000001);
    }

    @Test
    public void testL2Result(){
        L2NormModelResultError error = new L2NormModelResultError();
        double result = error.calc(new double[]{7.0,2.0}, new double[]{3.0,4.0});
        Assert.assertEquals(5.0, result, 0.000001);
    }

    @Test
    public void testMeanSquaredError(){
        MeanSquaredError error = new MeanSquaredError();
        double result = error.calc(new double[]{1.0,1.0}, new double[]{3.0,3.0});
        Assert.assertEquals(4.0, result, 0.000001);
    }

    @Test
    public void testAbsoluteError(){
        AbsolutError error = new AbsolutError();
        double result = error.calc(new double[]{1.0,1.0}, new double[]{0.0,3.0});
        Assert.assertEquals(3.0, result, 0.000001);
    }

    @Test
    public void testCanberraError(){
        CanberraError error = new CanberraError();
        double result = error.calc(new double[]{0.0,1.0}, new double[]{5.0,0.0});
        Assert.assertEquals(2.0, result, 0.000001);
    }

    @Test
    public void testChebyshevError(){
        ChebyshevError error = new ChebyshevError();
        double result = error.calc(new double[]{1.0,1.0}, new double[]{0.0,3.0});
        Assert.assertEquals(2.0, result, 0.000001);
    }

    @Test
    public void testEarthMovers(){
        EarthMoversError error = new EarthMoversError();
        double result = error.calc(new double[]{10.0,1.0}, new double[]{0.0,3.0});
        Assert.assertEquals(18.0, result, 0.000001);
    }

    @Test
    public void testManhattan(){
        ManhattanError error = new ManhattanError();
        double result = error.calc(new double[]{1.0,1.0}, new double[]{0.0,3.0});
        Assert.assertEquals(3.0, result, 0.000001);
    }

    @Test
    public void testRootMeanSquaredError(){
        RootMeanSquaredError error = new RootMeanSquaredError();
        double result = error.calc(new double[]{1.0,1.0}, new double[]{5.0,5.0});
        Assert.assertEquals(4.0, result, 0.000001);
    }

}
