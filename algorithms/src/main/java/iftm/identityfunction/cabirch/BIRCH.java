package iftm.identityfunction.cabirch;

import java.io.Serializable;

/**
 *
 * @author fschmidt
 */
public class BIRCH implements Serializable {

    private final CFTree cfTree;


    public BIRCH(int maxNodeEntries, double initialThreshold) {
        cfTree = new CFTree(maxNodeEntries, initialThreshold, true);
    }

    public BIRCH(){
        cfTree = new CFTree(20, 0.0, true);
    }

    public void train(double[] values) {
        cfTree.insertEntry(values);
    }

    public int cluster(double[] point) {
        cfTree.finishedInsertingData();
        return cfTree.cluster(point);
    }

    public double[] getClosestBorderPoint(double[] point) {
        return cfTree.getBorderPoint(point);
    }
    public double[] getOldClosestBorderPoint(double[] point) {
        return cfTree.getOldClosestBorderPoint(point);
    }

    public double[] getVectorFromBorder(double[] point) {
        return cfTree.getVectorFromBorder(point);
    }

    public double[] getClosestBorderDistance(double[] point) {
        return cfTree.getDistance(point);
    }

    public CFTree getCfTree() {
        return cfTree;
    }
}
