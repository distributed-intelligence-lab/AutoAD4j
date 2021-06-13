package iftm;

import iftm.thresholdmodel.*;
import org.junit.Assert;
import org.junit.Test;

public class ThresholdModelTests {

    @Test
    public void testCompleteHistoryAvgStd(){
        CompleteHistoryAvgStd model = new CompleteHistoryAvgStd(0.0);
        model.addValue(2.0);
        model.addValue(1.0);
        Assert.assertEquals(1.5, model.getAvg(), 0.000001);
        Assert.assertEquals(1.5, model.getThreshold(), 0.000001);
        model.addValue(0.0);
        Assert.assertEquals(1.0, model.getAvg(), 0.000001);
        Assert.assertEquals(1.0, model.getThreshold(), 0.000001);
        AvgStdThresholdModel m2 = model.newInstance();
        m2.addValue(2.0);
        m2.addValue(1.0);
        Assert.assertEquals(1.5, m2.getAvg(), 0.000001);
        Assert.assertEquals(1.5, m2.getThreshold(), 0.000001);
        m2.addValue(0.0);
        Assert.assertEquals(1.0, m2.getAvg(), 0.000001);
        Assert.assertEquals(1.0, m2.getThreshold(), 0.000001);
    }

    @Test
    public void testCompleteHistoryAvgStdSigma1() {
        CompleteHistoryAvgStd model = new CompleteHistoryAvgStd(1.0);
        for (int i = 0; i < 1000; i++) {
            model.addValue(2.0);
            model.addValue(1.0);
        }
        Assert.assertEquals(1.5, model.getAvg(), 0.000001);
        Assert.assertEquals(2.0001250468945404, model.getThreshold(), 0.00001);
    }

    @Test
    public void testTwoSidedCompleteHistoryAvgStd(){
        CompleteHistoryTwoSidedAvgStd model = new CompleteHistoryTwoSidedAvgStd(0.0);
        model.addValue(4.0);
        model.addValue(2.0);
        Assert.assertEquals(3.0, model.getLowerThreshold(), 0.000001);
        Assert.assertEquals(3.0, model.getUpperThreshold(), 0.000001);
        model.addValue(0.0);
        Assert.assertEquals(2.0, model.getLowerThreshold(), 0.000001);
        Assert.assertEquals(2.0, model.getUpperThreshold(), 0.000001);
        model.newInstance();
        CompleteHistoryTwoSidedAvgStd m2 = (CompleteHistoryTwoSidedAvgStd) model.newInstance();
        m2.addValue(4.0);
        m2.addValue(2.0);
        Assert.assertEquals(3.0, m2.getLowerThreshold(), 0.000001);
        Assert.assertEquals(3.0, m2.getUpperThreshold(), 0.000001);
        m2.addValue(0.0);
        Assert.assertEquals(2.0, m2.getLowerThreshold(), 0.000001);
        Assert.assertEquals(2.0, m2.getUpperThreshold(), 0.000001);
    }

    @Test
    public void testStaticThreshold(){
        StaticThreshold model = new StaticThreshold(100.0);
        Assert.assertEquals(100.0, model.getThreshold(), 0.000001);
        model.addValue(0.0);
        Assert.assertEquals(100.0, model.getThreshold(), 0.000001);
        model.newInstance();
        StaticThreshold m2 = (StaticThreshold) model.newInstance();
        Assert.assertEquals(100.0, m2.getThreshold(), 0.000001);
        m2.addValue(0.0);
        Assert.assertEquals(100.0, m2.getThreshold(), 0.000001);
    }

    @Test
    public void testTwoSidedStaticThreshold(){
        StaticThresholdTwoSided model = new StaticThresholdTwoSided(1.0,100.0);
        Assert.assertEquals(1, model.getLowerThreshold(), 0.000001);
        Assert.assertEquals(100.0, model.getUpperThreshold(), 0.000001);
        model.addValue(0.0);
        Assert.assertEquals(1, model.getLowerThreshold(), 0.000001);
        Assert.assertEquals(100.0, model.getUpperThreshold(), 0.000001);
        StaticThresholdTwoSided m2 = (StaticThresholdTwoSided) model.newInstance();
        Assert.assertEquals(1, m2.getLowerThreshold(), 0.000001);
        Assert.assertEquals(100.0, m2.getUpperThreshold(), 0.000001);
        m2.addValue(0.0);
        Assert.assertEquals(1, m2.getLowerThreshold(), 0.000001);
        Assert.assertEquals(100.0, m2.getUpperThreshold(), 0.000001);
    }

    @Test
    public void testStandardSlidingWindow(){
        StandardSlidingWindowThreshold model = new StandardSlidingWindowThreshold(2,0.0);
        model.addValue(10.0);
        model.addValue(8.0);
        Assert.assertEquals(9.0, model.getAvg(), 0.000001);
        Assert.assertEquals(9.0, model.getThreshold(), 0.000001);
        model.addValue(0.0);
        Assert.assertEquals(4.0, model.getAvg(), 0.000001);
        Assert.assertEquals(4.0, model.getThreshold(), 0.000001);
        AvgStdThresholdModel m2 = model.newInstance();
        m2.addValue(10.0);
        m2.addValue(8.0);
        Assert.assertEquals(9.0, m2.getAvg(), 0.000001);
        Assert.assertEquals(9.0, m2.getThreshold(), 0.000001);
        m2.addValue(0.0);
        Assert.assertEquals(4.0, m2.getAvg(), 0.000001);
        Assert.assertEquals(4.0, m2.getThreshold(), 0.000001);
    }
}
