package iftm;

import iftm.errorfunction.EuclideanError;
import iftm.errorfunction.L2NormModelResultError;
import iftm.identityfunction.cabirch.BIRCH;
import iftm.identityfunction.cabirch.ConceptAdaptingBIRCH;
import iftm.identityfunction.cabirch.decay.StaticDecay;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

public class CABIRCHTests {

    private BIRCH birch;
    private ConceptAdaptingBIRCH cabirchSame;
    private ConceptAdaptingBIRCH cabirchDiff;
    private final Random randomGenerator = new Random(123);

    @Before
    public void init(){
        birch = new BIRCH(10,0.0);
        cabirchSame = new ConceptAdaptingBIRCH(new StaticDecay(0.0), 10, 0.0);
        cabirchDiff = new ConceptAdaptingBIRCH(new StaticDecay(0.1), 10, 0.0);
        for(int i = 0; i < 100000;i++){
            double[] point = new double[]{randomGenerator.nextDouble(), randomGenerator.nextDouble(), randomGenerator.nextDouble()};
            birch.train(Arrays.copyOf(point,point.length));
            cabirchSame.train(Arrays.copyOf(point,point.length));
            cabirchDiff.train(Arrays.copyOf(point,point.length));
        }
    }

    @Test
    public void testCABIRCHSame(){
        testSame(birch, cabirchSame);
    }

    @Test
    public void testCABIRCHDiff() {
        testDiff(cabirchSame, cabirchDiff);
    }

    private void testSame(BIRCH model1, BIRCH model2){
        double[] point = new double[]{randomGenerator.nextDouble(), randomGenerator.nextDouble(), randomGenerator.nextDouble()};
        Assert.assertArrayEquals(model1.getClosestBorderDistance(point),model2.getClosestBorderDistance(point), 0.0);
        Assert.assertArrayEquals(model1.getClosestBorderPoint(point),model2.getClosestBorderPoint(point), 0.0);
    }

    private void testDiff(BIRCH model1, BIRCH model2){
        double[] point = new double[]{10 + randomGenerator.nextDouble(), 10 + randomGenerator.nextDouble(), 10 + randomGenerator.nextDouble()};
        double[] borderDist1 = model1.getClosestBorderDistance(point);
        double[] dist1 = model1.getClosestBorderDistance(point);
        double[] borderDist2 = model2.getClosestBorderDistance(point);
        double[] dist2 = model2.getClosestBorderDistance(point);
        for(int i =0; i< point.length;i++){
            Assert.assertNotEquals(borderDist1[i], borderDist2[i], 0.0);
            Assert.assertNotEquals(dist1[i], dist2[i], 0.0);
        }
    }

    @Test
    public void testBoarderDistance() {
        ConceptAdaptingBIRCH cabirch = new ConceptAdaptingBIRCH(new StaticDecay(0), 4, 0);
        cabirch.train(new double[]{1.0, 1.0});
        cabirch.train(new double[]{1.5, 1.5});
        cabirch.train(new double[]{1.4, 1.4});
        cabirch.train(new double[]{1.3, 1.3});
        cabirch.train(new double[]{0, 1.1});

        double[] boarderPoint = cabirch.getVectorFromBorder(new double[]{2.0, 2.0});
        double value = new L2NormModelResultError().calc(new double[]{2.0, 2.0}, boarderPoint);

        double[] boarderPoint2 = cabirch.getClosestBorderPoint(new double[]{2.0, 2.0});
        double value2 = new EuclideanError().calc(new double[]{2.0, 2.0}, boarderPoint2);

        Assert.assertArrayEquals(new double[]{0.5, 0.5}, boarderPoint, 0.0001);
        Assert.assertArrayEquals(new double[]{1.5, 1.5}, boarderPoint2, 0.0001);
        Assert.assertEquals(value, value2, 0.0001);
    }

}
